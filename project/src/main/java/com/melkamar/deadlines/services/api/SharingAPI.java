package com.melkamar.deadlines.services.api;

import com.melkamar.deadlines.config.StringConstants;
import com.melkamar.deadlines.dao.offer.OfferDAOHibernate;
import com.melkamar.deadlines.dao.offer.grouptask.GroupTaskSharingDAOHibernate;
import com.melkamar.deadlines.dao.offer.membership.MembershipSharingDAOHibernate;
import com.melkamar.deadlines.dao.offer.usertask.UserTaskSharingDAOHibernate;
import com.melkamar.deadlines.exceptions.AlreadyExistsException;
import com.melkamar.deadlines.exceptions.GroupPermissionException;
import com.melkamar.deadlines.exceptions.NotMemberOfException;
import com.melkamar.deadlines.exceptions.WrongParameterException;
import com.melkamar.deadlines.model.*;
import com.melkamar.deadlines.model.offer.GroupTaskSharingOffer;
import com.melkamar.deadlines.model.offer.MembershipOffer;
import com.melkamar.deadlines.model.offer.UserTaskSharingOffer;
import com.melkamar.deadlines.model.task.Task;
import com.melkamar.deadlines.model.task.TaskRole;
import com.melkamar.deadlines.services.PermissionHandler;
import com.melkamar.deadlines.services.helpers.GroupMemberHelper;
import com.melkamar.deadlines.services.helpers.TaskParticipantHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.Set;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 31.03.2016 10:27
 */
@Service
public class SharingAPI {
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
    private GroupAPI groupAPI;

    @Transactional
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


    @Transactional
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

    @Transactional
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


    public Set<UserTaskSharingOffer> listTaskOffersOfUser(User user) {
        return userTaskSharingDao.findByOfferedTo(user);
    }

    public Set<GroupTaskSharingOffer> listTaskOffersOfGroup(User manager, Group group) throws NotMemberOfException, GroupPermissionException {
        // Check if user has enough permissions
        if (!permissionHandler.hasGroupPermission(manager, group, MemberRole.MANAGER))
            throw new GroupPermissionException(MessageFormat.format(stringConstants.EXC_GROUP_PERMISSION, MemberRole.MANAGER, manager, group));

        return groupTaskSharingDao.findByOfferedTo(group);
    }

    public Set<MembershipOffer> listMembershipOffersOfUser(User user) {
        return membershipSharingDao.findByOfferedTo(user);
    }

    public void resolveTaskSharingOffer(User user, UserTaskSharingOffer offer, boolean accept) throws NotMemberOfException, WrongParameterException {
        permissionHandler.checkOfferOwnership(user, offer);
        // Passed - permission to do this ok

        if (!accept) {
            // If declined, delete
            deleteOffer(offer);
            return;
        }

        // TODO: 01.04.2016 See if it works with this commented out. It should not matter if the user is already on task, as nothing will get rewritten
//        if (isAlreadyOnTask(user, offer.getTaskOffered())) {
//            deleteOffer(offer);
//            throw new AlreadyExistsException(MessageFormat.format(stringConstants.EXC_ALREADY_EXISTS_TASK_PARTICIPANT, user, offer.getTaskOffered()));
//        } else {
        taskParticipantHelper.editOrCreateTaskParticipant(user, offer.getTaskOffered(), TaskRole.WATCHER, null, false);

        deleteOffer(offer);

//        }
    }


    public void resolveTaskSharingOffer(Group group, User manager, GroupTaskSharingOffer offer, boolean accept) throws NotMemberOfException, WrongParameterException, AlreadyExistsException, GroupPermissionException {
        permissionHandler.checkOfferOwnership(group, offer);

        if (!permissionHandler.hasGroupPermission(manager, group, MemberRole.MANAGER)) {
            throw new GroupPermissionException(MessageFormat.format(stringConstants.EXC_GROUP_PERMISSION, MemberRole.MANAGER, manager, group));
        }
        // Passed - permission to do this ok

        if (!accept) {
            // If declined, delete
            deleteOffer(offer);
            return;
        }

        try {
            groupAPI.addTask(manager, group, offer.getTaskOffered());
        } catch (NotMemberOfException | GroupPermissionException | WrongParameterException | AlreadyExistsException e) {
            deleteOffer(offer);
            throw e;
        }
    }

    public void resolveMembershipOffer(User user, MembershipOffer offer, boolean accept) throws NotMemberOfException, WrongParameterException, AlreadyExistsException, GroupPermissionException {
        permissionHandler.checkOfferOwnership(user, offer);
        // Passed - permission to do this ok

        if (!accept) {
            // If declined, delete
            deleteOffer(offer);
            return;
        }

        try {
            groupAPI.addMember(offer.getOfferer(), offer.getGroup(), user);
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

}
