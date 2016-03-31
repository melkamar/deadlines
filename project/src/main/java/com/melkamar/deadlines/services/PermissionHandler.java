package com.melkamar.deadlines.services;

import com.melkamar.deadlines.config.StringConstants;
import com.melkamar.deadlines.dao.groupmember.GroupMemberDAO;
import com.melkamar.deadlines.dao.groupmember.GroupMemberDAOHibernate;
import com.melkamar.deadlines.exceptions.GroupPermissionException;
import com.melkamar.deadlines.exceptions.NotMemberOfException;
import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.GroupMember;
import com.melkamar.deadlines.model.MemberRole;
import com.melkamar.deadlines.model.User;
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
    private StringConstants stringConstants;


    public boolean hasGroupPermission(User user, Group group, MemberRole requiredPermission) throws NotMemberOfException {
        GroupMember groupMember = groupMemberDAO.findByUserAndGroup(user, group);
        return hasGroupPermission(groupMember, requiredPermission);
    }

    public boolean hasGroupPermission(GroupMember executorGroupMember, MemberRole requiredPermission) throws NotMemberOfException {
        if (executorGroupMember == null)
            throw new NotMemberOfException(stringConstants.EXC_USER_NOT_MEMBER_OF_GROUP);

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
}
