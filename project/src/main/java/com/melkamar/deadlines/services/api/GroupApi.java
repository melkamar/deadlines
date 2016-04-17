package com.melkamar.deadlines.services.api;

import com.melkamar.deadlines.dao.processing.TaskFilter;
import com.melkamar.deadlines.dao.processing.TaskOrdering;
import com.melkamar.deadlines.exceptions.*;
import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.GroupMember;
import com.melkamar.deadlines.model.MemberRole;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.task.Task;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 11.04.2016 13:48
 *
 * {@link com.melkamar.deadlines.services.api.implementation.GroupApiImpl}
 */
public interface GroupApi {
    Group createGroup(String name, User founder, String description) throws WrongParameterException, AlreadyExistsException;
    //
    List<Group> listGroups();
    List<Group> listGroups(User user);
    List<Group> listGroups(User user, MemberRole role);
    Group getGroup(Long groupId) throws DoesNotExistException;
    Group getGroup(Long groupId, User user) throws DoesNotExistException, NotMemberOfException;
    //
    Set<User> getUsersOfGroup(Group group);
    void addMember(User manager, Group group, User newUser) throws WrongParameterException, NotMemberOfException, GroupPermissionException, AlreadyExistsException;
    void removeMember(User manager, Group group, User toRemove) throws NotAllowedException, NotMemberOfException, GroupPermissionException, WrongParameterException;
    void removeMember(User manager, Group group, GroupMember groupMemberToRemove) throws WrongParameterException, NotMemberOfException, GroupPermissionException, NotAllowedException;
    //
    void addTask(User manager, Group group, Task task) throws WrongParameterException, NotMemberOfException, GroupPermissionException, AlreadyExistsException;
    void leaveTask(User manager, Group group, Task task) throws WrongParameterException, NotMemberOfException, GroupPermissionException;
    //
    void editDetails(User admin, Group group, String newDescription) throws NotMemberOfException, GroupPermissionException;
    void setManager(User executor, Group group, User member, boolean newValue) throws GroupPermissionException, NotMemberOfException, WrongParameterException, NotAllowedException;
    void changeAdmin(User admin, Group group, User newAdmin) throws WrongParameterException, NotMemberOfException, GroupPermissionException;
    void deleteGroup(User admin, Group group) throws NotMemberOfException, GroupPermissionException, WrongParameterException;
}
