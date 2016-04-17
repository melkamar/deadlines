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
 *
 * {@link com.melkamar.deadlines.services.api.implementation.SharingApiImpl}
 */
public interface SharingApi {
    UserTaskSharingOffer offerTaskSharing(User offerer, Task task, User offeredTo) throws NotMemberOfException, AlreadyExistsException;
    GroupTaskSharingOffer offerTaskSharing(User offerer, Task task, Group offeredTo) throws NotMemberOfException, AlreadyExistsException;
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
