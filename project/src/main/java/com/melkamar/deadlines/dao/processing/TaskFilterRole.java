package com.melkamar.deadlines.dao.processing;

import com.melkamar.deadlines.model.TaskParticipant;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.task.Task;
import com.melkamar.deadlines.model.task.TaskRole;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 01.04.2016 15:42
 *
 * Filters given tasks based on user's role on them.
 */
public class TaskFilterRole implements TaskFilter {
    private final TaskRole showRole;
    private final User user;

    public TaskFilterRole(User user, TaskRole showRole) {
        this.user = user;
        this.showRole = showRole;
    }

    @Override
    public List<Task> filter(List<Task> tasks) {
        List<Task> newList = new ArrayList<>();
        for (Task task: tasks){
            for (TaskParticipant participant: task.getParticipants()){
                // Iterate through participants of the "task"
                if (participant.getUser().equals(user)){
                    // If user's participant found, no need to search further (there won't be another one)
                    if (participant.getRole() == showRole) newList.add(task);
                    break;
                }
            }

        }

        return newList;
    }
}
