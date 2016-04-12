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
 * Created by Martin Melka (martin.melka@gmail.com)
 * 11.04.2016 14:23
 */
public interface SharingApi {
    public UserTaskSharingOffer offerTaskSharing(User offerer, Task task, User offeredTo) throws NotMemberOfException, AlreadyExistsException;
    public GroupTaskSharingOffer offerTaskSharing(User offerer, Task task, Group offeredTo) throws NotMemberOfException, AlreadyExistsException;
    public MembershipOffer offerMembership(User offerer, Group group, User offeredTo) throws NotMemberOfException, GroupPermissionException, AlreadyExistsException;
    public Set<UserTaskSharingOffer> listTaskOffersOfUser(User user);
    public Set<GroupTaskSharingOffer> listTaskOffersOfGroup(User manager, Group group) throws NotMemberOfException, GroupPermissionException;
    public Set<MembershipOffer> listMembershipOffersOfUser(User user);
    public Task resolveTaskSharingOffer(User user, UserTaskSharingOffer offer, boolean accept) throws NotMemberOfException, WrongParameterException;
    public Task resolveTaskSharingOffer(Group group, User manager, GroupTaskSharingOffer offer, boolean accept) throws NotMemberOfException, WrongParameterException, AlreadyExistsException, GroupPermissionException;
    public Group resolveMembershipOffer(User user, MembershipOffer offer, boolean accept) throws NotMemberOfException, WrongParameterException, AlreadyExistsException, GroupPermissionException;
    public UserTaskSharingOffer getUserTaskSharingOffer(Long id) throws DoesNotExistException;
    public MembershipOffer getMembershipOffer(Long id) throws DoesNotExistException;
    public GroupTaskSharingOffer getGroupTaskSharingOffer(Long id) throws DoesNotExistException;
}
