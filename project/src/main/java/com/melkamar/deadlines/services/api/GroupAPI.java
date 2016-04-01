package com.melkamar.deadlines.services.api;

import com.melkamar.deadlines.config.StringConstants;
import com.melkamar.deadlines.dao.processing.GroupFilter;
import com.melkamar.deadlines.dao.group.GroupDAO;
import com.melkamar.deadlines.dao.groupmember.GroupMemberDAO;
import com.melkamar.deadlines.dao.taskparticipant.TaskParticipantDAOHibernate;
import com.melkamar.deadlines.dao.user.UserDAO;
import com.melkamar.deadlines.exceptions.*;
import com.melkamar.deadlines.model.*;
import com.melkamar.deadlines.model.task.Task;
import com.melkamar.deadlines.model.task.TaskRole;
import com.melkamar.deadlines.services.PermissionHandler;
import com.melkamar.deadlines.services.helpers.GroupMemberHelper;
import com.melkamar.deadlines.services.helpers.TaskParticipantHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 26.03.2016 15:52
 */
@Service
public class GroupAPI {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private StringConstants stringConstants;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private GroupDAO groupDAO;

    @Autowired
    private GroupMemberHelper groupMemberHelper;

    @Autowired
    private PermissionHandler permissionHandler;
    @Autowired
    private GroupMemberDAO groupMemberDAO;
    @Autowired
    private TaskParticipantDAOHibernate taskparticipantDAO;
    @Autowired
    private TaskParticipantHelper taskParticipantHelper;


    @Transactional
    public Group createGroup(String name, User founder, String description) throws WrongParameterException {
        if (name == null || name.isEmpty()) throw new WrongParameterException(stringConstants.EXC_PARAM_NAME_EMPTY);
        if (founder == null) throw new WrongParameterException(stringConstants.EXC_PARAM_FOUNDER_NULL);

        Group group = new Group(name);
        group.setDescription(description);

        groupDAO.save(group);

        try {
            groupMemberHelper.createGroupMember(founder, group, MemberRole.ADMIN);
        } catch (AlreadyExistsException e) {
            org.apache.log4j.Logger.getLogger(this.getClass()).error("User is already a member of newly created group! This should not happen.");
            e.printStackTrace();
        }
        return group;
    }

    public boolean setManager(User executor, Group group, User member, boolean newValue) throws GroupPermissionException, NotMemberOfException, WrongParameterException, NotAllowedException {
        if (executor == null || group == null || member == null) {
            throw new WrongParameterException(stringConstants.EXC_PARAM_ALL_NEED_NOT_NULL);
        }

        if (!permissionHandler.hasGroupPermission(executor, group, MemberRole.ADMIN))
            throw new GroupPermissionException(MessageFormat.format(stringConstants.EXC_GROUP_PERMISSION, MemberRole.ADMIN, executor, group));

        GroupMember promotedGroupMember = groupMemberHelper.getGroupMember(member, group);
        if (promotedGroupMember == null) {
            throw new NotMemberOfException(MessageFormat.format(stringConstants.EXC_USER_NOT_MEMBER_OF_GROUP, member, group));
        }
        if (promotedGroupMember.getRole() == MemberRole.ADMIN) {
            throw new NotAllowedException(stringConstants.EXC_NOT_ALLOWED_PROMOTE_ADMIN);
        }

        if (newValue) {
            promotedGroupMember.setRole(MemberRole.MANAGER);
        } else {
            promotedGroupMember.setRole(MemberRole.MEMBER);
        }

        return true;
    }

    /**
     * Lists all existing groups.
     * @param filter Filter of groups to apply. If null, all groups are listed.
     * @return List of {@link Group}
     */
    public List<Group> listGroups(GroupFilter filter, Object... params) {
        if (filter == null) return groupDAO.findAll();
        return filter.getGroups(params);
    }

    public Group getGroup(Long groupId) {
        return groupDAO.findById(groupId);
    }

