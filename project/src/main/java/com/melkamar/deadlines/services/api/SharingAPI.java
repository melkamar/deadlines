package com.melkamar.deadlines.services.api;

import com.melkamar.deadlines.config.StringConstants;
import com.melkamar.deadlines.dao.offer.OfferDAOHibernate;
import com.melkamar.deadlines.dao.offer.grouptask.GroupTaskSharingDAOHibernate;
import com.melkamar.deadlines.dao.offer.membership.MembershipSharingDAOHibernate;
import com.melkamar.deadlines.dao.offer.usertask.UserTaskSharingDAOHibernate;
import com.melkamar.deadlines.exceptions.AlreadyExistsException;
import com.melkamar.deadlines.exceptions.GroupPermissionException;
import com.melkamar.deadlines.exceptions.NotMemberOfException;
import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.MemberRole;
import com.melkamar.deadlines.model.TaskParticipant;
import com.melkamar.deadlines.model.User;
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
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.text.MessageFormat;
import java.util.List;
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
    private TaskParticipantHelper taskParticipantHelper;
    @Autowired
    private OfferDAOHibernate offerDao;
    @Autowired
    private GroupMemberHelper groupMemberHelper;
    @Autowired
    private GroupTaskSharingDAOHibernate groupTaskSharingDao;
    @Autowired
    private UserTaskSharingDAOHibernate userTaskSharingDao;
    @Autowired
    private MembershipSharingDAOHibernate membershipSharingDao;

    @Transactional
    public void offerTaskSharing(User offerer, Task task, User offeredTo) throws NotMemberOfException, AlreadyExistsException {
        // Check if offerer participates on task
        if (!permissionHandler.hasTaskPermission(offerer, task, TaskRole.WATCHER)){
            // This should never happen as we are requesting the lowest permission
            return;
        }

        // Check if the user is already on the task. If yes, exception.
        TaskParticipant offeredToParticipant = taskParticipantHelper.getTaskParticipant(offeredTo, task);
        if (offeredToParticipant!=null && offeredToParticipant.getSolo()){
            throw new AlreadyExistsException(MessageFormat.format(stringConstants.EXC_ALREADY_EXISTS_TASK_PARTICIPANT, offeredTo, task));
        }

        // Check if such offer already exists
        if (userTaskSharingDao.findByOfferedToAndTaskOffered(offeredTo, task) != null){
            throw new AlreadyExistsException(MessageFormat.format(stringConstants.EXC_ALREADY_EXISTS_TASK_OFFER, task, offeredTo));
        }

        UserTaskSharingOffer offer = new UserTaskSharingOffer(offerer, task, offeredTo);
        offeredTo.addTaskSharingOffer(offer);
        offerDao.save(offer);
    }

    @Transactional
    public void offerTaskSharing(User offerer, Task task, Group offeredTo) throws NotMemberOfException, AlreadyExistsException {
        // Check if offerer participates on task
        if (!permissionHandler.hasTaskPermission(offerer, task, TaskRole.WATCHER)){
            // This should never happen as we are requesting the lowest permission
            return;
        }

        // Check if the group is already on the task. If yes, exception.
        if (offeredTo.getSharedTasks().contains(task)){
            throw new AlreadyExistsException(MessageFormat.format(stringConstants.EXC_ALREADY_EXISTS_TASK_OF_GROUP, task, offeredTo));
        }

        // Check if such offer already exists
        if (groupTaskSharingDao.findByOfferedToAndTaskOffered(offeredTo, task) != null){
            throw new AlreadyExistsException(MessageFormat.format(stringConstants.EXC_ALREADY_EXISTS_TASK_OFFER, task, offeredTo));
        }

        GroupTaskSharingOffer offer = new GroupTaskSharingOffer(offerer, task, offeredTo);
        offeredTo.addTaskSharingOffer(offer);
        offerDao.save(offer);
    }

    @Transactional
    public void offerMembership(User offerer, Group group, User offeredTo) throws NotMemberOfException, GroupPermissionException, AlreadyExistsException {
        // Check if offerer has enough permissions
        if (!permissionHandler.hasGroupPermission(offerer, group, MemberRole.MANAGER)){
            throw new GroupPermissionException(MessageFormat.format(stringConstants.EXC_GROUP_PERMISSION, MemberRole.MANAGER, offerer, group));
        }

        // Check if offeredTo is not already in group
        if (groupMemberHelper.getGroupMember(offeredTo, group) != null){
            throw new AlreadyExistsException(MessageFormat.format(stringConstants.EXC_ALREADY_EXISTS_GROUP_MEMBER, offeredTo, group));
        }

        if (membershipSharingDao.findByOfferedToAndGroup(offeredTo, group)!= null){
            throw new AlreadyExistsException(MessageFormat.format(stringConstants.EXC_ALREADY_EXISTS_TASK_OFFER, offeredTo, group));
        }

        MembershipOffer offer = new MembershipOffer(offerer, group, offeredTo);
        offerDao.save(offer);
        offeredTo.addMembershipOffer(offer);
    }

    public Task resolveTaskSharingOffer(User sharedWith, UserTaskSharingOffer offer, boolean accept){
        // TODO: 31.03.2016 Implement
        throw new NotImplementedException();
    }

    public Set<UserTaskSharingOffer> listTaskOffersOfUser(User sharedWith){
        // TODO: 31.03.2016 Implement
        throw new NotImplementedException();
    }

    public Task resolveMembershipOffer(User sharedWith, MembershipOffer offer, boolean accept){
        // TODO: 31.03.2016 Implement
        throw new NotImplementedException();
    }

    public Set<MembershipOffer> listMembershipOffersOfUser(User sharedWith){
        // TODO: 31.03.2016 Implement
        throw new NotImplementedException();
    }

    public List<GroupTaskSharingOffer> listTaskOffersOfGroup(User manager, Group group){
        // TODO: 31.03.2016 Implement
        throw new NotImplementedException();
    }

    public Task resolveTaskSharingOffer(Group sharedWith, User manager, GroupTaskSharingOffer offer, boolean accept){
        // TODO: 31.03.2016 Implement
        throw new NotImplementedException();
    }

}
