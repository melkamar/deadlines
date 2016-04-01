package com.melkamar.deadlines.services.api;

import com.melkamar.deadlines.config.StringConstants;
import com.melkamar.deadlines.dao.offer.OfferDAOHibernate;
import com.melkamar.deadlines.exceptions.AlreadyExistsException;
import com.melkamar.deadlines.exceptions.NotMemberOfException;
import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.TaskParticipant;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.offer.GroupTaskSharingOffer;
import com.melkamar.deadlines.model.offer.MembershipOffer;
import com.melkamar.deadlines.model.offer.UserTaskSharingOffer;
import com.melkamar.deadlines.model.task.Task;
import com.melkamar.deadlines.model.task.TaskRole;
import com.melkamar.deadlines.services.PermissionHandler;
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

        GroupTaskSharingOffer offer = new GroupTaskSharingOffer(offerer, task, offeredTo);
        offeredTo.addTaskSharingOffer(offer);
        offerDao.save(offer);
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

    public void offerMembership(User manager, Group group, User offeredTo){
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
