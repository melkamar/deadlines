/*
 * Copyright (c) 2016 Martin Melka
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
 * Filters given jobs based on user's role on them.
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
