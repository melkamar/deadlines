package com.melkamar.deadlines.dao.task;

import com.melkamar.deadlines.model.task.Task;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 27.03.2016 15:14
 */
public interface TaskDAO {
    long count();
    Task save(Task task);
    Task findById(Long id);
}
