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
 */
public interface GroupAPI {
    public Group createGroup(String name, User founder, String description) throws WrongParameterException, AlreadyExistsException;
    public boolean setManager(User executor, Group group, User member, boolean newValue) throws GroupPermissionException, NotMemberOfException, WrongParameterException, NotAllowedException;
    public List<Group> listGroups();
    public List<Group> listGroups(User user);
    public List<Group> listGroups(User user, MemberRole role);
    public Group getGroup(Long groupId) throws DoesNotExistException;
    public Group getGroup(Long groupId, User user) throws DoesNotExistException, NotMemberOfException;
    public List<Task> listTasks(User user, Group group, TaskOrdering ordering, TaskFilter... filters) throws NotMemberOfException, GroupPermissionException;
    public void addMember(User manager, Group group, User newUser) throws WrongParameterException, NotMemberOfException, GroupPermissionException, AlreadyExistsException;
    public void removeMember(User manager, Group group, User toRemove) throws NotAllowedException, NotMemberOfException, GroupPermissionException, WrongParameterException;
    public void removeMember(User manager, Group group, GroupMember groupMemberToRemove) throws WrongParameterException, NotMemberOfException, GroupPermissionException, NotAllowedException;
    public void addTask(User manager, Group group, Task task) throws WrongParameterException, NotMemberOfException, GroupPermissionException, AlreadyExistsException;
    public void leaveTask(User manager, Group group, Task task) throws WrongParameterException, NotMemberOfException, GroupPermissionException;
    public void editDetails(User admin, Group group, String newDescription) throws NotMemberOfException, GroupPermissionException;
    public void changeAdmin(User admin, Group group, User newAdmin) throws WrongParameterException, NotMemberOfException, GroupPermissionException;
    public void deleteGroup(User admin, Group group) throws NotMemberOfException, GroupPermissionException, WrongParameterException;
    public Set<User> getUsersOfGroup(Group group);
}
