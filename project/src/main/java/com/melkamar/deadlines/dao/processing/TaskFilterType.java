package com.melkamar.deadlines.dao.processing;

import com.melkamar.deadlines.model.task.Task;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 01.04.2016 15:42
 * <p>
 * Filters given jobs based on their type (Deadline/Growing)
 */
public class TaskFilterType implements TaskFilter {
    private Class wantedType;

    public TaskFilterType(Class wantedType) {
        this.wantedType = wantedType;
    }

    @Override
    public List<Task> filter(List<Task> tasks) {
        return tasks.stream().filter(task -> task.getClass().equals(wantedType)).collect(Collectors.toList());
    }
}
