package com.melkamar.deadlines.services.helpers;

import com.melkamar.deadlines.DeadlinesApplication;
import com.melkamar.deadlines.exceptions.AlreadyExistsException;
import com.melkamar.deadlines.exceptions.WrongParameterException;
import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.MemberRole;
import com.melkamar.deadlines.model.User;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import static org.junit.Assert.*;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 28.03.2016 12:16
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DeadlinesApplication.class)
public class GroupMemberHelperTest {


    @Autowired
    private GroupMemberHelper groupMemberHelper;
    @Autowired
    private UserHelper userHelper;
    @Autowired
    private GroupHelper groupHelper;

    @Transactional
    @Test
    public void createGroupMemberGroupCreation() throws WrongParameterException {
        User user = userHelper.createUser("Username", "password", "John Doe", "a@b.c");
        Group group = groupHelper.createGroup("Groupname", user, "Random description");
    }

    @Transactional
    @Test
    public void createGroupMember() throws WrongParameterException, AlreadyExistsException {
        User user = userHelper.createUser("Username", "password", "John Doe", "a@b.c");
        User user2 = userHelper.createUser("NewlyAddedUser", "password", "Alice Doe", "ab@b.c");
        Group group = groupHelper.createGroup("Groupname", user, "Random description");

        Assert.assertNull(groupMemberHelper.getGroupMember(user2, group));
        groupMemberHelper.createGroupMember(user2, group, MemberRole.MEMBER);

        Assert.assertNotNull(groupMemberHelper.getGroupMember(user2, group));
        Assert.assertTrue(groupMemberHelper.getGroupMember(user2, group).getUser().equals(user2));
    }

    @Transactional
    @Test(expected = AlreadyExistsException.class)
    public void createDuplicateGroupMember() throws Exception {
        User user = userHelper.createUser("Username", "password", "John Doe", "a@b.c");
        Group group = groupHelper.createGroup("Groupname", user, "Random description");

        groupMemberHelper.createGroupMember(user, group, MemberRole.MEMBER);
    }

    @Transactional
    @Test
    public void addOrEditGroupMemberCreates() {
        throw new NotImplementedException();
    }

    @Transactional
    @Test
    public void addOrEditGroupMemberEdits() {
        throw new NotImplementedException();
    }

    @Transactional
    @Test
    public void getGroupMember() {
        throw new NotImplementedException();
    }
}