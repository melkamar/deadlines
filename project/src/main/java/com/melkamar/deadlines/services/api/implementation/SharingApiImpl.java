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

package com.melkamar.deadlines.services.api.implementation;

import com.melkamar.deadlines.config.StringConstants;
import com.melkamar.deadlines.dao.offer.OfferDAOHibernate;
import com.melkamar.deadlines.dao.offer.grouptask.GroupTaskSharingDAOHibernate;
import com.melkamar.deadlines.dao.offer.membership.MembershipSharingDAOHibernate;
import com.melkamar.deadlines.dao.offer.usertask.UserTaskSharingDAOHibernate;
import com.melkamar.deadlines.exceptions.*;
import com.melkamar.deadlines.model.*;
import com.melkamar.deadlines.model.offer.GroupTaskSharingOffer;
import com.melkamar.deadlines.model.offer.MembershipOffer;
import com.melkamar.deadlines.model.offer.UserTaskSharingOffer;
import com.melkamar.deadlines.model.task.Task;
import com.melkamar.deadlines.model.task.TaskRole;
import com.melkamar.deadlines.services.api.GroupApi;
import com.melkamar.deadlines.services.api.SharingApi;
import com.melkamar.deadlines.services.helpers.GroupMemberHelper;
import com.melkamar.deadlines.services.helpers.TaskParticipantHelper;
import com.melkamar.deadlines.services.security.PermissionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.Set;

/**
 * @author Martin Melka
 */
@Service("sharingApi")
@Transactional
public class SharingApiImpl implements SharingApi {
    @Autowired
    private PermissionHandler permissionHandler;
    @Autowired
    private StringConstants stringConstants;

    @Autowired
    private OfferDAOHibernate offerDao;
    @Autowired
    private GroupTaskSharingDAOHibernate groupTaskSharingDao;
    @Autowired
    private UserTaskSharingDAOHibernate userTaskSharingDao;
    @Autowired
    private MembershipSharingDAOHibernate membershipSharingDao;

    @Autowired
    private TaskParticipantHelper taskParticipantHelper;
    @Autowired
    private GroupMemberHelper groupMemberHelper;
    @Autowired
    private GroupApi groupApi;

    @Override
    public UserTaskSharingOffer offerTaskSharing(User offerer, Task task, User offeredTo) throws NotMemberOfException, AlreadyExistsException {
        // Check if offerer participates on task
        if (!permissionHandler.hasTaskPermission(offerer, task, TaskRole.WATCHER)) {
            // This should never happen as we are requesting the lowest permission
            return null;
        }

        if (isAlreadyOnTask(offeredTo, task)) {
            throw new AlreadyExistsException(MessageFormat.format(stringConstants.EXC_ALREADY_EXISTS_TASK_PARTICIPANT, offeredTo, task));
        }

        // Check if such offer already exists
        if (userTaskSharingDao.findByOfferedToAndTaskOffered(offeredTo, task) != null) {
            throw new AlreadyExistsException(MessageFormat.format(stringConstants.EXC_ALREADY_EXISTS_TASK_OFFER, task, offeredTo));
        }

        UserTaskSharingOffer offer = new UserTaskSharingOffer(offerer, task, offeredTo);
        offeredTo.addTaskSharingOffer(offer);
        offerDao.save(offer);
        return offer;
    }


    @Override
    public GroupTaskSharingOffer offerTaskSharing(User offerer, Task task, Group offeredTo) throws NotMemberOfException, AlreadyExistsException {
        // Check if offerer participates on task
        if (!permissionHandler.hasTaskPermission(offerer, task, TaskRole.WATCHER)) {
            // This should never happen as we are requesting the lowest permission
            return null;
        }

        if (isAlreadyOnTask(offeredTo, task)) {
            throw new AlreadyExistsException(MessageFormat.format(stringConstants.EXC_ALREADY_EXISTS_TASK_OF_GROUP, task, offeredTo));
        }

        // Check if such offer already exists
        if (groupTaskSharingDao.findByOfferedToAndTaskOffered(offeredTo, task) != null) {
            throw new AlreadyExistsException(MessageFormat.format(stringConstants.EXC_ALREADY_EXISTS_TASK_OFFER, task, offeredTo));
        }

        GroupTaskSharingOffer offer = new GroupTaskSharingOffer(offerer, task, offeredTo);
        offeredTo.addTaskSharingOffer(offer);
        offerDao.save(offer);

        return offer;
    }

    @Override
    public MembershipOffer offerMembership(User offerer, Group group, User offeredTo) throws NotMemberOfException, GroupPermissionException, AlreadyExistsException {
        // Check if offerer has enough permissions
        if (!permissionHandler.hasGroupPermission(offerer, group, MemberRole.MANAGER)) {
            throw new GroupPermissionException(MessageFormat.format(stringConstants.EXC_GROUP_PERMISSION, MemberRole.MANAGER, offerer, group));
        }

        // Check if offeredTo is not already in group
        if (groupMemberHelper.getGroupMember(offeredTo, group) != null) {
            throw new AlreadyExistsException(MessageFormat.format(stringConstants.EXC_ALREADY_EXISTS_GROUP_MEMBER, offeredTo, group));
        }

        if (membershipSharingDao.findByOfferedToAndGroup(offeredTo, group) != null) {
            throw new AlreadyExistsException(MessageFormat.format(stringConstants.EXC_ALREADY_EXISTS_TASK_OFFER, offeredTo, group));
        }

        MembershipOffer offer = new MembershipOffer(offerer, group, offeredTo);
        offerDao.save(offer);
        offeredTo.addMembershipOffer(offer);
        group.addMembershipOffer(offer);
        return offer;
    }


