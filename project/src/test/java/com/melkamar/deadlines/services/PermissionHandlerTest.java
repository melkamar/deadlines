package com.melkamar.deadlines.services;

import com.melkamar.deadlines.DeadlinesApplication;
import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.MemberRole;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.services.helpers.GroupHelper;
import com.melkamar.deadlines.services.helpers.GroupMemberHelper;
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
 * 28.03.2016 18:20
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DeadlinesApplication.class)
public class PermissionHandlerTest {


    @Autowired
    private UserAPI userAPI;
    @Autowired
    private GroupHelper groupHelper;
    @Autowired
    private PermissionHandler permissionHandler;
    @Autowired
    private GroupMemberHelper groupMemberHelper;


    @Transactional
    @Test
    public void hasGroupPermission() throws Exception {
        User userMember = userAPI.createUser("Member", "password", "John Doe", "a@b.c");
        User userManager = userAPI.createUser("Manager", "password", "John Doe", "b@b.c");
        User userAdmin = userAPI.createUser("Admin", "password", "John Doe", "c@b.c");
        User userNonmember = userAPI.createUser("Nonmember", "password", "John Doe", "d@b.c");

        Group group = groupHelper.createGroup("Groupname", userAdmin, "Random description");
        groupMemberHelper.createGroupMember(userMember, group, MemberRole.MEMBER);
        groupMemberHelper.createGroupMember(userManager, group, MemberRole.MANAGER);

        Assert.assertFalse(permissionHandler.hasGroupPermission(userAdmin, null, MemberRole.MEMBER));
        Assert.assertFalse(permissionHandler.hasGroupPermission(null, group, MemberRole.MEMBER));

        Assert.assertFalse(permissionHandler.hasGroupPermission(userNonmember, group, MemberRole.MEMBER));
        Assert.assertFalse(permissionHandler.hasGroupPermission(userNonmember, group, MemberRole.MANAGER));
        Assert.assertFalse(permissionHandler.hasGroupPermission(userNonmember, group, MemberRole.ADMIN));

        Assert.assertTrue(permissionHandler.hasGroupPermission(userMember, group, MemberRole.MEMBER));
        Assert.assertFalse(permissionHandler.hasGroupPermission(userMember, group, MemberRole.MANAGER));
        Assert.assertFalse(permissionHandler.hasGroupPermission(userMember, group, MemberRole.ADMIN));

        Assert.assertTrue(permissionHandler.hasGroupPermission(userManager, group, MemberRole.MEMBER));
        Assert.assertTrue(permissionHandler.hasGroupPermission(userManager, group, MemberRole.MANAGER));
        Assert.assertFalse(permissionHandler.hasGroupPermission(userManager, group, MemberRole.ADMIN));

        Assert.assertTrue(permissionHandler.hasGroupPermission(userAdmin, group, MemberRole.MEMBER));
        Assert.assertTrue(permissionHandler.hasGroupPermission(userAdmin, group, MemberRole.MANAGER));
        Assert.assertTrue(permissionHandler.hasGroupPermission(userAdmin, group, MemberRole.ADMIN));
    }
}