package com.melkamar.deadlines.services.helpers;

import com.melkamar.deadlines.DeadlinesApplication;
import com.melkamar.deadlines.exceptions.AlreadyExistsException;
import com.melkamar.deadlines.exceptions.GroupPermissionException;
import com.melkamar.deadlines.exceptions.NotMemberOfException;
import com.melkamar.deadlines.exceptions.WrongParameterException;
import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.MemberRole;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.task.Priority;
import com.melkamar.deadlines.model.task.Task;
import com.melkamar.deadlines.services.api.GroupAPI;
import com.melkamar.deadlines.services.api.TaskAPI;
import com.melkamar.deadlines.services.api.UserAPI;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

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
    private UserAPI userAPI;
    @Autowired
    private GroupAPI groupAPI;
    @Autowired
    private TaskAPI taskAPI;

    @Transactional
    @Test
    public void createGroupMemberGroupCreation() throws WrongParameterException {
        User user = userAPI.createUser("Username", "password", "John Doe", "a@b.c");
        Group group = groupAPI.createGroup("Groupname", user, "Random description");
    }

    @Transactional
    @Test
    public void createGroupMember() throws WrongParameterException, AlreadyExistsException {
        User user = userAPI.createUser("Username", "password", "John Doe", "a@b.c");
        User user2 = userAPI.createUser("NewlyAddedUser", "password", "Alice Doe", "ab@b.c");
        Group group = groupAPI.createGroup("Groupname", user, "Random description");

        Assert.assertNull(groupMemberHelper.getGroupMember(user2, group));
        groupMemberHelper.createGroupMember(user2, group, MemberRole.MEMBER);

        Assert.assertNotNull(groupMemberHelper.getGroupMember(user2, group));
        Assert.assertTrue(groupMemberHelper.getGroupMember(user2, group).getUser().equals(user2));
    }

    @Transactional
    @Test
    public void createGroupMember_TasksShared() throws WrongParameterException, AlreadyExistsException, GroupPermissionException, NotMemberOfException {
        User user = userAPI.createUser("Username", "password", "John Doe", "a@b.c");
        User user2 = userAPI.createUser("NewlyAddedUser", "password", "Alice Doe", "ab@b.c");
        Group group = groupAPI.createGroup("Groupname", user, "Random description");

        Set<Group> groupSet = new HashSet<>();
        groupSet.add(group);

        Task task1 = taskAPI.createTask(user, "Task", null, Priority.NORMAL, 10, groupSet, 1);
        Task task2 = taskAPI.createTask(user, "Task2", null, Priority.NORMAL, 10, groupSet, 1);
        Task task3 = taskAPI.createTask(user2, "Task3", null, Priority.NORMAL, 10, null, 1);

        Assert.assertEquals(1, user2.tasksOfUser().size());
        groupAPI.addMember(user, group, user2);
        Assert.assertEquals(3, user2.tasksOfUser().size());
    }

    @Transactional
    @Test(expected = AlreadyExistsException.class)
    public void createDuplicateGroupMember() throws Exception {
        User user = userAPI.createUser("Username", "password", "John Doe", "a@b.c");
        Group group = groupAPI.createGroup("Groupname", user, "Random description");

        groupMemberHelper.createGroupMember(user, group, MemberRole.MEMBER);
    }

    @Transactional
    @Test
    public void getGroupMember() throws WrongParameterException, AlreadyExistsException {
        User user = userAPI.createUser("Username", "password", "John Doe", "a@b.c");
        User user2 = userAPI.createUser("NewlyAddedUser", "password", "Alice Doe", "ab@b.c");
        User user3 = userAPI.createUser("NewlyAddedUser2", "password", "Alice Doe", "ab@b.c");

        Group group = groupAPI.createGroup("Groupname", user, "Random description");

        Assert.assertTrue(groupMemberHelper.getGroupMember(user, group).getUser().equals(user));
        Assert.assertNull(groupMemberHelper.getGroupMember(user2, group));
        Assert.assertNull(groupMemberHelper.getGroupMember(user3, group));

        groupMemberHelper.createGroupMember(user2, group, MemberRole.MEMBER);
        Assert.assertTrue(groupMemberHelper.getGroupMember(user, group).getUser().equals(user));
        Assert.assertTrue(groupMemberHelper.getGroupMember(user2, group).getUser().equals(user2));
        Assert.assertNull(groupMemberHelper.getGroupMember(user3, group));

        groupMemberHelper.createGroupMember(user3, group, MemberRole.MANAGER);
        Assert.assertTrue(groupMemberHelper.getGroupMember(user, group).getUser().equals(user));
        Assert.assertTrue(groupMemberHelper.getGroupMember(user2, group).getUser().equals(user2));
        Assert.assertTrue(groupMemberHelper.getGroupMember(user3, group).getUser().equals(user3));
    }
}