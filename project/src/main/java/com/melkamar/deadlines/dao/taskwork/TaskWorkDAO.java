package com.melkamar.deadlines.dao.taskwork;

import com.melkamar.deadlines.model.task.TaskWork;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 27.03.2016 19:14
 */
public interface TaskWorkDAO {
    long count();
    TaskWork save(TaskWork taskWork);
}
