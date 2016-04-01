package com.melkamar.deadlines.dao.processing;

import com.melkamar.deadlines.model.task.Task;

import java.util.List;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 01.04.2016 11:16
 */
public interface TaskFilter {
    public List<Task> filter(List<Task> tasks);

}
