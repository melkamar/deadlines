package com.melkamar.deadlines.services.api.implementation;

import com.melkamar.deadlines.config.StringConstants;
import com.melkamar.deadlines.dao.processing.TaskFilter;
import com.melkamar.deadlines.dao.processing.TaskOrdering;
import com.melkamar.deadlines.dao.task.TaskDAO;
import com.melkamar.deadlines.dao.taskparticipant.TaskParticipantDAO;
import com.melkamar.deadlines.dao.taskwork.TaskWorkDAO;
import com.melkamar.deadlines.dao.urgency.UrgencyDAO;
import com.melkamar.deadlines.exceptions.*;
import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.MemberRole;
import com.melkamar.deadlines.model.TaskParticipant;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.task.*;
import com.melkamar.deadlines.services.DateConvertor;
import com.melkamar.deadlines.services.PermissionHandler;
import com.melkamar.deadlines.services.api.GroupAPI;
import com.melkamar.deadlines.services.api.TaskAPI;
import com.melkamar.deadlines.services.helpers.TaskParticipantHelper;
import com.melkamar.deadlines.services.helpers.UrgencyHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 27.03.2016 12:52
 */
@Service
public class TaskAPIImpl implements TaskAPI {
    @Autowired
    private StringConstants stringConstants;
    @Autowired
    private UrgencyHelper urgencyHelper;
    @Autowired
    private TaskDAO taskDAO;
    @Autowired
    private TaskParticipantHelper taskParticipantHelper;
    @Autowired
    private TaskParticipantDAO taskparticipantDAO;
    @Autowired
    private GroupAPI groupApi;
    @Autowired
    private PermissionHandler permissionHandler;
    @Autowired
    private UrgencyDAO urgencyDao;
    @Autowired
    private TaskWorkDAO taskWorkDAO;


    @Override
    public Task createTask(User creator, String name, String description, Priority priority, double workEstimate, LocalDateTime deadline) throws WrongParameterException {
        validateGenericCreateTaskParams(creator, name);
        if (deadline == null) throw new WrongParameterException(stringConstants.EXC_PARAM_TASK_DEADLINE_NULL);

        Urgency urgency = new Urgency();
        urgencyDao.save(urgency);

        DeadlineTask task = new DeadlineTask(new Date(), DateConvertor.localDateTimeToDate(deadline), urgency);
        this.populateGenericTaskData(task, creator, name, description, priority, workEstimate);

        taskDAO.save(task);
        taskParticipantHelper.editOrCreateTaskParticipant(creator, task, TaskRole.WATCHER, null, true);
        return task;
    }

    @Override
    public Task createTask(User creator, String name, String description, Priority priority, double workEstimate, double hoursToPeak) throws WrongParameterException {
        validateGenericCreateTaskParams(creator, name);
        if (hoursToPeak < 0) throw new WrongParameterException(stringConstants.EXC_PARAM_TASK_GROWSPEED_INVALID);

        Urgency urgency = new Urgency();
        urgencyDao.save(urgency);

        GrowingTask task = new GrowingTask(new Date(), hoursToPeak, urgency);
        this.populateGenericTaskData(task, creator, name, description, priority, workEstimate);

        taskDAO.save(task);
        taskParticipantHelper.editOrCreateTaskParticipant(creator, task, TaskRole.WATCHER, null, true);
        return task;
    }

    /**
     * Creates a DeadlineTask. If groups is not null, then it will be immediately shared with the given groups.
     * Creator must be manager of all of them.
     */
    @Override
    public Task createTask(User creator, String name, String description, Priority priority, double workEstimate,
                           List<Group> groups, LocalDateTime deadline) throws
            WrongParameterException, GroupPermissionException, NotMemberOfException {
        if (groups == null) return createTask(creator, name, description, priority, workEstimate, deadline);
        Task newTask = createTask(creator, name, description, priority, workEstimate, deadline);

        for (Group group : groups) {
            try {
                groupApi.addTask(creator, group, newTask);
            } catch (AlreadyExistsException e) {
                // Do nothing, in this case we don't care about the exception and can ignore it
            }
        }

        return newTask;
    }