    @Override
    public Set<UserTaskSharingOffer> listTaskOffersOfUser(User user) {
        return userTaskSharingDao.findByOfferedTo(user);
    }

    @Override
    public Set<GroupTaskSharingOffer> listTaskOffersOfGroup(User manager, Group group) throws NotMemberOfException, GroupPermissionException {
        // Check if user has enough permissions
        if (!permissionHandler.hasGroupPermission(manager, group, MemberRole.MANAGER))
            throw new GroupPermissionException(MessageFormat.format(stringConstants.EXC_GROUP_PERMISSION, MemberRole.MANAGER, manager, group));

        return groupTaskSharingDao.findByOfferedTo(group);
    }

    @Override
    public Set<MembershipOffer> listMembershipOffersOfUser(User user) {
        return membershipSharingDao.findByOfferedTo(user);
    }

    @Override
    public Task resolveTaskSharingOffer(User user, UserTaskSharingOffer offer, boolean accept) throws NotMemberOfException, WrongParameterException {
        permissionHandler.checkOfferOwnership(user, offer);
        // Passed - permission to do this ok

        if (!accept) {
            // If declined, delete
            deleteOffer(offer);
            return null;
        }

        Task taskOffered = offer.getTaskOffered();
        taskParticipantHelper.editOrCreateTaskParticipant(user, taskOffered, TaskRole.WATCHER, null, false);
        deleteOffer(offer);
        return taskOffered;
//        }
    }

    @Override
    public Task resolveTaskSharingOffer(Group group, User manager, GroupTaskSharingOffer offer, boolean accept) throws NotMemberOfException, WrongParameterException, AlreadyExistsException, GroupPermissionException {
        permissionHandler.checkOfferOwnership(group, offer);

        if (!permissionHandler.hasGroupPermission(manager, group, MemberRole.MANAGER)) {
            throw new GroupPermissionException(MessageFormat.format(stringConstants.EXC_GROUP_PERMISSION, MemberRole.MANAGER, manager, group));
        }
        // Passed - permission to do this ok

        if (!accept) {
            // If declined, delete
            deleteOffer(offer);
            return null;
        }

        try {
            groupApi.addTask(manager, group, offer.getTaskOffered());
            return offer.getTaskOffered();
        } catch (NotMemberOfException | GroupPermissionException | WrongParameterException | AlreadyExistsException e) {
            deleteOffer(offer);
            throw e;
        }
    }

    @Override
    public Group resolveMembershipOffer(User user, MembershipOffer offer, boolean accept) throws NotMemberOfException, WrongParameterException, AlreadyExistsException, GroupPermissionException {
        permissionHandler.checkOfferOwnership(user, offer);
        // Passed - permission to do this ok

        if (!accept) {
            // If declined, delete
            deleteOffer(offer);
            return null;
        }

        try {
            groupApi.addMember(offer.getOfferer(), offer.getGroup(), user);
            return offer.getGroup();
        } catch (GroupPermissionException | AlreadyExistsException | NotMemberOfException e) {
            deleteOffer(offer);
            throw e;
        }

    }

    private void deleteOffer(UserTaskSharingOffer offer) {
        offer.getOfferedTo().removeTaskSharingOffer(offer);
        offerDao.delete(offer);
    }

    private void deleteOffer(GroupTaskSharingOffer offer) {
        offer.getOfferedTo().removeTaskSharingOffer(offer);
        offerDao.delete(offer);
    }

    private void deleteOffer(MembershipOffer offer) {
        offer.getOfferedTo().removeMembershipOffer(offer);
        offerDao.delete(offer);
    }

    private boolean isAlreadyOnTask(User offeredTo, Task task) {
        TaskParticipant offeredToParticipant = taskParticipantHelper.getTaskParticipant(offeredTo, task);
        if (offeredToParticipant != null && offeredToParticipant.getSolo()) {
            return true;
        }

        return false;
    }

    private boolean isAlreadyOnTask(Group offeredTo, Task task) throws AlreadyExistsException {
        // Check if the group is already on the task. If yes, exception.
        if (offeredTo.getSharedTasks().contains(task)) {
            return true;
        }

        return false;
    }

    private boolean isAlreadyOnGroup(User offeredTo, Group group) throws AlreadyExistsException {
        GroupMember groupMember = groupMemberHelper.getGroupMember(offeredTo, group);
        // Check if the group is already on the task. If yes, exception.
        if (groupMember != null) {
            return true;
        }

        return false;
    }

    @Override
    public UserTaskSharingOffer getUserTaskSharingOffer(Long id) throws DoesNotExistException {
        UserTaskSharingOffer offer = userTaskSharingDao.findById(id);
        if (offer == null) {
            throw new DoesNotExistException(MessageFormat.format(stringConstants.EXC_DOES_NOT_EXIST_OFFER, id));
        } else {
            return offer;
        }
    }

    @Override
    public MembershipOffer getMembershipOffer(Long id) throws DoesNotExistException {
        MembershipOffer offer = membershipSharingDao.findById(id);
        if (offer == null) {
            throw new DoesNotExistException(MessageFormat.format(stringConstants.EXC_DOES_NOT_EXIST_OFFER, id));
        } else {
            return offer;
        }
    }

    @Override
    public GroupTaskSharingOffer getGroupTaskSharingOffer(Long id) throws DoesNotExistException {
        GroupTaskSharingOffer offer = groupTaskSharingDao.findById(id);
        if (offer == null) {
            throw new DoesNotExistException(MessageFormat.format(stringConstants.EXC_DOES_NOT_EXIST_OFFER, id));
        } else {
            return offer;
        }
    }

}
