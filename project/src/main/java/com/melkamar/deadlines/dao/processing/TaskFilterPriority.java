package com.melkamar.deadlines.dao.processing;

import com.melkamar.deadlines.model.task.Priority;
import com.melkamar.deadlines.model.task.Task;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 01.04.2016 15:43
 */
public class TaskFilterPriority implements TaskFilter {
    private final Set<Priority> priorities;

    public TaskFilterPriority(Priority... priorities) {
        this.priorities = new HashSet<>(Arrays.asList(priorities));
    }

    @Override
    public List<Task> filter(List<Task> tasks) {
        return tasks.stream().filter(task -> priorities.contains(task.getPriority())).collect(Collectors.toList());
    }
}
