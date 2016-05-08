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
     * @param offerer   The user offering the task.
     * @param task      The task being offered.
     * @param offeredTo The user to whom the task is being offered.
     * @return UserTaskSharingOffer object representing the offer.
     * @throws NotMemberOfException   if offerer is not a participant on the task.
     * @throws AlreadyExistsException if the offeredTo user is already a participant on the task.
     */
    UserTaskSharingOffer offerTaskSharing(User offerer, Task task, User offeredTo) throws NotMemberOfException, AlreadyExistsException;

    /**
     * Offers a task sharing to a group.
     *
     * @param offerer   The user offering the task.
     * @param task      The task being offered.
     * @param offeredTo The group to which the task is being offered.
     * @return GroupTaskSharingOffer object representing the offer.
     * @throws NotMemberOfException   if offerer is not a participant on the task.
     * @throws AlreadyExistsException if the group is already participating on the task.
     */
    GroupTaskSharingOffer offerTaskSharing(User offerer, Task task, Group offeredTo) throws NotMemberOfException, AlreadyExistsException;

    /**
     * Offers a group membership to a user.
     *
     * @param offerer   The user offering the membership.
     * @param group     The group being offered.
     * @param offeredTo The user to whom the membership is being offered.
     * @return MembershipOffer object representing the offer.
     * @throws NotMemberOfException     if offerer is not a member of the group.
     * @throws GroupPermissionException if offerer does not have enough permissions in the group.
     * @throws AlreadyExistsException   if the offeredTo is already a member of the group.
     */
    MembershipOffer offerMembership(User offerer, Group group, User offeredTo) throws NotMemberOfException, GroupPermissionException, AlreadyExistsException;

    /**
     * Lists all task offers of a user.
     *
     * @param user The user whose offers should be listed.
     * @return All UserTaskSharingOffer objects associated with the user.
     */
    Set<UserTaskSharingOffer> listTaskOffersOfUser(User user);

    /**
     * Lists all task offers of a group.
     *
     * @param manager The user performing the listing.
     * @param group   The group of which the offers should be listed.
     * @return All UserTaskSharingOffer objects associated with the user.
     * @throws NotMemberOfException     if manager is not a member of the group.
     * @throws GroupPermissionException if manager does not have enough permissions in the group.
     */
    Set<GroupTaskSharingOffer> listTaskOffersOfGroup(User manager, Group group) throws NotMemberOfException, GroupPermissionException;

    /**
     * Lists all membership offers of a user.
     *
     * @param user The user whose offers should be listed.
     * @return All membership offers of the user.
     */
    Set<MembershipOffer> listMembershipOffersOfUser(User user);

    /**
     * Accepts or rejects a task sharing offer for a user.
     *
     * @param user   The user who is being offered a task.
     * @param offer  The offer object representing the offer.
     * @param accept Boolean value indicating whether to accept (true) or reject (false) the offer.
     * @return If accepted, the shared Task object is returned. If declined, null is returned.
     * @throws NotMemberOfException    if the user is not the recipient of the offer.
     * @throws WrongParameterException if the user or offer are null.
     */
    Task resolveTaskSharingOffer(User user, UserTaskSharingOffer offer, boolean accept) throws NotMemberOfException, WrongParameterException;

    /**
     * Accepts or rejects a task sharing offer for a group.
     *
     * @param group   The group to which a task is offered.
     * @param manager The manager making the call.
     * @param offer   The offer object representing the offer.
     * @param accept  Boolean value indicating whether to accept (true) or reject (false) the offer.
     * @return If accepted, the shared Task object is returned. If declined, null is returned.
     * @throws NotMemberOfException     if the group is not the recipient of the offer.
     * @throws WrongParameterException  if the group, manager or offer are null.
     * @throws AlreadyExistsException   if the task is already shared with the group.
     * @throws GroupPermissionException if the manager does not have sufficient permissions in the group.
     */
    Task resolveTaskSharingOffer(Group group, User manager, GroupTaskSharingOffer offer, boolean accept) throws NotMemberOfException, WrongParameterException, AlreadyExistsException, GroupPermissionException;

    /**
     * Accepts of rejects a membership offer for a user.
     *
     * @param user   The user who is being offered a membership.
     * @param offer  The offer object representing the offer.
     * @param accept Boolean value indicating whether to accept (true) or reject (false) the offer.
     * @return If accepted, the shared Group object is returned. If declined, null is returned.
     * @throws NotMemberOfException     if the user is not the recipient of the offer.
     * @throws WrongParameterException  if the user or offer are null.
     * @throws AlreadyExistsException   if the user is already a member of the group.
     * @throws GroupPermissionException if the original offerer does not have enough permissions in the group anymore.
     */
    Group resolveMembershipOffer(User user, MembershipOffer offer, boolean accept) throws NotMemberOfException, WrongParameterException, AlreadyExistsException, GroupPermissionException;


    /**
     * Retrieves a {@link UserTaskSharingOffer} instance based on its id.
     *
     * @param id ID of the offer to find.
     * @return UserTaskSharingOffer if it exists.
     * @throws DoesNotExistException if the offer with the given ID does not exist.
     */
    UserTaskSharingOffer getUserTaskSharingOffer(Long id) throws DoesNotExistException;

    /**
     * Retrieves a {@link MembershipOffer} instance based on its id.
     *
     * @param id ID of the offer to find.
     * @return MembershipOffer if it exists.
     * @throws DoesNotExistException if the offer with the given ID does not exist.
     */
    MembershipOffer getMembershipOffer(Long id) throws DoesNotExistException;

    /**
     * Retrieves a {@link GroupTaskSharingOffer} instance based on its id.
     *
     * @param id ID of the offer to find.
     * @return GroupTaskSharingOffer if it exists.
     * @throws DoesNotExistException if the offer with the given ID does not exist.
     */
    GroupTaskSharingOffer getGroupTaskSharingOffer(Long id) throws DoesNotExistException;
}
