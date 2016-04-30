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

package com.melkamar.deadlines.services.api;

import com.melkamar.deadlines.dao.processing.TaskFilter;
import com.melkamar.deadlines.dao.processing.TaskOrdering;
import com.melkamar.deadlines.exceptions.*;
import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.TaskParticipant;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.task.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 11.04.2016 14:25
 *
 * Impl: {@link com.melkamar.deadlines.services.api.implementation.TaskApiImpl}
 */
public interface TaskApi {
    Task createTask(User creator, String name, String description, Priority priority, double workEstimate, LocalDateTime deadline) throws WrongParameterException;
    Task createTask(User creator, String name, String description, Priority priority, double workEstimate, double hoursToPeak) throws WrongParameterException;
    Task createTask(User creator, String name, String description, Priority priority, double workEstimate, List<Group> groups, LocalDateTime deadline) throws WrongParameterException, GroupPermissionException, NotMemberOfException;
    Task createTask(User creator, String name, String description, Priority priority, double workEstimate, List<Group> groups, double hoursToPeak) throws WrongParameterException, GroupPermissionException, NotMemberOfException;
    //
    void editTask(User user, Task task, String newDescription, LocalDateTime newDeadline, Double newWorkEstimate, Priority newPriority) throws TaskPermissionException, NotAllowedException, NotMemberOfException;
    //
    List<Task> listTasks(User tasksOfUser, TaskOrdering ordering, TaskFilter... filters);
    List<Task> listTasks(Group tasksOfGroup, TaskOrdering ordering, TaskFilter... filters);
    List<Task> listTasks(User user, Group group, TaskOrdering ordering, TaskFilter... filters) throws NotMemberOfException, GroupPermissionException;
    Task getTask(Long taskId) throws DoesNotExistException;
    Task getTask(User executor, Long taskId) throws DoesNotExistException, NotMemberOfException;
    TaskParticipant getTaskParticipant(User user, Task task);
    //
    TaskWork reportWork(User user, Task task, Double workDone) throws WrongParameterException, NotMemberOfException, TaskPermissionException;
    //
    void setTaskRole(User user, Task task, TaskRole newRole) throws NotMemberOfException;
    void setTaskRole(User user, Task task, TaskRole newRole, User manager, Group group) throws WrongParameterException, NotMemberOfException, GroupPermissionException, NotAllowedException;
    void setTaskStatus(User user, Task task, TaskStatus newStatus) throws NotMemberOfException, NotAllowedException;
    //
    void resetUrgency(User user, Task task) throws NotAllowedException, NotMemberOfException, TaskPermissionException;
}
