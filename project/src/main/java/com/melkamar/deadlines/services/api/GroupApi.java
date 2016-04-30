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
import com.melkamar.deadlines.model.GroupMember;
import com.melkamar.deadlines.model.MemberRole;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.task.Task;

import java.util.List;
import java.util.Set;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 11.04.2016 13:48
 *
 * {@link com.melkamar.deadlines.services.api.implementation.GroupApiImpl}
 */
public interface GroupApi {
    Group createGroup(String name, User founder, String description) throws WrongParameterException, AlreadyExistsException;
    //
    List<Group> listGroups();
    List<Group> listGroups(User user);
    List<Group> listGroups(User user, MemberRole role);
    Group getGroup(Long groupId) throws DoesNotExistException;
    Group getGroup(Long groupId, User user) throws DoesNotExistException, NotMemberOfException;
    //
    Set<User> getUsersOfGroup(Group group);
    void addMember(User manager, Group group, User newUser) throws WrongParameterException, NotMemberOfException, GroupPermissionException, AlreadyExistsException;
    void removeMember(User manager, Group group, User toRemove) throws NotAllowedException, NotMemberOfException, GroupPermissionException, WrongParameterException;
    void removeMember(User manager, Group group, GroupMember groupMemberToRemove) throws WrongParameterException, NotMemberOfException, GroupPermissionException, NotAllowedException;
    //
    void addTask(User manager, Group group, Task task) throws WrongParameterException, NotMemberOfException, GroupPermissionException, AlreadyExistsException;
    void leaveTask(User manager, Group group, Task task) throws WrongParameterException, NotMemberOfException, GroupPermissionException;
    //
    void editDetails(User admin, Group group, String newDescription) throws NotMemberOfException, GroupPermissionException;
    void setManager(User executor, Group group, User member, boolean newValue) throws GroupPermissionException, NotMemberOfException, WrongParameterException, NotAllowedException;
    void changeAdmin(User admin, Group group, User newAdmin) throws WrongParameterException, NotMemberOfException, GroupPermissionException;
    void deleteGroup(User admin, Group group) throws NotMemberOfException, GroupPermissionException, WrongParameterException;
}
