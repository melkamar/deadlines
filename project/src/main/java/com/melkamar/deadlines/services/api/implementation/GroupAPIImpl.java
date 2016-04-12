package com.melkamar.deadlines.services.api.implementation;

import com.melkamar.deadlines.config.StringConstants;
import com.melkamar.deadlines.dao.group.GroupDAO;
import com.melkamar.deadlines.dao.groupmember.GroupMemberDAO;
import com.melkamar.deadlines.dao.processing.TaskFilter;
import com.melkamar.deadlines.dao.processing.TaskOrdering;
import com.melkamar.deadlines.dao.taskparticipant.TaskParticipantDAOHibernate;
import com.melkamar.deadlines.dao.user.UserDAO;
import com.melkamar.deadlines.exceptions.*;
import com.melkamar.deadlines.model.*;
import com.melkamar.deadlines.model.task.Task;
import com.melkamar.deadlines.model.task.TaskRole;
import com.melkamar.deadlines.services.security.PermissionHandler;
import com.melkamar.deadlines.services.api.GroupApi;
import com.melkamar.deadlines.services.api.TaskApi;
import com.melkamar.deadlines.services.helpers.GroupMemberHelper;
import com.melkamar.deadlines.services.helpers.TaskParticipantHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 26.03.2016 15:52
 */
@Service("groupApi")
@Transactional
public class GroupApiImpl implements GroupApi {
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
    @Autowired
    private TaskApi taskApi;


    @Override
    public Group createGroup(String name, User founder, String description) throws WrongParameterException, AlreadyExistsException {
        if (name == null || name.isEmpty()) throw new WrongParameterException(stringConstants.EXC_PARAM_NAME_EMPTY);
        if (founder == null) throw new WrongParameterException(stringConstants.EXC_PARAM_FOUNDER_NULL);

        Group group = new Group(name);
        group.setDescription(description);

        try {
            groupDAO.save(group);
        } catch (DataIntegrityViolationException e) {
            throw new AlreadyExistsException(MessageFormat.format(stringConstants.EXC_ALREADY_EXISTS_GROUP_NAME, name));
        }


        try {
            groupMemberHelper.createGroupMember(founder, group, MemberRole.ADMIN);
        } catch (AlreadyExistsException e) {
            org.apache.log4j.Logger.getLogger(this.getClass()).error("User is already a member of newly created group! This should not happen.");
            e.printStackTrace();
            throw new RuntimeException("User is already a member of newly created group! This should not happen.");
        }
        return group;
    }

    @Override
    public void setManager(User executor, Group group, User member, boolean newValue) throws GroupPermissionException, NotMemberOfException, WrongParameterException, NotAllowedException {
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
            throw new NotAllowedException(stringConstants.EXC_NOT_ALLOWED_DEMOTE_ADMIN);
        }

        if (newValue) {
            promotedGroupMember.setRole(MemberRole.MANAGER);
        } else {
            promotedGroupMember.setRole(MemberRole.MEMBER);
        }
    }

    /**
     * Lists all existing groups.
     *
     * @return List of {@link Group}
     */
    @Override
    public List<Group> listGroups() {
        return groupDAO.findAll();
    }

    /**
     * Lists all groups where User is a member.
     *
     * @param user
     * @return List of {@link Group}
     */
    @Override
    public List<Group> listGroups(User user) {
        return groupDAO.findByMembers_User(user);
    }

    /**
     * Lists all groups where User has a role.
     *
     * @param user
     * @param role
     * @return List of {@link Group}
     */
    @Override
    public List<Group> listGroups(User user, MemberRole role) {
        return groupDAO.findByMembers_UserAndRole(user, role);
    }


    @Override
    public Group getGroup(Long groupId) throws DoesNotExistException {
        Group group = groupDAO.findById(groupId);
        if (group == null) {
            throw new DoesNotExistException(MessageFormat.format(stringConstants.EXC_DOES_NOT_EXIST_GROUP, groupId));
        } else {
            return group;
        }
    }

    /**
     * Get group object only if user has permissions (only if he is a member of the group)
     *
     * @param groupId
     * @param user
     * @return
     * @throws DoesNotExistException
     * @throws NotMemberOfException
     */
    @Override
    public Group getGroup(Long groupId, User user) throws DoesNotExistException, NotMemberOfException {
        Group group = groupDAO.findById(groupId);

        if (group == null) {
            throw new DoesNotExistException(MessageFormat.format(stringConstants.EXC_DOES_NOT_EXIST_GROUP, groupId));
        }

        // Just check if user a member of the group
        permissionHandler.hasGroupPermission(user, group, MemberRole.MEMBER);

        return group;
    }

