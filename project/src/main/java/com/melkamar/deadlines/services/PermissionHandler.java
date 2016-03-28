package com.melkamar.deadlines.services;

import com.melkamar.deadlines.dao.groupmember.GroupMemberDAO;
import com.melkamar.deadlines.dao.groupmember.GroupMemberDAOHibernate;
import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.GroupMember;
import com.melkamar.deadlines.model.MemberRole;
import com.melkamar.deadlines.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 28.03.2016 18:18
 */
@Service
public class PermissionHandler {
    @Autowired
    private GroupMemberDAO groupMemberDAO;

    public boolean hasGroupPermission(User user, Group group, MemberRole requiredPermission){
        GroupMember groupMember = groupMemberDAO.findByUserAndGroup(user, group);
        return hasGroupPermission(groupMember, requiredPermission);
    }

    public boolean hasGroupPermission(GroupMember executorGroupMember, MemberRole requiredPermission) {
        if (executorGroupMember == null) return false;

        switch (requiredPermission) {
            case MEMBER:
                return true; // executor is at least a member of the group if this code is executing -> he has permission

            case MANAGER:
                return executorGroupMember.getRole() == MemberRole.MANAGER || executorGroupMember.getRole() == MemberRole.ADMIN;

            case ADMIN:
                return executorGroupMember.getRole() == MemberRole.ADMIN;

            default:
                return false;
        }
    }
}
