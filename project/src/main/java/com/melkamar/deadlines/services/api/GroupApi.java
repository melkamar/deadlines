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
 * @author Martin Melka
 */
public interface GroupApi {
    /**
     * Creates a new group.
     * <p>
     * The founder becomes an Admin of the group.
     *
     * @param name        Unique name for the group.
     * @param founder     Founder of the group.
     * @param description Custom description of the group.
     * @return Newly created {@link Group} instance.
     * @throws WrongParameterException if name or founder is null or empty.
     * @throws AlreadyExistsException  if a group with the given name already exists.
     */
    Group createGroup(String name, User founder, String description) throws WrongParameterException, AlreadyExistsException;

    /**
     * Lists all groups in the system.
     *
     * @return List of {@link Group}s.
     */
    List<Group> listGroups();

    /**
     * Lists all groups of which a user is a member.
     *
     * @param user User whose groups should be listed.
     * @return List of {@link Group}s.
     */
    List<Group> listGroups(User user);

    /**
     * Lists all groups of which a user is a member and has a certain role.
     *
     * @param user User whose groups should be listed.
     * @param role Role the user should in in the listed groups.
     * @return List of {@link Group}s.
     */
    List<Group> listGroups(User user, MemberRole role);

    /**
     * Finds a {@link Group} instance based on its ID.
     *
     * @param groupId ID of the group to find.
     * @return Group instance with the given ID.
     * @throws DoesNotExistException if there is no Group with the given ID.
     */
    Group getGroup(Long groupId) throws DoesNotExistException;

    /**
     * Finds a {@link Group} if a user is its member.
     *
     * @param groupId ID of the group to find.
     * @param user    User who should be member of the group.
     * @return Group instance with the given ID.
     * @throws DoesNotExistException if there is no Group with the given ID.
     * @throws NotMemberOfException  if the User is not a member of the group.
     */
    Group getGroup(Long groupId, User user) throws DoesNotExistException, NotMemberOfException;

    /**
     * Lists all users of a given group.
     *
     * @param group Group whose Users should be listed.
     * @return Set of Users of the given group.
     */
    Set<User> getUsersOfGroup(Group group);

    /**
     * Adds a user to a group.
     *
     * @param manager The user who is adding the new member. Needs to be at least a manager of the group.
     * @param group   Group to which to add the new member.
     * @param newUser User who is to be added to a group.
     * @throws WrongParameterException  if any of the parameters are null.
     * @throws NotMemberOfException     if the manager is not a member of the given group.
     * @throws GroupPermissionException if the manager does not have enough permissions in the group.
     * @throws AlreadyExistsException   if the newUser is already a member of the group.
     */
    void addMember(User manager, Group group, User newUser) throws WrongParameterException, NotMemberOfException, GroupPermissionException, AlreadyExistsException;

    /**
     * Removes a user from a group identified by a {@link User} object.
     * <p>
     * If manager is identical to toRemove, then the user is requesting their own removal and no group
     * permission checks are performed.
     *
     * @param manager  The user who is removing the member. Needs to be at least a manager of the group if different
     *                 from toRemove.
     * @param group    Group from which to remove the member.
     * @param toRemove User to be removed from the group.
     * @throws NotAllowedException      if the removed user is Admin.
     * @throws NotMemberOfException     if manager or toRemove are not members of the group.
     * @throws GroupPermissionException if the manager does not have enough permissions.
     * @throws WrongParameterException  if any of the parameters is null.
     */
    void removeMember(User manager, Group group, User toRemove) throws NotAllowedException, NotMemberOfException, GroupPermissionException, WrongParameterException;

    /**
     * Removes a member from a group identified by a {@link GroupMember} object.
     * <p>
     * If manager is identical to toRemove, then the user is requesting their own removal and no group
     * permission checks are performed.
     *
     * @param manager             The user who is removing the member. Needs to be at least a manager of the group if different
     *                            from toRemove.
     * @param group               Group from which to remove the member.
     * @param groupMemberToRemove GroupMember to be removed from the group.
     * @throws NotAllowedException      if the removed user is Admin.
     * @throws NotMemberOfException     if manager or toRemove are not members of the group.
     * @throws GroupPermissionException if the manager does not have enough permissions.
     * @throws WrongParameterException  if any of the parameters is null.
     */
    void removeMember(User manager, Group group, GroupMember groupMemberToRemove) throws WrongParameterException, NotMemberOfException, GroupPermissionException, NotAllowedException;

