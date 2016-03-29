package com.melkamar.deadlines.services.helpers;

import com.melkamar.deadlines.config.StringConstants;
import com.melkamar.deadlines.dao.task.TaskDAO;
import com.melkamar.deadlines.dao.taskparticipant.TaskParticipantDAO;
import com.melkamar.deadlines.exceptions.NotMemberOfException;
import com.melkamar.deadlines.exceptions.WrongParameterException;
import com.melkamar.deadlines.exceptions.WrongRoleException;
import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.TaskParticipant;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.task.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 27.03.2016 12:52
 */
@Service
public class TaskHelper {
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


    public Task createTask(User creator, String name, String description, Priority priority, double workEstimate, LocalDateTime deadline) throws WrongParameterException {
        validateGenericCreateTaskParams(creator, name);
        if (deadline == null) throw new WrongParameterException(stringConstants.EXC_PARAM_TASK_DEADLINE_NULL);

        DeadlineTask task = new DeadlineTask(new Date());
        this.populateGenericTaskData(task, creator, name, description, priority, workEstimate);

        taskDAO.save(task);
        taskParticipantHelper.addTaskParticipantEntry(creator, task, TaskRole.WATCHER, null);
        return task;
    }

    public Task createTask(User creator, String name, String description, Priority priority, double workEstimate, double growSpeed) throws WrongParameterException {
        validateGenericCreateTaskParams(creator, name);
        if (growSpeed < 0) throw new WrongParameterException(stringConstants.EXC_PARAM_TASK_GROWSPEED_INVALID);

        GrowingTask task = new GrowingTask(new Date());
        this.populateGenericTaskData(task, creator, name, description, priority, workEstimate);

        taskDAO.save(task);
        taskParticipantHelper.addTaskParticipantEntry(creator, task, TaskRole.WATCHER, null);
        return task;
    }

    public TaskWork reportWork(User user, Task task, double manhours) throws WrongParameterException, NotMemberOfException, WrongRoleException {
        if (manhours<0){
            throw new WrongParameterException(stringConstants.EXC_PARAM_TASK_MANHOURS_INVALID);
        }
        if (!task.usersOnTask().contains(user)){
            throw new NotMemberOfException(MessageFormat.format(stringConstants.EXC_USER_NOT_PARTICIPANT, user.getUsername(), task.getName(), task.getId()));
        }

        TaskParticipant participant = taskparticipantDAO.findByUserAndTask(user, task);
        if (participant == null){
            throw new NotMemberOfException(MessageFormat.format(stringConstants.EXC_USER_NOT_PARTICIPANT_IS_NULL, user.getUsername(), task.getName(), task.getId()));
        } else if (participant.getRole() != TaskRole.WORKER){
            throw new WrongRoleException(MessageFormat.format(stringConstants.EXC_USER_NOT_WORKER, user.getUsername(), task.getName(), task.getId()));
        }

        TaskWork taskWork = new TaskWork(manhours, user);
        task.addWorkReport(taskWork);

        return taskWork;
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
        task.setPriority(priority==null?Priority.NORMAL:priority);
        urgencyHelper.computeUrgency(task);
        task.setStatus(TaskStatus.OPEN);

        return task;
    }
}
