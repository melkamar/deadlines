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

package com.melkamar.deadlines.dao.taskparticipant;

import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.TaskParticipant;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.task.Task;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 27.03.2016 15:46
 */
@Repository
public interface TaskParticipantRepository extends CrudRepository<TaskParticipant, Long> {
    TaskParticipant findByUserAndTask(User user, Task task);
    Set<TaskParticipant> findByUserAndGroups(User user, Group group);
    Set<TaskParticipant> findByTaskAndGroups(Task task, Group group);
    Set<TaskParticipant> findByUser(User user);
}