    /**
     * Adds a task as a shared task to the group.
     *
     * @param manager The user adding the task.
     * @param group   The group to which to add the task.
     * @param task    The task to be added.
     * @throws WrongParameterException  if any of the parameters is null.
     * @throws NotMemberOfException     if the manager is not a member of the group.
     * @throws GroupPermissionException if the manager does not have sufficient permissions.
     * @throws AlreadyExistsException   if the task is already shared with the group.
     */
    void addTask(User manager, Group group, Task task) throws WrongParameterException, NotMemberOfException, GroupPermissionException, AlreadyExistsException;

    /**
     * Removes a shared task from the group.
     * <p>
     * Also removes the task from all its members, if they were not participants on the task in an another way
     * (e.g. solo, or through another group).
     * <p>
     * All "orphan" {@link com.melkamar.deadlines.model.TaskParticipant} objects are destroyed.
     *
     * @param manager The user removing the task.
     * @param group   The group from which the task is removed.
     * @param task    The task being removed.
     * @throws WrongParameterException  if any of the parameters is null.
     * @throws NotMemberOfException     if the manager is not a member
     * @throws GroupPermissionException if the manager does not have sufficient permissions.
     * @throws NotAllowedException      if the task is not shared with the group.
     */
    void leaveTask(User manager, Group group, Task task) throws WrongParameterException, NotMemberOfException, GroupPermissionException, NotAllowedException;

    /**
     * Edits details of a group.
     *
     * @param admin          The user editing the group.
     * @param group          The group to be edited.
     * @param newDescription New description to add to the group.
     * @throws NotMemberOfException     if the admin is not a member of the broup.
     * @throws GroupPermissionException if the admin user does not have enough permissions in the group.
     */
    void editDetails(User admin, Group group, String newDescription) throws NotMemberOfException, GroupPermissionException;

    /**
     * Set the Manager role for a member of a group.
     *
     * @param executor The user making the call.
     * @param group    The group for which to change the role.
     * @param member   The member for whom to change the role.
     * @param newValue If true, the user will be made a Manager. If false, the user will be made a Member.
     * @throws GroupPermissionException if the executor does not have sufficient permissions.
     * @throws NotMemberOfException     if the executor or member are not members of the group.
     * @throws WrongParameterException  if any of the parameters are null.
     * @throws NotAllowedException      if the target user is an Admin of the group.
     */
    void setManager(User executor, Group group, User member, boolean newValue) throws GroupPermissionException, NotMemberOfException, WrongParameterException, NotAllowedException;

    /**
     * Changes the admin of the group.
     * <p>
     * The Admin role will be transferred from the old admin onto a new one.
     *
     * @param admin    The current admin of the group.
     * @param group    The group for which to change the admin.
     * @param newAdmin The user who will become the new admin.
     * @throws WrongParameterException  if any of the parameters is null.
     * @throws NotMemberOfException     if the admin or newAdmin is not a member of the group.
     * @throws GroupPermissionException if the admin is not an admin of the group.
     */
    void changeAdmin(User admin, Group group, User newAdmin) throws WrongParameterException, NotMemberOfException, GroupPermissionException;

    /**
     * Deletes a group.
     * <p>
     * All the group-related connections such as GroupMember objects and task sharing will be removed as well.
     * Work reports are not deleted.
     *
     * @param admin Admin of the group.
     * @param group The group to be deleted.
     * @throws NotMemberOfException     if admin is not a member of the group.
     * @throws GroupPermissionException if admin does not have sufficient permissions.
     * @throws WrongParameterException  if any of the parameters is null.
     */
    void deleteGroup(User admin, Group group) throws NotMemberOfException, GroupPermissionException, WrongParameterException;
}
