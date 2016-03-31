package com.melkamar.deadlines.services.api;

import com.melkamar.deadlines.config.StringConstants;
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
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.text.MessageFormat;
import java.util.List;

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
            throw new WrongParameterException(stringConstants.EXC_PARAM_ALL_NEED_NULL);
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

    public List<Group> listGroups() {
        // TODO: 31.03.2016 Implement
        throw new NotImplementedException();
    }

    public Group getGroup(User executor, Long groupId) {
        // TODO: 31.03.2016 Implement
        throw new NotImplementedException();
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
            throw new WrongParameterException(stringConstants.EXC_PARAM_NOT_NULL);

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
            throw new WrongParameterException(stringConstants.EXC_PARAM_NOT_NULL);

        GroupMember toRemoveGroupMember = groupMemberDAO.findByUserAndGroup(toRemove, group);
        if (toRemoveGroupMember == null)
            throw new NotMemberOfException(MessageFormat.format(stringConstants.EXC_USER_NOT_MEMBER_OF_GROUP, toRemove, group));

        // If the "manager" user is not a manager of group AND he also isn't the user requesting removal, deny it
        if (!manager.equals(toRemove) && !permissionHandler.hasGroupPermission(manager, group, MemberRole.MANAGER))
            throw new GroupPermissionException(MessageFormat.format(stringConstants.EXC_GROUP_PERMISSION, MemberRole.MANAGER, manager, group));

        if (group.getGroupMembers(MemberRole.ADMIN).iterator().next().equals(toRemoveGroupMember))
            throw new NotAllowedException(stringConstants.EXC_NOT_ALLOWED_ADMIN_LEAVE);

        // Remove each TaskParticipant entry of the removed user from the group
        for (TaskParticipant taskParticipant : taskparticipantDAO.findByUserAndGroups(toRemove, group)) {
            group.removeTaskParticipant(taskParticipant);
            taskParticipantHelper.removeFromGroup(taskParticipant, group);
        }
    }

    /**
     * Adds a task as shared with the group.
     *
     * @param manager User approving the action. Must be at least a Manager.
     * @param group   Group to share the task with.
     * @param task    Task to share.
     */
    public void addTask(User manager, Group group, Task task) throws WrongParameterException, NotMemberOfException, GroupPermissionException {
        if (manager == null || group == null || task == null)
            throw new WrongParameterException(stringConstants.EXC_PARAM_NOT_NULL);

        if (!permissionHandler.hasGroupPermission(manager, group, MemberRole.MANAGER))
            throw new GroupPermissionException(MessageFormat.format(stringConstants.EXC_GROUP_PERMISSION, MemberRole.MANAGER, manager, group));
    }

    public void leaveTask(User manager, Group group, Task task) {
        // TODO: 31.03.2016 Implement
        throw new NotImplementedException();
    }

    public Group editDetails(User manager, Group group, String newDescription) {
        // TODO: 31.03.2016 Implement
        throw new NotImplementedException();
    }

    public Group changeAdmin(User executor, Group group, User newAdmin) {
        // TODO: 31.03.2016 Implement
        throw new NotImplementedException();
    }

    public void deleteGroup(User executor, Group group) {
        // TODO: 31.03.2016 Implement
        throw new NotImplementedException();
    }


    public class GroupFilter {
        public List<Group> filter(List<Group> groups) {
            // TODO: 31.03.2016 Implement
            throw new NotImplementedException();
        }
    }
}
