package com.melkamar.deadlines.services.api;

import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.offer.GroupTaskSharingOffer;
import com.melkamar.deadlines.model.offer.MembershipOffer;
import com.melkamar.deadlines.model.offer.TaskSharingOffer;
import com.melkamar.deadlines.model.offer.UserTaskSharingOffer;
import com.melkamar.deadlines.model.task.Task;
import org.springframework.stereotype.Service;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;
import java.util.Set;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 31.03.2016 10:27
 */
@Service
public class SharingAPI {
    public Task offerTaskSharing(User sharer, Task task, User shareWithUser){
        // TODO: 31.03.2016 Implement
        throw new NotImplementedException();
    }

    public Task offerTaskSharing(User sharer, Task task, Group shareWithGroup){
        // TODO: 31.03.2016 Implement
        throw new NotImplementedException();
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
