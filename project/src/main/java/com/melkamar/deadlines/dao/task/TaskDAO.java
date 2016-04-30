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

package com.melkamar.deadlines.dao.task;

import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.task.Task;
import com.melkamar.deadlines.model.task.TaskStatus;

import java.util.List;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 27.03.2016 15:14
 */
public interface TaskDAO {
    long count();
    Task save(Task task);
    Task findById(Long id);
    List<Task> findAll();
    List<Task> findByUser(User user);
    List<Task> findByStatus(TaskStatus status);

    List<Task> findByUserOrderByNameAsc(User user);
    List<Task> findByUserOrderByNameDesc(User user);

    List<Task> findByUserOrderByDateCreatedAsc(User user);
    List<Task> findByUserOrderByDateCreatedDesc(User user);

    List<Task> findByUserOrderByPriorityAsc(User user);
    List<Task> findByUserOrderByPriorityDesc(User user);

    List<Task> findByUserOrderByUrgency_ValueAsc(User user);
    List<Task> findByUserOrderByUrgency_ValueDesc(User user);

    List<Task> findByUserOrderByDeadlineAsc(User user);
    List<Task> findByUserOrderByDeadlineDesc(User user);

    List<Task> findByUserOrderByWorkedAsc(User user);
    List<Task> findByUserOrderByWorkedDesc(User user);

    // GROUPS
    List<Task> findByGroup(Group group);

    List<Task> findByGroupOrderByNameAsc(Group group);
    List<Task> findByGroupOrderByNameDesc(Group group);

    List<Task> findByGroupOrderByDateCreatedAsc(Group group);
    List<Task> findByGroupOrderByDateCreatedDesc(Group group);

    List<Task> findByGroupOrderByPriorityAsc(Group group);
    List<Task> findByGroupOrderByPriorityDesc(Group group);

    List<Task> findByGroupOrderByUrgency_ValueAsc(Group group);
    List<Task> findByGroupOrderByUrgency_ValueDesc(Group group);

    List<Task> findByGroupOrderByDeadlineAsc(Group group);
    List<Task> findByGroupOrderByDeadlineDesc(Group group);

    List<Task> findByGroupOrderByWorkedAsc(Group group);
    List<Task> findByGroupOrderByWorkedDesc(Group group);
}


