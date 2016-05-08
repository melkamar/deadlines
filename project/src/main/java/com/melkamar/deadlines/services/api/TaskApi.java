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
 * @author Martin Melka
 */
public interface TaskApi {
    /**
     * Creates a {@link DeadlineTask} shared with the creating user only, with the given parameters.
     *
     * @param creator      User creating the task.
     * @param name         Name of the task.
     * @param description  Description of the task.
     * @param priority     Priority of the task.
     * @param workEstimate Work estimate for the task in manhours.
     * @param deadline     Deadline for the task.
     * @return The newly created Task object.
     * @throws WrongParameterException if any of the parameters are null or contain a non acceptable value.
     */
    Task createTask(User creator, String name, String description, Priority priority, double workEstimate, LocalDateTime deadline) throws WrongParameterException;

    /**
     * Creates a {@link GrowingTask} shared with the creating user only, with the given parameters.
     *
     * @param creator      User creating the task.
     * @param name         Name of the task.
     * @param description  Description of the task.
     * @param priority     Priority of the task.
     * @param workEstimate Work estimate for the task in manhours.
     * @param hoursToPeak  Speed of growth. The amount of hours it takes before the task reaches peak
     *                     urgency -- equal to that of {@link DeadlineTask} which has equal amount
     *                     of hours left until its deadline as is its remaining work estimate.
     * @return The newly created Task object.
     * @throws WrongParameterException if any of the parameters are null or contain a non acceptable value.
     */
    Task createTask(User creator, String name, String description, Priority priority, double workEstimate, double hoursToPeak) throws WrongParameterException;

    /**
     * Creates a {@link DeadlineTask} shared with the creating user and some groups he is a manager of, with the given parameters.
     *
     * @param creator      User creating the task.
     * @param name         Name of the task.
     * @param description  Description of the task.
     * @param priority     Priority of the task.
     * @param workEstimate Work estimate for the task in manhours.
     * @param groups       Groups with which the task should be shared.
     * @param deadline     Deadline for the task.
     * @return The newly created Task object.
     * @throws WrongParameterException  if any of the parameters are null or contain a non acceptable value.
     * @throws GroupPermissionException if the creator does not have sufficient permissions in one or more of the groups.
     * @throws NotMemberOfException     if the creator is not a member of one or more of the groups.
     */
    Task createTask(User creator, String name, String description, Priority priority, double workEstimate, List<Group> groups, LocalDateTime deadline) throws WrongParameterException, GroupPermissionException, NotMemberOfException;

    /**
     * Creates a {@link GrowingTask} shared with the creating user only, with the given parameters.
     *
     * @param creator      User creating the task.
     * @param name         Name of the task.
     * @param description  Description of the task.
     * @param priority     Priority of the task.
     * @param workEstimate Work estimate for the task in manhours.
     * @param groups       Groups with which the task should be shared.
     * @param hoursToPeak  Speed of growth. The amount of hours it takes before the task reaches peak
     *                     urgency -- equal to that of {@link DeadlineTask} which has equal amount
     *                     of hours left until its deadline as is its remaining work estimate.
     * @return The newly created Task object.
     * @throws WrongParameterException  if any of the parameters are null or contain a non acceptable value.
     * @throws GroupPermissionException if the creator does not have sufficient permissions in one or more of the groups.
     * @throws NotMemberOfException     if the creator is not a member of one or more of the groups.
     */
    Task createTask(User creator, String name, String description, Priority priority, double workEstimate, List<Group> groups, double hoursToPeak) throws WrongParameterException, GroupPermissionException, NotMemberOfException;

    /**
     * Edits an existing task.
     *
     * @param user            The user performing the edit.
     * @param task            The task to be edited.
     * @param newDescription  New description.
     * @param newDeadline     New deadline (only for {@link DeadlineTask}.
     * @param newWorkEstimate New work estimate.
     * @param newPriority     New task priority.
     * @throws TaskPermissionException if the user does not have enough permissions (a role) in a task.
     * @throws NotAllowedException     if the edit is not allowed, such as some values conflicting (setting newDeadline for a {@link GrowingTask})
     * @throws NotMemberOfException    if the user is not a participant on the task.
     */
    void editTask(User user, Task task, String newDescription, LocalDateTime newDeadline, Double newWorkEstimate, Priority newPriority) throws TaskPermissionException, NotAllowedException, NotMemberOfException;

    /**
     * Lists all tasks of a user, with given ordering and filtering.
     *
     * @param tasksOfUser User whose tasks should be listed.
     * @param ordering    A {@link TaskOrdering} object specifying the ordering criteria and direction the results
     *                    should follow.
     * @param filters     Array of {@link TaskFilter} objects, filtering the results.
     * @return All tasks in the requested order and filtering.
     */
    List<Task> listTasks(User tasksOfUser, TaskOrdering ordering, TaskFilter... filters);

