package com.melkamar.deadlines.services.helpers;

import com.melkamar.deadlines.config.StringConstants;
import com.melkamar.deadlines.dao.group.GroupDAO;
import com.melkamar.deadlines.dao.user.UserDAO;
import com.melkamar.deadlines.exceptions.AlreadyExistsException;
import com.melkamar.deadlines.exceptions.GroupPermissionException;
import com.melkamar.deadlines.exceptions.WrongParameterException;
import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.GroupMember;
import com.melkamar.deadlines.model.MemberRole;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.task.Task;
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
@Service
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

    public Group createGroup(String name, User founder, String description) throws WrongParameterException {
        if (name == null || name.isEmpty()) throw new WrongParameterException(stringConstants.EXC_PARAM_NAME_EMPTY);
        if (founder == null) throw new WrongParameterException(stringConstants.EXC_PARAM_FOUNDER_NULL);

        Group group = new Group(name);
        group.setDescription(description);

        groupDAO.save(group);

        try {
            groupMemberHelper.addOrEditGroupMember(founder, group, MemberRole.ADMIN);
        } catch (AlreadyExistsException e) {
            org.apache.log4j.Logger.getLogger(this.getClass()).error("User is already a member of newly created group! This should not happen.");
            e.printStackTrace();
        }
        return group;
    }

    public boolean deleteGroup(User executor, Group group) {
        throw new NotImplementedException();
    }

    public boolean removeMember(User executor, Group group, User member) {
        throw new NotImplementedException();
    }

    public boolean removeTask(User executor, Group group, Task task) {
        throw new NotImplementedException();
    }

    public boolean setManager(User executor, Group group, User member, boolean newValue) throws GroupPermissionException {
        throw new NotImplementedException();

//        if (!hasPermission(executor, group, Group.PermissionLevel.ADMIN))
//            throw new GroupPermissionException(MessageFormat.format(stringConstants.EXC_GROUP_PERMISSION, executor, executor));
    }

    public boolean changeAdmin(User executor, Group group, User newAdmin) {
        throw new NotImplementedException();
    }

    private boolean hasPermission(User executor, Group group, MemberRole requiredPermission) {
        throw new NotImplementedException();
//        switch (requiredPermission) {
//            case MEMBER:
//                return group.getMembers().contains(executor) || group.getManagers().contains(executor) || group.getAdmin().equals(executor);
//
//            case MANAGER:
//                return group.getManagers().contains(executor) || group.getAdmin().equals(executor);
//
//            case ADMIN:
//                return group.getAdmin().equals(executor);
//
//            default:
//                return false;
//        }
    }
}
