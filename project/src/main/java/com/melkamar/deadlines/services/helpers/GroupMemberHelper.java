package com.melkamar.deadlines.services.helpers;

import com.melkamar.deadlines.config.StringConstants;
import com.melkamar.deadlines.dao.groupmember.GroupMemberDAO;
import com.melkamar.deadlines.exceptions.AlreadyExistsException;
import com.melkamar.deadlines.exceptions.WrongParameterException;
import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.GroupMember;
import com.melkamar.deadlines.model.MemberRole;
import com.melkamar.deadlines.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 28.03.2016 11:22
 */
@Service
public class GroupMemberHelper {
    @Autowired
    GroupMemberDAO groupMemberDAO;
    @Autowired
    private StringConstants stringConstants;


    public GroupMember createGroupMember(User user, Group group, MemberRole role) throws AlreadyExistsException, WrongParameterException {
        validateGroupMemberParams(user, group, role);

        if (groupMemberDAO.findByUserAndGroup(user, group) != null) {
            throw new AlreadyExistsException(MessageFormat.format(stringConstants.EXC_ALREADY_EXISTS_GROUP_MEMBER, user, group));
        }

        GroupMember groupMember = new GroupMember(user, group, role);
        user.addGroupMember(groupMember);
        group.addGroupMember(groupMember);

        // TODO: 29.03.2016 Share any tasks the group has with the user joining the group

        groupMemberDAO.save(groupMember);
        return groupMember;
    }

//    public GroupMember addOrEditGroupMember(User user, Group group, MemberRole role) throws AlreadyExistsException, WrongParameterException {
//        validateGroupMemberParams(user, group, role);
//
//        GroupMember member = groupMemberDAO.findByUserAndGroup(user, group);
//        if (member == null) {
//            member = createGroupMember(user, group, role);
//        } else {
//            member.setRole(role);
//        }
//
//        return member;
//    }

    /**
     * Deletes this GroupMember object including references to it from associated
     * User and Group objects.
     * @param groupMember
     */
    public void deleteGroupMember(GroupMember groupMember){
        groupMember.getUser().removeGroupMember(groupMember);
        groupMember.getGroup().removeMember(groupMember);
        groupMemberDAO.delete(groupMember);
    }

    public GroupMember getGroupMember(User user, Group group){
        return groupMemberDAO.findByUserAndGroup(user, group);
    }

    private void validateGroupMemberParams(User user, Group group, MemberRole role) throws WrongParameterException {
        if (user == null) throw new WrongParameterException(stringConstants.EXC_PARAM_USER_NULL);
        if (group == null) throw new WrongParameterException(stringConstants.EXC_PARAM_GROUP_NULL);
        if (role == null) throw new WrongParameterException(stringConstants.EXC_PARAM_MEMBER_ROLE_NULL);
    }
}
