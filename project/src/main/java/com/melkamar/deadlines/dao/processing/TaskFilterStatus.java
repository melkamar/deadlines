package com.melkamar.deadlines.dao.processing;

import com.melkamar.deadlines.model.task.Task;
import com.melkamar.deadlines.model.task.TaskStatus;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 01.04.2016 15:43
 *
 * Filters given jobs based on their status
 */
public class TaskFilterStatus implements TaskFilter {
    private final TaskStatus status;

    public TaskFilterStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public List<Task> filter(List<Task> tasks) {
        return tasks.stream().filter(task -> task.getStatus().equals(status)).collect(Collectors.toList());
    }
}
