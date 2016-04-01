package com.melkamar.deadlines.services.api;

import com.melkamar.deadlines.config.StringConstants;
import com.melkamar.deadlines.dao.task.TaskDAO;
import com.melkamar.deadlines.dao.taskparticipant.TaskParticipantDAO;
import com.melkamar.deadlines.exceptions.GroupPermissionException;
import com.melkamar.deadlines.exceptions.NotMemberOfException;
import com.melkamar.deadlines.exceptions.WrongParameterException;
import com.melkamar.deadlines.exceptions.WrongRoleException;
import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.TaskParticipant;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.task.*;
import com.melkamar.deadlines.services.helpers.TaskParticipantHelper;
import com.melkamar.deadlines.services.helpers.UrgencyHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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
public class TaskAPI {
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
    private GroupAPI groupAPI;


    public Task createTask(User creator, String name, String description, Priority priority, double workEstimate, LocalDateTime deadline) throws WrongParameterException {
        validateGenericCreateTaskParams(creator, name);
        if (deadline == null) throw new WrongParameterException(stringConstants.EXC_PARAM_TASK_DEADLINE_NULL);

        DeadlineTask task = new DeadlineTask(new Date());
        this.populateGenericTaskData(task, creator, name, description, priority, workEstimate);

        taskDAO.save(task);
        taskParticipantHelper.editOrCreateTaskParticipant(creator, task, TaskRole.WATCHER, null, true);
        return task;
    }

    public Task createTask(User creator, String name, String description, Priority priority, double workEstimate, double growSpeed) throws WrongParameterException {
        validateGenericCreateTaskParams(creator, name);
        if (growSpeed < 0) throw new WrongParameterException(stringConstants.EXC_PARAM_TASK_GROWSPEED_INVALID);

        GrowingTask task = new GrowingTask(new Date());
        this.populateGenericTaskData(task, creator, name, description, priority, workEstimate);

        taskDAO.save(task);
        taskParticipantHelper.editOrCreateTaskParticipant(creator, task, TaskRole.WATCHER, null, true);
        return task;
    }

    /**
     * Creates a DeadlineTask. If groups is not null, then it will be immediately shared with the given groups.
     * Creator must be manager of all of them.
     */
    @Transactional
    public Task createTask(User creator, String name, String description, Priority priority, double workEstimate, Set<Group> groups, LocalDateTime deadline) throws WrongParameterException, GroupPermissionException, NotMemberOfException {
        if (groups == null) return createTask(creator, name, description, priority, workEstimate, deadline);
        Task newTask = createTask(creator, name, description, priority, workEstimate, deadline);

        for (Group group : groups) {
            groupAPI.addTask(creator, group, newTask);
        }

        return newTask;
    }

    /**
     * Creates a GrowingTask. If groups is not null, then it will be immediately shared with the given groups.
     * Creator must be manager of all of them.
     */
    @Transactional
    public Task createTask(User creator, String name, String description, Priority priority, double workEstimate, Set<Group> groups, double growSpeed) throws WrongParameterException, GroupPermissionException, NotMemberOfException {
        if (groups == null) return createTask(creator, name, description, priority, workEstimate, growSpeed);
        Task newTask = createTask(creator, name, description, priority, workEstimate, growSpeed);

        for (Group group : groups) {
            groupAPI.addTask(creator, group, newTask);
        }

        return newTask;
    }


    public TaskWork reportWork(User user, Task task, double workDone) throws WrongParameterException, NotMemberOfException, WrongRoleException {
        if (workDone < 0) {
            throw new WrongParameterException(stringConstants.EXC_PARAM_TASK_MANHOURS_INVALID);
        }
        if (!task.usersOnTask().contains(user)) {
            throw new NotMemberOfException(MessageFormat.format(stringConstants.EXC_USER_NOT_PARTICIPANT, user, task));
        }

        TaskParticipant participant = taskparticipantDAO.findByUserAndTask(user, task);
        if (participant == null) {
            throw new NotMemberOfException(MessageFormat.format(stringConstants.EXC_USER_NOT_PARTICIPANT_IS_NULL, user.getUsername(), task.getName(), task.getId()));
        } else if (participant.getRole() != TaskRole.WORKER) {
            throw new WrongRoleException(MessageFormat.format(stringConstants.EXC_USER_NOT_WORKER, user.getUsername(), task.getName(), task.getId()));
        }

        TaskWork taskWork = new TaskWork(workDone, user);
        task.addWorkReport(taskWork);

        return taskWork;
    }

    public List<Task> listTasks(User tasksOfUser, TaskFilter filter, SortCriteria sortCriteria) {
        // TODO: 31.03.2016 Implement
        throw new NotImplementedException();
    }

    public Task getTask(User executor, Long taskId) {
        // TODO: 31.03.2016 Implement
        throw new NotImplementedException();
    }

    /**
     * Changes a toChangeUser's role on a task. Will succeed if executor is toChangeUser, or if executor is a manager
     * of a group the toChangeUser belongs to and which is participating on the task.
     *
     * @return
     */
    public Task setTaskRole(User executor, Task task, User toChangeUser, TaskRole newRole) {
        // TODO: 31.03.2016 Implement
        throw new NotImplementedException();
    }

    public Task setTaskStatus(User executor, Task task, TaskStatus newStatus) {
        // TODO: 31.03.2016 Implement
        throw new NotImplementedException();
    }

    public Task editTask(User executor, Task task, String newDescription, LocalDateTime newDeadline, Double newWorkEstimate, Priority newPriority) {
        // TODO: 31.03.2016 Implement
        throw new NotImplementedException();
    }

    /**
     * Resets urgency of a GrowingTask. Does not affect DeadlineTasks.
     *
     * @param executor
     * @param task
     * @return
     */
    public Task resetUrgency(User executor, Task task) {
        // TODO: 31.03.2016 Implement
        throw new NotImplementedException();
    }

    public List<Task> listTasks(Group tasksOfGroup, TaskFilter filter, SortCriteria sortCriteria) {
        // TODO: 31.03.2016 Implement
        throw new NotImplementedException();
    }


    public class TaskFilter {
        public List<Task> filter(List<Task> tasks) {
            // TODO: 31.03.2016 Implement
            throw new NotImplementedException();
        }
    }

    public enum SortCriteria {
        NONE, NAME, DATE_START, DATE_DEADLINE, WORKED_PERCENT, PRIORITY, URGENCY
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
        urgencyHelper.computeUrgency(task);
        task.setStatus(TaskStatus.OPEN);

        return task;
    }
}
