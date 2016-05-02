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
import com.melkamar.deadlines.model.offer.GroupTaskSharingOffer;
import com.melkamar.deadlines.model.offer.MembershipOffer;
import com.melkamar.deadlines.model.offer.UserTaskSharingOffer;
import com.melkamar.deadlines.model.task.Task;

import java.util.Set;

/**
 * @author Martin Melka
 */
public interface SharingApi {
    /**
     * Offers a task sharing to a user.
     *
     * @param offerer The user offering the task.
     * @param task The task being offered.
     * @param offeredTo The user to whom the task is being offered.
     * @return UserTaskSharingOffer object representing the offer.
     * @throws NotMemberOfException if offerer is not a participant on the task.
     * @throws AlreadyExistsException if the offeredTo user is already a participant on the task.
     */
    UserTaskSharingOffer offerTaskSharing(User offerer, Task task, User offeredTo) throws NotMemberOfException, AlreadyExistsException;

    /**
     * Offers a task sharing to a group.
     *
     * @param offerer The user offering the task.
     * @param task The task being offered.
     * @param offeredTo The group to which the task is being offered.
     * @return GroupTaskSharingOffer object representing the offer.
     * @throws NotMemberOfException if offerer is not a participant on the task.
     * @throws AlreadyExistsException if the group is already participating on the task.
     */
    GroupTaskSharingOffer offerTaskSharing(User offerer, Task task, Group offeredTo) throws NotMemberOfException, AlreadyExistsException;

    /**
     * Offers
     *
     * @param offerer
     * @param group
     * @param offeredTo
     * @return
     * @throws NotMemberOfException
     * @throws GroupPermissionException
     * @throws AlreadyExistsException
     */
    MembershipOffer offerMembership(User offerer, Group group, User offeredTo) throws NotMemberOfException, GroupPermissionException, AlreadyExistsException;
    //
    Set<UserTaskSharingOffer> listTaskOffersOfUser(User user);
    Set<GroupTaskSharingOffer> listTaskOffersOfGroup(User manager, Group group) throws NotMemberOfException, GroupPermissionException;
    Set<MembershipOffer> listMembershipOffersOfUser(User user);
    //
    Task resolveTaskSharingOffer(User user, UserTaskSharingOffer offer, boolean accept) throws NotMemberOfException, WrongParameterException;
    Task resolveTaskSharingOffer(Group group, User manager, GroupTaskSharingOffer offer, boolean accept) throws NotMemberOfException, WrongParameterException, AlreadyExistsException, GroupPermissionException;
    Group resolveMembershipOffer(User user, MembershipOffer offer, boolean accept) throws NotMemberOfException, WrongParameterException, AlreadyExistsException, GroupPermissionException;
    //
    UserTaskSharingOffer getUserTaskSharingOffer(Long id) throws DoesNotExistException;
    MembershipOffer getMembershipOffer(Long id) throws DoesNotExistException;
    GroupTaskSharingOffer getGroupTaskSharingOffer(Long id) throws DoesNotExistException;
}
