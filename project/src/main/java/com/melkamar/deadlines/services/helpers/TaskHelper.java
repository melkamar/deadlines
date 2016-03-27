package com.melkamar.deadlines.services.helpers;

import com.melkamar.deadlines.config.StringConstants;
import com.melkamar.deadlines.exceptions.NullParameterException;
import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.task.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public Task createTask(User creator, String name, String description, Priority priority, double workEstimate, Date deadline) throws NullParameterException {
        validateGenericCreateTaskParams(creator, name);
        if (deadline == null) throw new NullParameterException(stringConstants.EXC_PARAM_TASK_DEADLINE_NULL);

        DeadlineTask task = new DeadlineTask();
        this.populateGenericTaskData(task, creator, name, description, priority, workEstimate);
        return task;
    }

    public Task createTask(User creator, String name, String description, Priority priority, double workEstimate, double growSpeed) throws NullParameterException {
        validateGenericCreateTaskParams(creator, name);
        if (growSpeed < 0) throw new NullParameterException(stringConstants.EXC_PARAM_TASK_GROWSPEED_INVALID);

        GrowingTask task = new GrowingTask();
        this.populateGenericTaskData(task, creator, name, description, priority, workEstimate);
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
    public void addUserToTask(User user, Task task, Group group) {
        // TODO: 27.03.2016 Look for Participant, if exists edit it, if not create it.
    }


    private void validateGenericCreateTaskParams(User creator, String name) throws NullParameterException {
        if (creator == null) {
            throw new NullParameterException(stringConstants.EXC_PARAM_TASK_CREATOR_NULL);
        }
        if (name == null || name.isEmpty()) {
            throw new NullParameterException(stringConstants.EXC_PARAM_NAME_EMPTY);
        }
    }

    private Task populateGenericTaskData(Task task, User creator, String name, String description, Priority priority, double workEstimate) {
        task.setName(name);
        task.setDescription(description);
        task.setWorkEstimate(workEstimate);
        task.setPriority(priority);
        urgencyHelper.computeUrgency(task);
        task.setStatus(TaskStatus.OPEN);

        this.addUserToTask(creator, task, null);

        return task;
    }
}
