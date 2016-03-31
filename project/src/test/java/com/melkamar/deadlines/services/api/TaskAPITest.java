package com.melkamar.deadlines.services.api;

import com.melkamar.deadlines.DeadlinesApplication;
import com.melkamar.deadlines.dao.task.TaskDAO;
import com.melkamar.deadlines.dao.user.UserDAO;
import com.melkamar.deadlines.exceptions.*;
import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.TaskParticipant;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.task.Priority;
import com.melkamar.deadlines.model.task.Task;
import com.melkamar.deadlines.model.task.TaskRole;
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

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 27.03.2016 14:36
 */
//@Rollback(value = false)
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DeadlinesApplication.class)
public class TaskAPITest {
    @Autowired
    private TaskAPI taskAPI;
    @Autowired
    private UserAPI userAPI;

    @Autowired
    private UserDAO userDAO;
    @Autowired
    private TaskDAO taskDAO;
    @Autowired
    private GroupAPI groupAPI;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test(expected = WrongParameterException.class)
    @Transactional
    public void nullParameters() throws WrongParameterException {
        taskAPI.createTask(null, null, null, null, 0, null);
    }

    @Test(expected = WrongParameterException.class)
    @Transactional
    public void nullDeadline() throws WrongParameterException {
        User user = userAPI.createUser("TestUser", "pwd", "Some name", "a@b.c");
        taskAPI.createTask(user, "TestTask", "Task Description", Priority.NORMAL, 0, null);
    }

    @Test(expected = WrongParameterException.class)
    @Transactional
    public void negativeGrowspeed() throws WrongParameterException {
        User user = userAPI.createUser("TestUser", "pwd", "Some name", "a@b.c");
        taskAPI.createTask(user, "TestTask", "Task Description", Priority.NORMAL, 0, -10);
    }

    @Test
    @Transactional
    public void minimumInfoDeadline() throws WrongParameterException {
        User user = userAPI.createUser("TestUser", "pwd", "Some name", "a@b.c");
        taskAPI.createTask(user, "TestTask", null, null, 0, LocalDateTime.now().plusDays(10));
    }

    @Test
    @Transactional
    public void minimumInfoGrowing() throws WrongParameterException {
        User user = userAPI.createUser("TestUser", "pwd", "Some name", "a@b.c");
        taskAPI.createTask(user, "TestTask", null, null, 0, 10);
    }

    @Test
    @Transactional
    public void creatorMemberOfTask() throws WrongParameterException {
        User user = userAPI.createUser("TestUser", "pwd", "Some name", "a@b.c");
        Task task = taskAPI.createTask(user, "TestTask", null, null, 0, LocalDateTime.now().plusDays(10));

        Assert.assertTrue(task.usersOnTask().contains(user));
        Assert.assertTrue(user.tasksOfUser().contains(task));
    }

    @Test
    @Transactional
    public void userTaskRelationPersistence() throws WrongParameterException {
        User user = userAPI.createUser("TestUser", "pwd", "Some name", "a@b.c");
        Task task = taskAPI.createTask(user, "TestTask", null, null, 0, LocalDateTime.now().plusDays(10));

        User retrievedUser = userDAO.findByUsername("TestUser");
        Assert.assertTrue(retrievedUser.tasksOfUser().size() == 1);

        Task retrievedTask = retrievedUser.tasksOfUser().iterator().next();
        Assert.assertTrue(retrievedTask.getName().equals("TestTask"));

        System.out.println("Original:  " + task);
        System.out.println("Retrieved: " + retrievedTask);
    }

    @Test
    @Transactional
    public void createGroupTasks() throws WrongParameterException, GroupPermissionException, NotMemberOfException {
        User user = userAPI.createUser("TestUser", "pwd", "Some name", "a@b.c");
        User userNonMember = userAPI.createUser("TestUserNonMember", "pwd", "Some name", "a@b.c");
        Group group = groupAPI.createGroup("TestGroup", user, null);

        Set<Group> groupSet = new HashSet<>();
        groupSet.add(group);

        Task task = taskAPI.createTask(user, "TestTask", null, null, 0, groupSet, LocalDateTime.now().plusDays(10));
        Task task2 = taskAPI.createTask(user, "TestTask2", null, null, 0, groupSet, LocalDateTime.now().plusDays(101));
        Task task3 = taskAPI.createTask(user, "TestTask3", null, null, 0, groupSet, LocalDateTime.now().plusDays(102));
        Task task4 = taskAPI.createTask(user, "TestTask4", null, null, 0, null, LocalDateTime.now().plusDays(102));

        Assert.assertEquals(user.tasksOfUser().size(), 4);
        Assert.assertEquals(userNonMember.tasksOfUser().size(), 0);
        Assert.assertEquals(group.getSharedTasks().size(), 3);
    }