    /**
     * Creates a GrowingTask. If groups is not null, then it will be immediately shared with the given groups.
     * Creator must be manager of all of them.
     */
    @Override
    public Task createTask(User creator, String name, String description, Priority priority, double workEstimate,
                           List<Group> groups, double hoursToPeak)
            throws WrongParameterException, GroupPermissionException, NotMemberOfException{
        if (groups == null) return createTask(creator, name, description, priority, workEstimate, hoursToPeak);
        Task newTask = createTask(creator, name, description, priority, workEstimate, hoursToPeak);

        for (Group group : groups) {
            try {
                groupApi.addTask(creator, group, newTask);
            } catch (AlreadyExistsException e) {
                // Do nothing, in this case we don't care about the exception and can ignore it
            }
        }

        return newTask;
    }

    @Override
    public TaskWork reportWork(User user, Task task, Double workDone) throws WrongParameterException, NotMemberOfException, TaskPermissionException {
        if (workDone == null) throw new WrongParameterException(stringConstants.EXC_PARAM_ALL_NEED_NOT_NULL);
        if (workDone < 0) {
            throw new WrongParameterException(stringConstants.EXC_PARAM_TASK_MANHOURS_INVALID);
        }
        if (!task.getUsersOnTask().contains(user)) {
            throw new NotMemberOfException(MessageFormat.format(stringConstants.EXC_USER_NOT_PARTICIPANT, user, task));
        }

        TaskParticipant participant = taskparticipantDAO.findByUserAndTask(user, task);
        if (participant == null) {
            throw new NotMemberOfException(MessageFormat.format(stringConstants.EXC_USER_NOT_PARTICIPANT_IS_NULL, user.getUsername(), task.getName(), task.getId()));
        } else if (participant.getRole() != TaskRole.WORKER) {
            throw new TaskPermissionException(MessageFormat.format(stringConstants.EXC_USER_NOT_WORKER, user, task));
        }

        TaskWork taskWork = new TaskWork(workDone, user);
        taskWorkDAO.save(taskWork);
        task.addWorkReport(taskWork);

        return taskWork;
    }

    /**
     * Lists tasks of the user, optionally applying filters and ordering to it.
     *
     * @param tasksOfUser
     * @return
     */
    @Override
    public List<Task> listTasks(User tasksOfUser, TaskOrdering ordering, TaskFilter... filters) {
        List<Task> tasks = getOrderedTasksOfUser(tasksOfUser, ordering);

        for (TaskFilter filter : filters) {
            tasks = filter.filter(tasks);
        }

        return tasks;
    }


    private List<Task> getOrderedTasksOfUser(User user, TaskOrdering ordering) {
        switch (ordering) {
            case NONE:
                return taskDAO.findByUser(user);
            case NAME_ASC:
                return taskDAO.findByUserOrderByNameAsc(user);
            case NAME_DESC:
                return taskDAO.findByUserOrderByNameDesc(user);
            case DATE_START_ASC:
                return taskDAO.findByUserOrderByDateCreatedAsc(user);
            case DATE_START_DESC:
                return taskDAO.findByUserOrderByDateCreatedDesc(user);
            case DATE_DEADLINE_ASC:
                return taskDAO.findByUserOrderByDeadlineAsc(user);
            case DATE_DEADLINE_DESC:
                return taskDAO.findByUserOrderByDeadlineDesc(user);
            case WORKED_PERCENT_ASC:
                return taskDAO.findByUserOrderByWorkedAsc(user);
            case WORKED_PERCENT_DESC:
                return taskDAO.findByUserOrderByWorkedDesc(user);
            case PRIORITY_ASC:
                return taskDAO.findByUserOrderByPriorityAsc(user);
            case PRIORITY_DESC:
                return taskDAO.findByUserOrderByPriorityDesc(user);
            case URGENCY_ASC:
                return taskDAO.findByUserOrderByUrgency_ValueAsc(user);
            case URGENCY_DESC:
                return taskDAO.findByUserOrderByUrgency_ValueDesc(user);
            default:
                return taskDAO.findByUser(user);
        }
    }