    /**
     * Adds a new member to the group. All tasks shared with the group will be shared with the new member as well.
     *
     * @param manager User "approving" the addition, needs to be at least a Manager of the group
     * @param group   Group to add the user to
     * @param newUser A user that should be added to the group
     */
    public void addMember(User manager, Group group, User newUser) throws WrongParameterException, NotMemberOfException, GroupPermissionException, AlreadyExistsException {
        if (manager == null || group == null || newUser == null)
            throw new WrongParameterException(stringConstants.EXC_PARAM_ALL_NEED_NOT_NULL);

        if (!permissionHandler.hasGroupPermission(manager, group, MemberRole.MANAGER))
            throw new GroupPermissionException(MessageFormat.format(stringConstants.EXC_GROUP_PERMISSION, MemberRole.MANAGER, manager, group));

        // If the newUser is already in the group
        if (group.getGroupMembers().contains(newUser))
            throw new AlreadyExistsException(MessageFormat.format(stringConstants.EXC_ALREADY_EXISTS_GROUP_MEMBER, newUser, group));

        // Everything correct, start creating associations
        GroupMember newGroupMember = groupMemberHelper.createGroupMember(newUser, group, MemberRole.MEMBER);
        group.addGroupMember(newGroupMember);

        for (Task groupTask : group.getSharedTasks()) {
            taskParticipantHelper.editOrCreateTaskParticipant(newUser, groupTask, TaskRole.WATCHER, group, false);
        }
    }

    /**
     * Removes a user from a group. Manager either needs to be a Manager of the Group, or be equal to the user
     * to be removed (indicating he himself requested the removal).
     *
     * @throws NotAllowedException      Admin cannot be removed from a group.
     * @throws NotMemberOfException     Manager or User are not members of the group.
     * @throws GroupPermissionException Manager does not have sufficient permissions.
     * @throws WrongParameterException  One of parameters is null.
     */
    public void removeMember(User manager, Group group, User toRemove) throws NotAllowedException, NotMemberOfException, GroupPermissionException, WrongParameterException {
        if (manager == null || group == null || toRemove == null)
            throw new WrongParameterException(stringConstants.EXC_PARAM_ALL_NEED_NOT_NULL);

        GroupMember toRemoveGroupMember = groupMemberDAO.findByUserAndGroup(toRemove, group);
        if (toRemoveGroupMember == null)
            throw new NotMemberOfException(MessageFormat.format(stringConstants.EXC_USER_NOT_MEMBER_OF_GROUP, toRemove, group));

        removeMember(manager, group, toRemoveGroupMember);
    }

    public void removeMember(User manager, Group group, GroupMember groupMemberToRemove) throws WrongParameterException, NotMemberOfException, GroupPermissionException, NotAllowedException {
        if (manager == null || group == null || groupMemberToRemove == null)
            throw new WrongParameterException(stringConstants.EXC_PARAM_ALL_NEED_NOT_NULL);

        // If the "manager" user is not a manager of group AND he also isn't the user requesting removal, deny it
        if (!manager.equals(groupMemberToRemove.getUser()) && !permissionHandler.hasGroupPermission(manager, group, MemberRole.MANAGER))
            throw new GroupPermissionException(MessageFormat.format(stringConstants.EXC_GROUP_PERMISSION, MemberRole.MANAGER, manager, group));


        if (group.getGroupMembers(MemberRole.ADMIN).iterator().next().equals(groupMemberToRemove))
            throw new NotAllowedException(stringConstants.EXC_NOT_ALLOWED_ADMIN_LEAVE);

        // Remove each TaskParticipant entry of the removed user from the group
        for (TaskParticipant taskParticipant : taskparticipantDAO.findByUserAndGroups(groupMemberToRemove.getUser(), group)) {
            group.removeTaskParticipant(taskParticipant);
            taskParticipantHelper.removeGroupConnection(taskParticipant, group);
        }

        groupMemberHelper.deleteGroupMember(groupMemberToRemove);
    }

    /**
     * Adds a task as shared with the group.
     *
     * @param manager User approving the action. Must be at least a Manager.
     * @param group   Group to share the task with.
     * @param task    Task to share.
     */
    public void addTask(User manager, Group group, Task task) throws WrongParameterException, NotMemberOfException, GroupPermissionException, AlreadyExistsException {
        if (manager == null || group == null || task == null)
            throw new WrongParameterException(stringConstants.EXC_PARAM_ALL_NEED_NOT_NULL);

        if (!permissionHandler.hasGroupPermission(manager, group, MemberRole.MANAGER))
            throw new GroupPermissionException(MessageFormat.format(stringConstants.EXC_GROUP_PERMISSION, MemberRole.MANAGER, manager, group));

        // If the Task is already shared with the group
        if (group.getSharedTasks().contains(task))
            throw new AlreadyExistsException(MessageFormat.format(stringConstants.EXC_ALREADY_EXISTS_TASK_OF_GROUP, task, group));

        // Security checks passed, create associations
        group.addSharedTask(task);
        task.addSharedGroup(group);

        // For all members update/create TaskParticipant entries
        for (User user : getUsersOfGroup(group)) {
            taskParticipantHelper.editOrCreateTaskParticipant(user, task, TaskRole.WATCHER, group, false);
        }

    }

