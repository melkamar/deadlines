package com.melkamar.deadlines.factory;

import com.melkamar.deadlines.exceptions.GroupPermissionException;
import com.melkamar.deadlines.exceptions.NotMemberOfException;
import com.melkamar.deadlines.exceptions.WrongParameterException;
import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.task.DeadlineTask;
import com.melkamar.deadlines.model.task.GrowingTask;
import com.melkamar.deadlines.model.task.Priority;
import com.melkamar.deadlines.model.task.Task;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 15.04.2016 12:51
 */
public interface TaskFactory {
    DeadlineTask createTask(User creator, String name, String description, Priority priority, double workEstimate, LocalDateTime deadline) throws WrongParameterException;
    GrowingTask createTask(User creator, String name, String description, Priority priority, double workEstimate, double hoursToPeak) throws WrongParameterException;
}