    @Test(expected = NotMemberOfException.class)
    @Transactional
    public void createGroupTaskByNonMember() throws WrongParameterException, GroupPermissionException, NotMemberOfException {
        User user = userAPI.createUser("TestUser", "pwd", "Some name", "a@b.c");
        User userNonMember = userAPI.createUser("TestUserNonMember", "pwd", "Some name", "a@b.c");
        Group group = groupAPI.createGroup("TestGroup", user, null);

        Set<Group> groupSet = new HashSet<>();
        groupSet.add(group);

        Task task = taskAPI.createTask(userNonMember, "TestTask", null, null, 0, groupSet, LocalDateTime.now().plusDays(10));
    }

    @Test
    @Transactional
    public void createGroupTaskByMemberNotManager() throws WrongParameterException, GroupPermissionException, NotMemberOfException, AlreadyExistsException {
        User userAdmin = userAPI.createUser("TestUserd", "pwd", "Some name", "a@b.c");
        User userMember = userAPI.createUser("TestUserNonMember", "pwd", "Some name", "a@b.c");
        Group group = groupAPI.createGroup("TestGroup", userAdmin, null);

        groupAPI.addMember(userAdmin, group, userMember);

        Set<Group> groupSet = new HashSet<>();
        groupSet.add(group);

        expectedException.expect(GroupPermissionException.class);
        Task task = taskAPI.createTask(userMember, "TestTask", null, null, 0, groupSet, LocalDateTime.now().plusDays(10));
    }

    // WORK REPORTS TESTS
    @Test(expected = WrongParameterException.class)
    @Transactional
    public void reportWorkInvalidManhours() throws WrongParameterException, NotMemberOfException, WrongRoleException {
        User user = userAPI.createUser("TestUser", "pwd", "Some name", "a@b.c");
        Task task = taskAPI.createTask(user, "TestTask", null, null, 0, LocalDateTime.now().plusDays(10));

        TaskParticipant participant = user.getParticipants().iterator().next();
        participant.setRole(TaskRole.WORKER);

        taskAPI.reportWork(user, task, -1);
    }

    @Test(expected = NotMemberOfException.class)
    @Transactional
    public void reportWorkUserNotParticipant() throws WrongParameterException, NotMemberOfException, WrongRoleException {
        User user = userAPI.createUser("TestUser", "pwd", "Some name", "a@b.c");
        User nonParticipant = userAPI.createUser("NotAParticipant", "pwd", "Some name", "a@b.c");
        Task task = taskAPI.createTask(user, "TestTask", null, null, 0, LocalDateTime.now().plusDays(10));

        TaskParticipant participant = user.getParticipants().iterator().next();
        participant.setRole(TaskRole.WORKER);

        taskAPI.reportWork(nonParticipant, task, 10);
    }

    @Test(expected = WrongRoleException.class)
    @Transactional
    public void reportWorkUserNotWorker() throws WrongParameterException, NotMemberOfException, WrongRoleException {
        User user = userAPI.createUser("TestUser", "pwd", "Some name", "a@b.c");
        Task task = taskAPI.createTask(user, "TestTask", null, null, 0, LocalDateTime.now().plusDays(10));

        TaskParticipant participant = user.getParticipants().iterator().next();

        taskAPI.reportWork(user, task, 10);
    }

    @Test
    @Transactional
    public void reportWorkPersistence() throws WrongParameterException, NotMemberOfException, WrongRoleException {
        User user = userAPI.createUser("TestUser", "pwd", "Some name", "a@b.c");
        User nonParticipant = userAPI.createUser("NotAParticipant", "pwd", "Some name", "a@b.c");
        Task task = taskAPI.createTask(user, "TestTask", null, null, 0, LocalDateTime.now().plusDays(10));
        Long taskId = task.getId();

        TaskParticipant participant = user.getParticipants().iterator().next();
        participant.setRole(TaskRole.WORKER);

        taskAPI.reportWork(user, task, 10);
        Assert.assertTrue(task.getWorkReports().size() == 1);

        taskAPI.reportWork(user, task, 5);
        Assert.assertTrue(task.getWorkReports().size() == 2);

        taskAPI.reportWork(user, task, 12);
        Assert.assertTrue(task.getWorkReports().size() == 3);

        Task retrievedTask = taskDAO.findById(taskId);
        Assert.assertNotNull(retrievedTask);

        Assert.assertTrue(retrievedTask.getWorkReports().size() == 3);
        Assert.assertTrue(retrievedTask.manhoursWorked() == 10 + 5 + 12);
    }


}