    @Transactional
    public void leaveTask(User manager, Group group, Task task) throws WrongParameterException, NotMemberOfException, GroupPermissionException {
        if (manager == null || group == null || task == null)
            throw new WrongParameterException(stringConstants.EXC_PARAM_ALL_NEED_NOT_NULL);

        if (!permissionHandler.hasGroupPermission(manager, group, MemberRole.MANAGER))
            throw new GroupPermissionException(MessageFormat.format(stringConstants.EXC_GROUP_PERMISSION, MemberRole.MANAGER, manager, group));

        // For all members of group remove this task/group connection to it
        Set<TaskParticipant> taskParticipants = taskparticipantDAO.findByTaskAndGroups(task, group);
        Set<TaskParticipant> participantsCopy = new HashSet<>(taskParticipants);

        for (TaskParticipant taskParticipant : participantsCopy) {
            taskParticipantHelper.removeGroupConnection(taskParticipant, group);
        }

        group.removeTask(task);
        task.removeSharedGroup(group);

        // TODO: 31.03.2016 REMOVE ASSERTION FOR PRODUCTION
        // After removing the task from a group there should be NO TaskParticipant connected to the Task and Group
        assert taskparticipantDAO.findByTaskAndGroups(task, group).size() == 0;
    }

    @Transactional
    public void editDetails(User admin, Group group, String newDescription) throws NotMemberOfException, GroupPermissionException {
        if (!permissionHandler.hasGroupPermission(admin, group, MemberRole.ADMIN))
            throw new GroupPermissionException(MessageFormat.format(stringConstants.EXC_GROUP_PERMISSION, MemberRole.ADMIN, admin, group));

        group.setDescription(newDescription);
    }

    public void changeAdmin(User admin, Group group, User newAdmin) throws WrongParameterException, NotMemberOfException, GroupPermissionException {
        if (admin == null || group == null || newAdmin == null)
            throw new WrongParameterException(stringConstants.EXC_PARAM_ALL_NEED_NOT_NULL);

        if (!permissionHandler.hasGroupPermission(admin, group, MemberRole.ADMIN))
            throw new GroupPermissionException(MessageFormat.format(stringConstants.EXC_GROUP_PERMISSION, MemberRole.ADMIN, admin, group));

        if (admin.equals(newAdmin)) return;

        GroupMember adminMember = groupMemberHelper.getGroupMember(admin, group);
        GroupMember newAdminMember = groupMemberHelper.getGroupMember(newAdmin, group);
        if (newAdminMember == null)
            throw new NotMemberOfException(MessageFormat.format(stringConstants.EXC_USER_NOT_MEMBER_OF_GROUP, newAdmin, group));

        newAdminMember.setRole(MemberRole.ADMIN);
        adminMember.setRole(MemberRole.MANAGER);
    }

    public void deleteGroup(User admin, Group group) throws NotMemberOfException, GroupPermissionException, WrongParameterException, NotAllowedException {
        if (!permissionHandler.hasGroupPermission(admin, group, MemberRole.ADMIN))
            throw new GroupPermissionException(MessageFormat.format(stringConstants.EXC_GROUP_PERMISSION, MemberRole.ADMIN, admin, group));

        // Remove all members from group - that automatically deletes all needed connections
        GroupMember adminGroupMember = null;
        Set<GroupMember> groupMembersCopy = new HashSet<>(group.getGroupMembers());
        for (GroupMember groupMember : groupMembersCopy) {
            if (groupMember.getRole() == MemberRole.ADMIN){
                adminGroupMember = groupMember;
                continue; // Skip deleting the admin
            }
            removeMember(admin, group, groupMember);
        }

        // Leave all tasks associated with group
        Set<Task> sharedTasksCopy = new HashSet<>(group.getSharedTasks());
        for (Task task: sharedTasksCopy){
            leaveTask(admin, group, task);
        }

        // Remove admin
        for (TaskParticipant taskParticipant : taskparticipantDAO.findByUserAndGroups(admin, group)) {
            group.removeTaskParticipant(taskParticipant);
            taskParticipantHelper.removeGroupConnection(taskParticipant, group);
        }

        groupMemberHelper.deleteGroupMember(adminGroupMember);
        // TODO: 31.03.2016 Also delete all sharing offers! (they were not implemented yet when writing this)
        groupDAO.delete(group);
    }

    public Set<User> getUsersOfGroup(Group group) {
        return groupMemberDAO.findByGroup(group).stream().map(GroupMember::getUser).collect(Collectors.toSet());
    }


}