//    @Override
//    public List<Task> listTasks(User user, Group group, TaskOrdering ordering, TaskFilter... filters) throws NotMemberOfException, GroupPermissionException {
//        if (!permissionHandler.hasGroupPermission(user, group, MemberRole.MEMBER))
//            throw new GroupPermissionException(MessageFormat.format(stringConstants.EXC_GROUP_PERMISSION, MemberRole.MEMBER, user, group));
//
//        return taskApi.listTasks(group, ordering, filters);
//    }


    /**
     * Adds a new member to the group. All jobs shared with the group will be shared with the new member as well.
     *
     * @param manager User "approving" the addition, needs to be at least a Manager of the group
     * @param group   Group to add the user to
     * @param newUser A user that should be added to the group
     */
    @Override
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
    @Override
    public void removeMember(User manager, Group group, User toRemove) throws NotAllowedException, NotMemberOfException, GroupPermissionException, WrongParameterException {
        if (manager == null || group == null || toRemove == null)
            throw new WrongParameterException(stringConstants.EXC_PARAM_ALL_NEED_NOT_NULL);

        GroupMember toRemoveGroupMember = groupMemberDAO.findByUserAndGroup(toRemove, group);
        if (toRemoveGroupMember == null)
            throw new NotMemberOfException(MessageFormat.format(stringConstants.EXC_USER_NOT_MEMBER_OF_GROUP, toRemove, group));

        removeMember(manager, group, toRemoveGroupMember);
    }

    @Override
    public void removeMember(User manager, Group group, GroupMember groupMemberToRemove) throws WrongParameterException, NotMemberOfException, GroupPermissionException, NotAllowedException {
        if (manager == null || group == null || groupMemberToRemove == null)
            throw new WrongParameterException(stringConstants.EXC_PARAM_ALL_NEED_NOT_NULL);

        // If the "manager" user is not a manager of group AND he also isn't the user requesting removal, deny it
        if (!manager.equals(groupMemberToRemove.getUser()) && !permissionHandler.hasGroupPermission(manager, group, MemberRole.MANAGER))
            throw new GroupPermissionException(MessageFormat.format(stringConstants.EXC_GROUP_PERMISSION, MemberRole.MANAGER, manager, group));


        if (group.getGroupMembers(MemberRole.ADMIN).iterator().next().equals(groupMemberToRemove))
            throw new NotAllowedException(stringConstants.EXC_NOT_ALLOWED_ADMIN_LEAVE_OR_REMOVE);

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
    @Override
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

    @Override
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

    @Override
    public void editDetails(User admin, Group group, String newDescription) throws NotMemberOfException, GroupPermissionException {
        if (!permissionHandler.hasGroupPermission(admin, group, MemberRole.ADMIN))
            throw new GroupPermissionException(MessageFormat.format(stringConstants.EXC_GROUP_PERMISSION, MemberRole.ADMIN, admin, group));

        group.setDescription(newDescription);
    }

    @Override
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

    @Override
    public void deleteGroup(User admin, Group group) throws NotMemberOfException, GroupPermissionException, WrongParameterException {
        if (!permissionHandler.hasGroupPermission(admin, group, MemberRole.ADMIN))
            throw new GroupPermissionException(MessageFormat.format(stringConstants.EXC_GROUP_PERMISSION, MemberRole.ADMIN, admin, group));

        // Remove all members from group - that automatically deletes all needed connections
        GroupMember adminGroupMember = null;
        Set<GroupMember> groupMembersCopy = new HashSet<>(group.getGroupMembers());
        for (GroupMember groupMember : groupMembersCopy) {
            if (groupMember.getRole() == MemberRole.ADMIN) {
                adminGroupMember = groupMember;
                continue; // Skip deleting the admin
            }

            try {
                removeMember(admin, group, groupMember);
            } catch (NotAllowedException e) {
                // If this happens then the admin is not removed.
                // That does not matter, he is removed futher down in the method
                org.apache.log4j.Logger.getLogger(this.getClass()).error("deleteGroup tried to removeMember(admin). This should not happen.", e);
            }

        }

        // Leave all jobs associated with group
        Set<Task> sharedTasksCopy = new HashSet<>(group.getSharedTasks());
        for (Task task : sharedTasksCopy) {
            leaveTask(admin, group, task);
        }

        // Remove admin
        for (TaskParticipant taskParticipant : taskparticipantDAO.findByUserAndGroups(admin, group)) {
            group.removeTaskParticipant(taskParticipant);
            taskParticipantHelper.removeGroupConnection(taskParticipant, group);
        }

        groupMemberHelper.deleteGroupMember(adminGroupMember);
        groupDAO.delete(group);
    }

    @Override
    public Set<User> getUsersOfGroup(Group group) {
        return groupMemberDAO.findByGroup(group).stream().map(GroupMember::getUser).collect(Collectors.toSet());
    }


}
