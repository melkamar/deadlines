package com.melkamar.deadlines.services.helpers;

import com.melkamar.deadlines.config.StringConstants;
import com.melkamar.deadlines.dao.group.GroupDAO;
import com.melkamar.deadlines.dao.user.UserDAO;
import com.melkamar.deadlines.exceptions.*;
import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.GroupMember;
import com.melkamar.deadlines.model.MemberRole;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.task.Task;
import com.melkamar.deadlines.services.PermissionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.text.MessageFormat;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 26.03.2016 15:52
 */
@Service("groupHelper")
public class GroupHelper {
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

    public boolean deleteGroup(User executor, Group group) {
        // TODO: 29.03.2016 Implement
        throw new NotImplementedException();
    }

    public boolean removeMember(User executor, Group group, User member) {
        // TODO: 29.03.2016 Implement
        throw new NotImplementedException();
    }

    public boolean leaveTask(User executor, Group group, Task task) {
        // TODO: 29.03.2016 Implement
        throw new NotImplementedException();
    }

    public boolean setManager(User executor, Group group, User member, boolean newValue) throws GroupPermissionException, NotMemberOfException, WrongParameterException, NotAllowedException {
        if (executor == null || group == null || member == null){
            throw new WrongParameterException(stringConstants.EXC_PARAM_ALL_NEED_NULL);
        }

        GroupMember executorGroupMember = groupMemberHelper.getGroupMember(executor, group);
        if (!permissionHandler.hasGroupPermission(executorGroupMember, MemberRole.ADMIN))
            throw new GroupPermissionException(MessageFormat.format(stringConstants.EXC_GROUP_PERMISSION, MemberRole.ADMIN, executor, group));

        GroupMember promotedGroupMember = groupMemberHelper.getGroupMember(member, group);
        if (promotedGroupMember == null) {
            throw new NotMemberOfException(MessageFormat.format(stringConstants.EXC_USER_NOT_MEMBER_CANT_PROMOTE, member, group));
        }
        if (promotedGroupMember.getRole() == MemberRole.ADMIN){
            throw new NotAllowedException(stringConstants.EXC_NOT_ALLOWED_PROMOTE_ADMIN);
        }

        if (newValue){
            promotedGroupMember.setRole(MemberRole.MANAGER);
        } else {
            promotedGroupMember.setRole(MemberRole.MEMBER);
        }

        return true;
    }


    public boolean changeAdmin(User executor, Group group, User newAdmin) {
        // TODO: 29.03.2016 Implement
        throw new NotImplementedException();
    }
}
