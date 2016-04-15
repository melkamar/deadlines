package com.melkamar.deadlines.factory;

import com.melkamar.deadlines.config.StringConstants;
import com.melkamar.deadlines.exceptions.GroupPermissionException;
import com.melkamar.deadlines.exceptions.NotMemberOfException;
import com.melkamar.deadlines.exceptions.WrongParameterException;
import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.task.*;
import com.melkamar.deadlines.utils.DateConvertor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 15.04.2016 12:52
 */
@Component("taskFactory")
public class TaskFactoryImpl implements TaskFactory {
    @Autowired
    private StringConstants stringConstants;

    @Override
    public DeadlineTask createTask(User creator, String name, String description, Priority priority, double workEstimate, LocalDateTime deadline) throws WrongParameterException {
        validateGenericCreateTaskParams(creator, name);
        if (deadline == null) throw new WrongParameterException(stringConstants.EXC_PARAM_TASK_DEADLINE_NULL);

        DeadlineTask task = new DeadlineTask(new Date(), DateConvertor.localDateTimeToDate(deadline));
        this.populateGenericTaskData(task, creator, name, description, priority, workEstimate);

        return task;
    }

    @Override
    public GrowingTask createTask(User creator, String name, String description, Priority priority, double workEstimate, double hoursToPeak) throws WrongParameterException {
        validateGenericCreateTaskParams(creator, name);
        if (hoursToPeak < 0) throw new WrongParameterException(stringConstants.EXC_PARAM_TASK_GROWSPEED_INVALID);

        GrowingTask task = new GrowingTask(new Date(), hoursToPeak);
        this.populateGenericTaskData(task, creator, name, description, priority, workEstimate);

        return task;
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

        return task;
    }
}