    @Override
    public List<Task> listTasks(Group tasksOfGroup, TaskOrdering ordering, TaskFilter... filters) {
        List<Task> tasks = getOrderedTasksOfGroup(tasksOfGroup, ordering);

        for (TaskFilter filter : filters) {
            tasks = filter.filter(tasks);
        }

        return tasks;
    }

    private List<Task> getOrderedTasksOfGroup(Group group, TaskOrdering ordering) {
        switch (ordering) {
            case NONE:
                return taskDAO.findByGroup(group);
            case NAME_ASC:
                return taskDAO.findByGroupOrderByNameAsc(group);
            case NAME_DESC:
                return taskDAO.findByGroupOrderByNameDesc(group);
            case DATE_START_ASC:
                return taskDAO.findByGroupOrderByDateCreatedAsc(group);
            case DATE_START_DESC:
                return taskDAO.findByGroupOrderByDateCreatedDesc(group);
            case DATE_DEADLINE_ASC:
                return taskDAO.findByGroupOrderByDeadlineAsc(group);
            case DATE_DEADLINE_DESC:
                return taskDAO.findByGroupOrderByDeadlineDesc(group);
            case WORKED_PERCENT_ASC:
                return taskDAO.findByGroupOrderByWorkedAsc(group);
            case WORKED_PERCENT_DESC:
                return taskDAO.findByGroupOrderByWorkedDesc(group);
            case PRIORITY_ASC:
                return taskDAO.findByGroupOrderByPriorityAsc(group);
            case PRIORITY_DESC:
                return taskDAO.findByGroupOrderByPriorityDesc(group);
            case URGENCY_ASC:
                return taskDAO.findByGroupOrderByUrgency_ValueAsc(group);
            case URGENCY_DESC:
                return taskDAO.findByGroupOrderByUrgency_ValueDesc(group);
            default:
                return taskDAO.findByGroup(group);
        }
    }

    @Override
    public Task getTask(Long taskId) throws DoesNotExistException {
        Task task = taskDAO.findById(taskId);
        if (task == null)
            throw new DoesNotExistException(MessageFormat.format(stringConstants.EXC_DOES_NOT_EXIST_TASK, taskId));
        return task;
    }

    @Override
    public Task getTask(User executor, Long taskId) throws DoesNotExistException, NotMemberOfException {
        Task task = taskDAO.findById(taskId);
        if (task == null)
            throw new DoesNotExistException(MessageFormat.format(stringConstants.EXC_DOES_NOT_EXIST_TASK, taskId));
        if (!task.getUsersOnTask().contains(executor))
            throw new NotMemberOfException(MessageFormat.format(stringConstants.EXC_USER_NOT_PARTICIPANT, executor, task));

        return task;
    }

    /**
     * Changes a toChangeUser's role on a task. Will succeed if executor is toChangeUser, or if executor is a manager
     * of a group the toChangeUser belongs to and which is participating on the task.
     */
    @Override
    public void setTaskRole(User user, Task task, TaskRole newRole) throws NotMemberOfException {
        TaskParticipant taskParticipant = taskParticipantHelper.getTaskParticipant(user, task);
        if (taskParticipant == null)
            throw new NotMemberOfException(MessageFormat.format(stringConstants.EXC_USER_NOT_PARTICIPANT, user, task));

        taskParticipant.setRole(newRole);
    }

