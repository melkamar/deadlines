package com.melkamar.deadlines.services.api;

import com.melkamar.deadlines.DeadlinesApplication;
import com.melkamar.deadlines.exceptions.*;
import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.task.Priority;
import com.melkamar.deadlines.model.task.Task;
import com.melkamar.deadlines.model.task.TaskRole;
import com.melkamar.deadlines.services.helpers.TaskParticipantHelper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 01.04.2016 18:22
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DeadlinesApplication.class)
public class SharingAPITest {
    @Autowired
    private UserAPI userAPI;
    @Autowired
    private TaskAPI taskAPI;
    @Autowired
    private SharingAPI sharingAPI;
    @Autowired
    private TaskParticipantHelper taskParticipantHelper;
    @Autowired
    private GroupAPI groupAPI;

    @Test
    @Transactional
    public void offerTaskSharingUser() throws WrongParameterException, TaskPermissionException, NotMemberOfException, AlreadyExistsException {
        User user1 = userAPI.createUser("TestUser", "pwd", "Some name", "a@b.c");
        User user2 = userAPI.createUser("TestUserNonMember", "pwd", "Some name", "a@b.c");

        Task task1 = taskAPI.createTask(user1, "task1", null, Priority.NORMAL, 10, 10);
        Task task2 = taskAPI.createTask(user1, "task2", null, Priority.NORMAL, 10, 10);

        Assert.assertEquals(0, user1.getTaskOffers().size());
        Assert.assertEquals(0, user2.getTaskOffers().size());

        sharingAPI.offerTaskSharing(user1, task1, user2);
        Assert.assertEquals(0, user1.getTaskOffers().size());
        Assert.assertEquals(1, user2.getTaskOffers().size());

        sharingAPI.offerTaskSharing(user1, task2, user2);
        Assert.assertEquals(0, user1.getTaskOffers().size());
        Assert.assertEquals(2, user2.getTaskOffers().size());
    }

    @Test(expected = NotMemberOfException.class)
    @Transactional
    public void offerTaskSharingUser_OffererNotParticipant() throws WrongParameterException, TaskPermissionException, NotMemberOfException, AlreadyExistsException {
        User user1 = userAPI.createUser("User1", "pwd", "Some name", "a@b.c");
        User user2 = userAPI.createUser("User2", "pwd", "Some name", "a@b.c");
        User user3 = userAPI.createUser("User3", "pwd", "Some name", "a@b.c");

        Task task1 = taskAPI.createTask(user1, "task1", null, Priority.NORMAL, 10, 10);

        sharingAPI.offerTaskSharing(user2, task1, user3);
    }

    @Test(expected = AlreadyExistsException.class)
    @Transactional
    public void offerTaskSharingUser_UserAlreadyParticipant() throws WrongParameterException, TaskPermissionException, NotMemberOfException, AlreadyExistsException {
        User user1 = userAPI.createUser("User1", "pwd", "Some name", "a@b.c");
        User user2 = userAPI.createUser("User2", "pwd", "Some name", "a@b.c");
        User user3 = userAPI.createUser("User3", "pwd", "Some name", "a@b.c");

        Task task1 = taskAPI.createTask(user1, "task1", null, Priority.NORMAL, 10, 10);
        taskParticipantHelper.editOrCreateTaskParticipant(user2, task1, TaskRole.WATCHER, null, true);

        sharingAPI.offerTaskSharing(user2, task1, user1);
    }

    @Test
    @Transactional
    public void offerTaskSharingGroup() throws WrongParameterException, TaskPermissionException, NotMemberOfException, AlreadyExistsException {
        User user1 = userAPI.createUser("TestUser", "pwd", "Some name", "a@b.c");
        Group group = groupAPI.createGroup("Group", user1, null);

        Task task1 = taskAPI.createTask(user1, "task1", null, Priority.NORMAL, 10, 10);
        Task task2 = taskAPI.createTask(user1, "task2", null, Priority.NORMAL, 10, 10);

        Assert.assertEquals(0, group.getTaskOffers().size());

        sharingAPI.offerTaskSharing(user1, task1, group);
        Assert.assertEquals(1, group.getTaskOffers().size());

        sharingAPI.offerTaskSharing(user1, task2, group);
        Assert.assertEquals(2, group.getTaskOffers().size());
    }

    @Test(expected = NotMemberOfException.class)
    @Transactional
    public void offerTaskSharingGroup_OffererNotParticipant() throws WrongParameterException, TaskPermissionException, NotMemberOfException, AlreadyExistsException {
        User user1 = userAPI.createUser("User1", "pwd", "Some name", "a@b.c");
        User user2 = userAPI.createUser("User2", "pwd", "Some name", "a@b.c");
        Group group = groupAPI.createGroup("Group", user1, null);

        Task task1 = taskAPI.createTask(user1, "task1", null, Priority.NORMAL, 10, 10);

        sharingAPI.offerTaskSharing(user2, task1, group);
    }

    @Test(expected = AlreadyExistsException.class)
    @Transactional
    public void offerTaskSharingGroup_UserAlreadyParticipant() throws WrongParameterException, TaskPermissionException, NotMemberOfException, AlreadyExistsException, GroupPermissionException {
        User user1 = userAPI.createUser("User1", "pwd", "Some name", "a@b.c");
        Group group = groupAPI.createGroup("Group", user1, null);

        Task task1 = taskAPI.createTask(user1, "task1", null, Priority.NORMAL, 10, 10);
        groupAPI.addTask(user1, group, task1);

        sharingAPI.offerTaskSharing(user1, task1, group);
    }
}