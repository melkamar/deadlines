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
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Martin Melka
 */
@Repository
public interface TaskRepository extends CrudRepository<Task, Long> {
    Task saveAndFlush(Task task);
    Task findById(Long id);
    List<Task> findByStatus(TaskStatus status);

    List<Task> findByParticipants_User(User user);
    List<Task> findByParticipants_UserOrderByNameAsc(User user);
    List<Task> findByParticipants_UserOrderByNameDesc(User user);
    List<Task> findByParticipants_UserOrderByDateCreatedAsc(User user);
    List<Task> findByParticipants_UserOrderByDateCreatedDesc(User user);
    List<Task> findByParticipants_UserOrderByPriorityAsc(User user);
    List<Task> findByParticipants_UserOrderByPriorityDesc(User user);
    List<Task> findByParticipants_UserOrderByUrgency_ValueAsc(User user);
    List<Task> findByParticipants_UserOrderByUrgency_ValueDesc(User user);

    // GROUPS
    List<Task> findBySharedGroups(Group group);
    List<Task> findBySharedGroupsOrderByNameAsc(Group group);
    List<Task> findBySharedGroupsOrderByNameDesc(Group group);
    List<Task> findBySharedGroupsOrderByDateCreatedAsc(Group group);
    List<Task> findBySharedGroupsOrderByDateCreatedDesc(Group group);
    List<Task> findBySharedGroupsOrderByPriorityAsc(Group group);
    List<Task> findBySharedGroupsOrderByPriorityDesc(Group group);
    List<Task> findBySharedGroupsOrderByUrgency_ValueAsc(Group group);
    List<Task> findBySharedGroupsOrderByUrgency_ValueDesc(Group group);
}