    @Override
    public void setTaskRole(User user, Task task, TaskRole newRole, User manager, Group group) throws WrongParameterException, NotMemberOfException, GroupPermissionException, NotAllowedException {
        if (user == null || task == null || manager == null || group == null)
            throw new WrongParameterException(stringConstants.EXC_PARAM_ALL_NEED_NOT_NULL);

        if (!group.getSharedTasks().contains(task)) { // Task is not shared with the group
            throw new NotAllowedException(MessageFormat.format(stringConstants.EXC_GROUP_NOT_IN_TASK, group, task));
        }

        // If the "manager" user is not a manager of group AND he also isn't the user requesting removal, deny it
        if (!permissionHandler.hasGroupPermissionOver(manager, group, user, MemberRole.MANAGER))
            throw new GroupPermissionException(MessageFormat.format(stringConstants.EXC_GROUP_PERMISSION, MemberRole.MANAGER, manager, group));

        // Sufficient permissions, allow changing role
        setTaskRole(user, task, newRole);
    }

    @Override
    public void setTaskStatus(User user, Task task, TaskStatus newStatus) throws NotMemberOfException, NotAllowedException {
        TaskParticipant taskParticipant = taskParticipantHelper.getTaskParticipant(user, task);
        if (taskParticipant == null)
            throw new NotMemberOfException(MessageFormat.format(stringConstants.EXC_USER_NOT_PARTICIPANT, user, task));
        if (taskParticipant.getRole() != TaskRole.WORKER)
            throw new NotAllowedException(MessageFormat.format(stringConstants.EXC_USER_NOT_WORKER, user, task));

        task.setStatus(newStatus);
    }

    @Override
    public void editTask(User user, Task task, String newDescription, LocalDateTime newDeadline, Double newWorkEstimate, Priority newPriority) throws TaskPermissionException, NotAllowedException, NotMemberOfException {
        if (!permissionHandler.hasTaskPermission(user, task, TaskRole.WORKER))
            throw new TaskPermissionException(MessageFormat.format(stringConstants.EXC_USER_NOT_WORKER, user, task));

        if (newDescription != null && !newDescription.isEmpty()) {
            task.setDescription(newDescription);
        }

        if (newDeadline != null) {
            if (task instanceof DeadlineTask) {
                ((DeadlineTask) task).setDeadline(DateConvertor.localDateTimeToDate(newDeadline));
                urgencyHelper.updateUrgency(task, true);
            } else {
                throw new NotAllowedException(stringConstants.EXC_NOT_ALLOWED_SETING_DEADLINE_ON_GROWING);
            }
        }

        if (newWorkEstimate != null && newWorkEstimate >= 0) {
            task.setWorkEstimate(newWorkEstimate);
        }

        if (newPriority != null) {
            task.setPriority(newPriority);
        }
    }


    /**
     * Resets urgency of a GrowingTask. Does not affect DeadlineTasks.
     *
     * @param user
     * @param task
     * @return
     */
    @Override
    public Task resetUrgency(User user, Task task) throws NotAllowedException, NotMemberOfException, TaskPermissionException {
        if (!(task instanceof GrowingTask)) {
            throw new NotAllowedException(stringConstants.EXC_NOT_ALLOWED_RESET_URGENCY_ON_DEADLINE);
        }

        if (!permissionHandler.hasTaskPermission(user, task, TaskRole.WORKER)) {
            throw new TaskPermissionException(MessageFormat.format(stringConstants.EXC_USER_NOT_WORKER, user, task));
        }

        urgencyHelper.resetUrgency(task);

        return task;
    }

    @Override
    public TaskParticipant getTaskParticipant(User user, Task task) {
        return taskParticipantHelper.getTaskParticipant(user, task);
    }

    private void validateGenericCreateTaskParams(User creator, String name) throws WrongParameterException {
        if (creator == null) {
            throw new WrongParameterException(stringConstants.EXC_PARAM_TASK_CREATOR_NULL);
        }
        if (name == null || name.isEmpty()) {
            throw new WrongParameterException(stringConstants.EXC_PARAM_NAME_EMPTY);
        }
    }

    private Task populateGenericTaskData(Task task, User creator, String name, String description, Priority priority, double workEstimate) {
        task.setName(name);
        task.setDescription(description);
        task.setWorkEstimate(workEstimate);
        task.setPriority(priority == null ? Priority.NORMAL : priority);
        task.setStatus(TaskStatus.OPEN);

        urgencyHelper.updateUrgency(task, true);

        return task;
    }
}
