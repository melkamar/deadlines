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
import com.melkamar.deadlines.services.api.GroupApi;
import com.melkamar.deadlines.services.api.TaskApi;
import com.melkamar.deadlines.services.api.UserApi;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Martin Melka
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DeadlinesApplication.class)
@WebAppConfiguration
public class GroupMemberHelperTest {


    @Autowired
    private GroupMemberHelper groupMemberHelper;
    @Autowired
    private UserApi userApi;
    @Autowired
    private GroupApi groupApi;
    @Autowired
    private TaskApi taskApi;

    @Transactional
    @Test
    public void createGroupMemberGroupCreation() throws WrongParameterException, AlreadyExistsException, AlreadyExistsException {
        User user = userApi.createUser("Username", "password", "John Doe", "a@b.c");
        Group group = groupApi.createGroup("Groupname", user, "Random description");
    }

    @Transactional
    @Test
    public void createGroupMember() throws WrongParameterException, AlreadyExistsException, AlreadyExistsException {
        User user = userApi.createUser("Username", "password", "John Doe", "a@b.c");
        User user2 = userApi.createUser("NewlyAddedUser", "password", "Alice Doe", "ab@b.c");
        Group group = groupApi.createGroup("Groupname", user, "Random description");

        Assert.assertNull(groupMemberHelper.getGroupMember(user2, group));
        groupMemberHelper.createGroupMember(user2, group, MemberRole.MEMBER);

        Assert.assertNotNull(groupMemberHelper.getGroupMember(user2, group));
        Assert.assertTrue(groupMemberHelper.getGroupMember(user2, group).getUser().equals(user2));
    }

    @Transactional
    @Test
    public void createGroupMember_TasksShared() throws WrongParameterException, AlreadyExistsException, GroupPermissionException, NotMemberOfException, AlreadyExistsException {
        User user = userApi.createUser("Username", "password", "John Doe", "a@b.c");
        User user2 = userApi.createUser("NewlyAddedUser", "password", "Alice Doe", "ab@b.c");
        Group group = groupApi.createGroup("Groupname", user, "Random description");

        List<Group> groupList = new ArrayList<>();
        groupList.add(group);

        Task task1 = taskApi.createTask(user, "Task", null, Priority.NORMAL, 10, groupList, 1);
        Task task2 = taskApi.createTask(user, "Task2", null, Priority.NORMAL, 10, groupList, 1);
        Task task3 = taskApi.createTask(user2, "Task3", null, Priority.NORMAL, 10, null, 1);

        Assert.assertEquals(1, user2.getTasksOfUser().size());
        groupApi.addMember(user, group, user2);
        Assert.assertEquals(3, user2.getTasksOfUser().size());
    }

    @Transactional
    @Test(expected = AlreadyExistsException.class)
    public void createDuplicateGroupMember() throws Exception {
        User user = userApi.createUser("Username", "password", "John Doe", "a@b.c");
        Group group = groupApi.createGroup("Groupname", user, "Random description");

        groupMemberHelper.createGroupMember(user, group, MemberRole.MEMBER);
    }

    @Transactional
    @Test
    public void getGroupMember() throws WrongParameterException, AlreadyExistsException, AlreadyExistsException {
        User user = userApi.createUser("Username", "password", "John Doe", "a@b.c");
        User user2 = userApi.createUser("NewlyAddedUser", "password", "Alice Doe", "ab@b.c");
        User user3 = userApi.createUser("NewlyAddedUser2", "password", "Alice Doe", "ab@b.c");

        Group group = groupApi.createGroup("Groupname", user, "Random description");

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