package com.melkamar.deadlines.services;

import com.melkamar.deadlines.config.StringConstants;
import com.melkamar.deadlines.dao.groupmember.GroupMemberDAO;
import com.melkamar.deadlines.dao.groupmember.GroupMemberDAOHibernate;
import com.melkamar.deadlines.exceptions.GroupPermissionException;
import com.melkamar.deadlines.exceptions.NotMemberOfException;
import com.melkamar.deadlines.model.*;
import com.melkamar.deadlines.model.task.Task;
import com.melkamar.deadlines.model.task.TaskRole;
import com.melkamar.deadlines.services.helpers.TaskParticipantHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

import static com.melkamar.deadlines.model.MemberRole.ADMIN;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 28.03.2016 18:18
 */
@Service
public class PermissionHandler {
    @Autowired
    private GroupMemberDAO groupMemberDAO;
    @Autowired
    private TaskParticipantHelper taskParticipantHelper;
    @Autowired
    private StringConstants stringConstants;


    public boolean hasGroupPermission(User user, Group group, MemberRole requiredPermission) throws NotMemberOfException {
        GroupMember groupMember = groupMemberDAO.findByUserAndGroup(user, group);
        if (groupMember == null)
            throw new NotMemberOfException(MessageFormat.format(stringConstants.EXC_USER_NOT_MEMBER_OF_GROUP, user, group));

        return hasGroupPermission(groupMember, requiredPermission);
    }

    /**
     * Checks whether an executor has a sufficient permission in a group over its member.
     *
     * @param executor           The user performing an action.
     * @param group              Affected group.
     * @param targetUser         A member of the group.
     * @param requiredPermission Required minimal permission.
     * @return
     */
    public boolean hasGroupPermissionOver(User executor, Group group, User targetUser, MemberRole requiredPermission) throws NotMemberOfException {
        boolean executorHasPermission = hasGroupPermission(executor, group, requiredPermission);
        if (!executorHasPermission) return false;

        // Executor has enough permission in group. Now check if the target user is a member of it.
        GroupMember targetGroupMember = groupMemberDAO.findByUserAndGroup(targetUser, group);
        if (targetGroupMember == null) return false;
        else return true;
    }

    private boolean hasGroupPermission(GroupMember executorGroupMember, MemberRole requiredPermission) throws NotMemberOfException {
        switch (requiredPermission) {
            case MEMBER:
                return true; // executor is at least a member of the group if this code is executing -> he has permission

            case MANAGER:
                return executorGroupMember.getRole() == MemberRole.MANAGER || executorGroupMember.getRole() == ADMIN;


            case ADMIN:
                return executorGroupMember.getRole() == ADMIN;

            default:
                return false;
        }
    }

    /**
     * Checks whether a user has enough privileges on a task.
     *
     * @param user
     * @param task
     * @param taskRole
     * @return
     */
    public boolean hasTaskPermission(User user, Task task, TaskRole taskRole) throws NotMemberOfException {
        TaskParticipant taskParticipant = taskParticipantHelper.getTaskParticipant(user, task);
        if (taskParticipant == null)
            throw new NotMemberOfException(MessageFormat.format(stringConstants.EXC_USER_NOT_PARTICIPANT, user, task));

        switch (taskRole) {
            case WATCHER:
                return true;

            case WORKER:
                return taskParticipant.getRole() == TaskRole.WORKER;

            default: return false;
        }
    }
}
