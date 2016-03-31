package com.melkamar.deadlines.services.helpers;

import com.melkamar.deadlines.DeadlinesApplication;
import com.melkamar.deadlines.dao.group.GroupDAO;
import com.melkamar.deadlines.dao.user.UserDAO;
import com.melkamar.deadlines.exceptions.*;
import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.MemberRole;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.services.api.UserAPI;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 26.03.2016 16:07
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DeadlinesApplication.class)
public class GroupHelperTest {
    @Autowired
    private GroupDAO groupDAO;
    @Autowired
    private GroupHelper groupHelper;


    @Autowired
    private UserDAO userDAO;
    @Autowired
    private UserAPI userAPI;
    @Autowired
    private GroupMemberHelper groupMemberHelper;

    @Test(expected = WrongParameterException.class)
    @Transactional
    public void nullParameters() throws WrongParameterException {
        groupHelper.createGroup(null, null, null);
    }

    @Test(expected = WrongParameterException.class)
    @Transactional
    public void nullFounder() throws WrongParameterException {
        groupHelper.createGroup("SomeName", null, null);
    }

    @Test
    @Transactional
    public void founderAdmin() throws WrongParameterException {
        User user = userAPI.createUser("GroupAdmin", "pwd", null, null);
        Group group = groupHelper.createGroup("AGroup", user, null);

        User retrievedUser = userDAO.findByUsername("GroupAdmin");
        Group retrievedGroup = groupDAO.findByName("AGroup");

        Assert.assertEquals(retrievedUser, retrievedGroup.getGroupMembers(MemberRole.ADMIN).iterator().next().getUser());
        Assert.assertTrue(groupMemberHelper.getGroupMember(retrievedUser, retrievedGroup).getRole() == MemberRole.ADMIN);
    }

    @Test(expected = WrongParameterException.class)
    @Transactional
    public void setManagerWrongParam1() throws WrongParameterException, GroupPermissionException, NotMemberOfException, NotAllowedException {
        User userMember = userAPI.createUser("Member", "password", "John Doe", "a@b.c");
        User userManager = userAPI.createUser("Manager", "password", "John Doe", "b@b.c");
        User userAdmin = userAPI.createUser("Admin", "password", "John Doe", "c@b.c");
        User userNonmember = userAPI.createUser("Nonmember", "password", "John Doe", "d@b.c");

        Group group = groupHelper.createGroup("Groupname", userAdmin, "Random description");

        groupHelper.setManager(null, group, userMember, true);
    }

    @Test(expected = WrongParameterException.class)
    @Transactional
    public void setManagerWrongParam2() throws WrongParameterException, GroupPermissionException, NotMemberOfException, NotAllowedException {
        User userMember = userAPI.createUser("Member", "password", "John Doe", "a@b.c");
        User userManager = userAPI.createUser("Manager", "password", "John Doe", "b@b.c");
        User userAdmin = userAPI.createUser("Admin", "password", "John Doe", "c@b.cb");
        User userNonmember = userAPI.createUser("Nonmember", "password", "John Doe", "d@b.c");

        Group group = groupHelper.createGroup("Groupname", userAdmin, "Random description");

        groupHelper.setManager(userAdmin, null, userMember, true);
    }

    @Test(expected = WrongParameterException.class)
    @Transactional
    public void setManagerWrongParam3() throws WrongParameterException, GroupPermissionException, NotMemberOfException, AlreadyExistsException, NotAllowedException {
        User userMember = userAPI.createUser("Member", "password", "John Doe", "a@b.c");
        User userManager = userAPI.createUser("Manager", "password", "John Doe", "b@b.ca");
        User userAdmin = userAPI.createUser("Admin", "password", "John Doe", "c@b.c");
        User userNonmember = userAPI.createUser("Nonmember", "password", "John Doe", "d@b.c");

        Group group = groupHelper.createGroup("Groupname", userAdmin, "Random description");

        groupHelper.setManager(userAdmin, group, null, true);
    }

