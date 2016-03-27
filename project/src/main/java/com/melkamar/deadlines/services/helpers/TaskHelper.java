package com.melkamar.deadlines.services.helpers;

import com.melkamar.deadlines.config.StringConstants;
import com.melkamar.deadlines.dao.task.TaskDAO;
import com.melkamar.deadlines.dao.user.UserDAOHibernate;
import com.melkamar.deadlines.exceptions.WrongParameterException;
import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.TaskParticipant;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.task.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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


    public Task createTask(User creator, String name, String description, Priority priority, double workEstimate, LocalDateTime deadline) throws WrongParameterException {
        validateGenericCreateTaskParams(creator, name);
        if (deadline == null) throw new WrongParameterException(stringConstants.EXC_PARAM_TASK_DEADLINE_NULL);

        DeadlineTask task = new DeadlineTask(new Date());
        this.populateGenericTaskData(task, creator, name, description, priority, workEstimate);

        taskDAO.save(task);
        this.addUserToTask(creator, task, TaskRole.WATCHER, null);
        return task;
    }

    public Task createTask(User creator, String name, String description, Priority priority, double workEstimate, double growSpeed) throws WrongParameterException {
        validateGenericCreateTaskParams(creator, name);
        if (growSpeed < 0) throw new WrongParameterException(stringConstants.EXC_PARAM_TASK_GROWSPEED_INVALID);

        GrowingTask task = new GrowingTask(new Date());
        this.populateGenericTaskData(task, creator, name, description, priority, workEstimate);

        taskDAO.save(task);
        this.addUserToTask(creator, task, TaskRole.WATCHER, null);
        return task;
    }

    public boolean reportWork() {
        // TODO: 27.03.2016
        return false;
    }

    /**
     * Adds a user to a task as a TaskParticipant.
     *
     * @param user  User to add.
     * @param task  Task to add the user to.
     * @param group If set, the user is added as a member of the group. May be null.
     */
    public void addUserToTask(User user, Task task, TaskRole role, Group group) {
        TaskParticipant taskParticipant = taskParticipantHelper.addTaskParticipantEntry(user, task, role, group);
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
