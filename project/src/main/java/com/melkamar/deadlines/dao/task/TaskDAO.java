package com.melkamar.deadlines.dao.task;

import com.melkamar.deadlines.model.task.Task;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 27.03.2016 15:14
 */
public interface TaskDAO {
    public long count();
    public Task save(Task user);
//    public User findByUsername(String username);
}
