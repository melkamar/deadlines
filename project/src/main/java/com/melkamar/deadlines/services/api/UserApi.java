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

import com.melkamar.deadlines.exceptions.*;
import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.task.Task;

import java.util.List;
import java.util.Set;

/**
 * @author Martin Melka
 */
public interface UserApi {
    /**
     * Creates a user from given parameters.
     *
     * @param username Username of the new user.
     * @param password Password of the new user.
     * @param name     Optional. Real name of the new user.
     * @param email    Optional. E-mail of the new user.
     * @return The created {@link User} instance.
     * @throws WrongParameterException if any of the required parameters are null.
     * @throws AlreadyExistsException  if a user with the given username already exists.
     */
    User createUser(String username, String password, String name, String email) throws WrongParameterException, AlreadyExistsException;

    /**
     * Edits details of an existing user.
     *
     * @param user A {@link User} object whose details should be changed.
     * @param name Optional. New name to set for the user.
     * @param email Optional. New email to set for the user.
     * @param password Optional. New password to set for the user.
     * @return The {@link User} object that has been changed.
     */
    User editUserDetails(User user, String name, String email, String password);

    /**
     * Lists all users in the application.
     *
     * @return All existing users.
     */
    List<User> listUsers();

    /**
     * Lists all groups the user belongs to.
     *
     * @param executor User of whom all groups should be listed.
     * @return All groups the user belongs to.
     */
    Set<Group> getGroupsOfUser(User executor);

    /**
     * Gets a user based on their ID.
     *
     * @param id ID of the user to find.
     * @return The {@link User} object with the given ID.
     * @throws DoesNotExistException if no such User exists.
     */
    User getUser(Long id) throws DoesNotExistException;

    /**
     * Gets a user based on their username.
     *
     * @param username Username of the user to find.
     * @return The {@link User} object with the given ID.
     * @throws DoesNotExistException if no such User exists.
     */
    User getUser(String username) throws DoesNotExistException;

    /**
     * Removes a user from a group.
     *
     * @param user User to be removed from a group.
     * @param group The group from which the user should be removed.
     * @throws NotAllowedException      if the removed user is Admin.
     * @throws NotMemberOfException     if the user is not a member of the group.
     * @throws WrongParameterException  if any of the parameters is null.
     */
    void leaveGroup(User user, Group group) throws NotAllowedException, WrongParameterException, NotMemberOfException;

    /**
     * Removes a user from a task.
     *
     * @param user User to be removed from a task.
     * @param task The task from which a user should be removed.
     * @throws NotMemberOfException if the user is not a participant on the task.
     */
    void leaveTask(User user, Task task) throws NotMemberOfException;
}
