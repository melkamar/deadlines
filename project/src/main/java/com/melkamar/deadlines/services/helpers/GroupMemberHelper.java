/*
 * Copyright (c) 2016 Martin Melka
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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


    /**
     * Creates a new GroupMember object for given {@link User} and {@link Group}, with a given {@link MemberRole}.
     * Handles only creation of the object and setting associations for User and Group, it does not handle any other
     * logic (it will not add Group's jobs to the User etc., this has to be handled above).
     * @param user
     * @param group
     * @param role
     * @return Created {@link GroupMember} for the User and Group.
     * @throws AlreadyExistsException Thrown if GroupMember already exists.
     * @throws WrongParameterException Thrown if any of parameters are null.
     */
    public GroupMember createGroupMember(User user, Group group, MemberRole role) throws AlreadyExistsException, WrongParameterException {
        validateGroupMemberParams(user, group, role);

        if (groupMemberDAO.findByUserAndGroup(user, group) != null) {
            throw new AlreadyExistsException(MessageFormat.format(stringConstants.EXC_ALREADY_EXISTS_GROUP_MEMBER, user, group));
        }

        GroupMember groupMember = new GroupMember(user, group, role);
        user.addGroupMember(groupMember);
        group.addGroupMember(groupMember);

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