    /**
     * Lists all tasks of a group, with given ordering and filtering.
     *
     * @param tasksOfGroup Group of which tasks should be listed.
     * @param ordering     A {@link TaskOrdering} object specifying the ordering criteria and direction the results
     *                     should follow.
     * @param filters      Array of {@link TaskFilter} objects, filtering the results.
     * @return All tasks in the requested order and filtering.
     */
    List<Task> listTasks(Group tasksOfGroup, TaskOrdering ordering, TaskFilter... filters);

    /**
     * Lists tasks of a group, while checking whether the performing user has enough permissions to do so.
     *
     * @param user     The user who wants to list the tasks.
     * @param group    The group which tasks should be listed.
     * @param ordering A {@link TaskOrdering} object specifying the ordering criteria and direction the results
     *                 should follow.
     * @param filters  Array of {@link TaskFilter} objects, filtering the results.
     * @return All tasks in the requested order and filtering.
     * @throws NotMemberOfException     if the user is not a member of the group.
     * @throws GroupPermissionException if the user does not have sufficient permissions in the group.
     */
    List<Task> listTasks(User user, Group group, TaskOrdering ordering, TaskFilter... filters) throws NotMemberOfException, GroupPermissionException;

    /**
     * Gets a {@link Task} object based on its ID.
     *
     * @param taskId ID of the task to find.
     * @return The Task object with the given ID.
     * @throws DoesNotExistException if the task with such ID does not exist.
     */
    Task getTask(Long taskId) throws DoesNotExistException;

    /**
     * Gets a {@link Task} object based on its ID, while checking that the requesting user has enough
     * permissions.
     *
     * @param executor User who wants to retrieve the task.
     * @param taskId   ID of the task to retrieve.
     * @return The Task object with the given ID.
     * @throws DoesNotExistException if the task with such ID does not exist.
     * @throws NotMemberOfException  if the user is not a participant on the task.
     */
    Task getTask(User executor, Long taskId) throws DoesNotExistException, NotMemberOfException;

    /**
     * Gets a {@link TaskParticipant} object based on its defining {@link User} and {@link Task}.
     *
     * @param user User object defining the {@link TaskParticipant}.
     * @param task Task object defining the {@link TaskParticipant}.
     * @return {@link TaskParticipant} object, if such exists. Null otherwise.
     */
    TaskParticipant getTaskParticipant(User user, Task task);

    /**
     * Reports a work done on a task by a user.
     *
     * @param user     User who has done the work.
     * @param task     Task on which the user has done the work.
     * @param workDone Number of hours the user has worked on the task. Must be a positive value.
     * @return Newly created {@link TaskWork} object containing information about the reported work.
     * @throws WrongParameterException if any of the parameters are null or not allowed.
     * @throws NotMemberOfException    if the user is not a participant on the task.
     * @throws TaskPermissionException if the user has not enough permissions on the task to report work on it.
     */
    TaskWork reportWork(User user, Task task, Double workDone) throws WrongParameterException, NotMemberOfException, TaskPermissionException;

    /**
     * Sets a role on a task for a user.
     *
     * @param user    User who wants to change their role.
     * @param task    Task in which the user wants to change their role.
     * @param newRole New role to be assigned to them.
     * @throws NotMemberOfException if the user is not a participant on the task.
     */
    void setTaskRole(User user, Task task, TaskRole newRole) throws NotMemberOfException;

    /**
     * Sets a role for a member of a group on a task by their manager.
     *
     * @param user    User whose role should be changed.
     * @param task    Task on which the user's role should be changed.
     * @param newRole New role to be assigned to them.
     * @param manager The user performing the assignment.
     * @param group   The group in which the assignment is performed.
     * @throws WrongParameterException  if any of the parameters is null or not accepted.
     * @throws NotMemberOfException     if the user or the manager are not members of the given group.
     * @throws GroupPermissionException if the manager does not have sufficient permissions in the group.
     * @throws NotAllowedException      if the task is not shared with the group.
     */
    void setTaskRole(User user, Task task, TaskRole newRole, User manager, Group group) throws WrongParameterException, NotMemberOfException, GroupPermissionException, NotAllowedException;

    /**
     * Changes a task's status.
     *
     * @param user      User changing the status.
     * @param task      Task of which the status is changed.
     * @param newStatus New status to be assigned to the task.
     * @throws NotMemberOfException    if the user is not a participant on the task.
     * @throws TaskPermissionException if the user does not have sufficient permissions in the task to change its status.
     */
    void setTaskStatus(User user, Task task, TaskStatus newStatus) throws NotMemberOfException, TaskPermissionException;

    /**
     * Resets the urgency of a {@link GrowingTask}.
     *
     * @param user User performing the reset.
     * @param task Task of which the urgency is to be reset. Must be an instance of {@link GrowingTask}.
     * @throws NotMemberOfException    if the user is not a participant on the task.
     * @throws NotAllowedException     if the task is not a {@link GrowingTask}.
     * @throws TaskPermissionException if the user does not have sufficient permissions in the task to change its status.
     */
    void resetUrgency(User user, Task task) throws NotAllowedException, NotMemberOfException, TaskPermissionException;
}