    @Test(expected = GroupPermissionException.class)
    @Transactional
    public void setManagerNoPermission() throws WrongParameterException, GroupPermissionException, NotMemberOfException, AlreadyExistsException, NotAllowedException {
        User userMember = userAPI.createUser("Member", "password", "John Doe", "a@b.c");
        User userManager = userAPI.createUser("Manager", "password", "John Doe", "b@b.ca");
        User userAdmin = userAPI.createUser("Admin", "password", "John Doe", "c@b.c");
        User userNonmember = userAPI.createUser("Nonmember", "password", "John Doe", "d@b.ca");

        Group group = groupHelper.createGroup("Groupname", userAdmin, "Random description");
        groupMemberHelper.createGroupMember(userMember, group, MemberRole.MEMBER);
        groupMemberHelper.createGroupMember(userManager, group, MemberRole.MANAGER);

        groupHelper.setManager(userManager, group, userMember, true);
    }

    @Test(expected = NotMemberOfException.class)
    @Transactional
    public void setManagerTargetNotMember() throws WrongParameterException, GroupPermissionException, NotMemberOfException, AlreadyExistsException, NotAllowedException {
        User userMember = userAPI.createUser("Member", "password", "John Doe", "a@b.c");
        User userManager = userAPI.createUser("Manager", "password", "John Doe", "b@b.ca");
        User userAdmin = userAPI.createUser("Admin", "password", "John Doe", "c@b.c");
        User userNonmember = userAPI.createUser("Nonmember", "password", "John Doe", "d@b.ca");

        Group group = groupHelper.createGroup("Groupname", userAdmin, "Random description");

        groupHelper.setManager(userAdmin, group, userMember, true);
    }

    @Test
    @Transactional
    public void setManagerOk() throws WrongParameterException, GroupPermissionException, NotMemberOfException, AlreadyExistsException, NotAllowedException {
        User userMember = userAPI.createUser("Member", "password", "John Doe", "a@b.c");
        User userManager = userAPI.createUser("Manager", "password", "John Doe", "b@b.ca");
        User userAdmin = userAPI.createUser("Admin", "password", "John Doe", "c@b.c");
        User userNonmember = userAPI.createUser("Nonmember", "password", "John Doe", "d@b.ca");

        Group group = groupHelper.createGroup("Groupname", userAdmin, "Random description");
        groupMemberHelper.createGroupMember(userMember, group, MemberRole.MEMBER);
        groupMemberHelper.createGroupMember(userManager, group, MemberRole.MANAGER);

        Assert.assertTrue(groupMemberHelper.getGroupMember(userMember, group).getRole() == MemberRole.MEMBER);
        groupHelper.setManager(userAdmin, group, userMember, true);
        Assert.assertTrue(groupMemberHelper.getGroupMember(userMember, group).getRole() == MemberRole.MANAGER);

        Assert.assertTrue(groupMemberHelper.getGroupMember(userManager, group).getRole() == MemberRole.MANAGER);
        groupHelper.setManager(userAdmin, group, userManager, false);
        Assert.assertTrue(groupMemberHelper.getGroupMember(userManager, group).getRole() == MemberRole.MEMBER);
    }

    @Test(expected = NotAllowedException.class)
    @Transactional
    public void setManagerOnAdmin() throws WrongParameterException, GroupPermissionException, NotMemberOfException, AlreadyExistsException, NotAllowedException {
        User userMember = userAPI.createUser("Member", "password", "John Doe", "a@b.c");
        User userManager = userAPI.createUser("Manager", "password", "John Doe", "b@b.cab");
        User userAdmin = userAPI.createUser("Admin", "password", "John Doe", "c@b.c");
        User userNonmember = userAPI.createUser("Nonmember", "password", "John Doe", "d@b.ca");

        Group group = groupHelper.createGroup("Groupname", userAdmin, "Random description");
        groupMemberHelper.createGroupMember(userMember, group, MemberRole.MEMBER);
        groupMemberHelper.createGroupMember(userManager, group, MemberRole.MANAGER);

        groupHelper.setManager(userAdmin, group, userAdmin, true);
    }
}