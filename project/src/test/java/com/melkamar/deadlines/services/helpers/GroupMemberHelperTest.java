package com.melkamar.deadlines.services.helpers;

import com.melkamar.deadlines.DeadlinesApplication;
import com.melkamar.deadlines.exceptions.AlreadyExistsException;
import com.melkamar.deadlines.exceptions.WrongParameterException;
import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.MemberRole;
import com.melkamar.deadlines.model.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 28.03.2016 12:16
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DeadlinesApplication.class)
public class GroupMemberHelperTest {

//
//    @Autowired
//    private GroupMemberHelper groupMemberHelper;
//    @Autowired
//    private UserAPI userAPI;
//    @Autowired
//    private GroupHelper groupHelper;
//
//    @Transactional
//    @Test
//    public void createGroupMemberGroupCreation() throws WrongParameterException {
//        User user = userAPI.createUser("Username", "password", "John Doe", "a@b.c");
//        Group group = groupHelper.createGroup("Groupname", user, "Random description");
//    }
//
//    @Transactional
//    @Test
//    public void createGroupMember() throws WrongParameterException, AlreadyExistsException {
//        User user = userAPI.createUser("Username", "password", "John Doe", "a@b.c");
//        User user2 = userAPI.createUser("NewlyAddedUser", "password", "Alice Doe", "ab@b.c");
//        Group group = groupHelper.createGroup("Groupname", user, "Random description");
//
//        Assert.assertNull(groupMemberHelper.getGroupMember(user2, group));
//        groupMemberHelper.createGroupMember(user2, group, MemberRole.MEMBER);
//
//        Assert.assertNotNull(groupMemberHelper.getGroupMember(user2, group));
//        Assert.assertTrue(groupMemberHelper.getGroupMember(user2, group).getUser().equals(user2));
//    }
//
//    @Transactional
//    @Test(expected = AlreadyExistsException.class)
//    public void createDuplicateGroupMember() throws Exception {
//        User user = userAPI.createUser("Username", "password", "John Doe", "a@b.c");
//        Group group = groupHelper.createGroup("Groupname", user, "Random description");
//
//        groupMemberHelper.createGroupMember(user, group, MemberRole.MEMBER);
//    }
//
//    @Transactional
//    @Test
//    public void getGroupMember() throws WrongParameterException, AlreadyExistsException {
//        User user = userAPI.createUser("Username", "password", "John Doe", "a@b.c");
//        User user2 = userAPI.createUser("NewlyAddedUser", "password", "Alice Doe", "ab@b.c");
//        User user3 = userAPI.createUser("NewlyAddedUser2", "password", "Alice Doe", "ab@b.c");
//
//        Group group = groupHelper.createGroup("Groupname", user, "Random description");
//
//        Assert.assertTrue(groupMemberHelper.getGroupMember(user, group).getUser().equals(user));
//        Assert.assertNull(groupMemberHelper.getGroupMember(user2, group));
//        Assert.assertNull(groupMemberHelper.getGroupMember(user3, group));
//
//        groupMemberHelper.createGroupMember(user2, group, MemberRole.MEMBER);
//        Assert.assertTrue(groupMemberHelper.getGroupMember(user, group).getUser().equals(user));
//        Assert.assertTrue(groupMemberHelper.getGroupMember(user2, group).getUser().equals(user2));
//        Assert.assertNull(groupMemberHelper.getGroupMember(user3, group));
//
//        groupMemberHelper.createGroupMember(user3, group, MemberRole.MANAGER);
//        Assert.assertTrue(groupMemberHelper.getGroupMember(user, group).getUser().equals(user));
//        Assert.assertTrue(groupMemberHelper.getGroupMember(user2, group).getUser().equals(user2));
//        Assert.assertTrue(groupMemberHelper.getGroupMember(user3, group).getUser().equals(user3));
//    }
}