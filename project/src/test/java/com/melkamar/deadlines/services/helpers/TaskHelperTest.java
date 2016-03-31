package com.melkamar.deadlines.services.helpers;

import com.melkamar.deadlines.services.api.UserAPI;

import com.melkamar.deadlines.DeadlinesApplication;
import com.melkamar.deadlines.dao.task.TaskDAO;
import com.melkamar.deadlines.dao.user.UserDAO;
import com.melkamar.deadlines.exceptions.NotMemberOfException;
import com.melkamar.deadlines.exceptions.WrongParameterException;
import com.melkamar.deadlines.exceptions.WrongRoleException;
import com.melkamar.deadlines.model.TaskParticipant;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.task.Priority;
import com.melkamar.deadlines.model.task.Task;
import com.melkamar.deadlines.model.task.TaskRole;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 27.03.2016 14:36
 */
//@Rollback(value = false)
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DeadlinesApplication.class)
public class TaskHelperTest {
    @Autowired
    private TaskHelper taskHelper;
    @Autowired
    private UserAPI userAPI;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private TaskDAO taskDAO;


    @Test(expected = WrongParameterException.class)
    @Transactional
    public void nullParameters() throws WrongParameterException {
        taskHelper.createTask(null, null, null, null, 0, null);
    }

    @Test(expected = WrongParameterException.class)
    @Transactional
    public void nullDeadline() throws WrongParameterException {
        User user = userAPI.createUser("TestUser", "pwd", "Some name", "a@b.c");
        taskHelper.createTask(user, "TestTask", "Task Description", Priority.NORMAL, 0, null);
    }

    @Test(expected = WrongParameterException.class)
    @Transactional
    public void negativeGrowspeed() throws WrongParameterException {
        User user = userAPI.createUser("TestUser", "pwd", "Some name", "a@b.c");
        taskHelper.createTask(user, "TestTask", "Task Description", Priority.NORMAL, 0, -10);
    }

    @Test
    @Transactional
    public void minimumInfoDeadline() throws WrongParameterException {
        User user = userAPI.createUser("TestUser", "pwd", "Some name", "a@b.c");
        taskHelper.createTask(user, "TestTask", null, null, 0, LocalDateTime.now().plusDays(10));
    }

    @Test
    @Transactional
    public void minimumInfoGrowing() throws WrongParameterException {
        User user = userAPI.createUser("TestUser", "pwd", "Some name", "a@b.c");
        taskHelper.createTask(user, "TestTask", null, null, 0, 10);
    }

    @Test
    @Transactional
    public void creatorMemberOfTask() throws WrongParameterException {
        User user = userAPI.createUser("TestUser", "pwd", "Some name", "a@b.c");
        Task task = taskHelper.createTask(user, "TestTask", null, null, 0, LocalDateTime.now().plusDays(10));

        Assert.assertTrue(task.usersOnTask().contains(user));
        Assert.assertTrue(user.tasksOfUser().contains(task));
    }

    @Test
    @Transactional
    public void userTaskRelationPersistence() throws WrongParameterException {
        User user = userAPI.createUser("TestUser", "pwd", "Some name", "a@b.c");
        Task task = taskHelper.createTask(user, "TestTask", null, null, 0, LocalDateTime.now().plusDays(10));

        User retrievedUser = userDAO.findByUsername("TestUser");
        Assert.assertTrue(retrievedUser.tasksOfUser().size() == 1);

        Task retrievedTask = retrievedUser.tasksOfUser().iterator().next();
        Assert.assertTrue(retrievedTask.getName().equals("TestTask"));

        System.out.println("Original:  " + task);
        System.out.println("Retrieved: " + retrievedTask);
    }

    // WORK REPORTS TESTS
    @Test(expected = WrongParameterException.class)
    @Transactional
    public void reportWorkInvalidManhours() throws WrongParameterException, NotMemberOfException, WrongRoleException {
        User user = userAPI.createUser("TestUser", "pwd", "Some name", "a@b.c");
        Task task = taskHelper.createTask(user, "TestTask", null, null, 0, LocalDateTime.now().plusDays(10));

        TaskParticipant participant = user.getParticipants().iterator().next();
        participant.setRole(TaskRole.WORKER);

        taskHelper.reportWork(user, task, -1);
    }

    @Test(expected = NotMemberOfException.class)
    @Transactional
    public void reportWorkUserNotParticipant() throws WrongParameterException, NotMemberOfException, WrongRoleException {
        User user = userAPI.createUser("TestUser", "pwd", "Some name", "a@b.c");
        User nonParticipant = userAPI.createUser("NotAParticipant", "pwd", "Some name", "a@b.c");
        Task task = taskHelper.createTask(user, "TestTask", null, null, 0, LocalDateTime.now().plusDays(10));

        TaskParticipant participant = user.getParticipants().iterator().next();
        participant.setRole(TaskRole.WORKER);

        taskHelper.reportWork(nonParticipant, task, 10);
    }

    @Test(expected = WrongRoleException.class)
    @Transactional
    public void reportWorkUserNotWorker() throws WrongParameterException, NotMemberOfException, WrongRoleException {
        User user = userAPI.createUser("TestUser", "pwd", "Some name", "a@b.c");
        Task task = taskHelper.createTask(user, "TestTask", null, null, 0, LocalDateTime.now().plusDays(10));

        TaskParticipant participant = user.getParticipants().iterator().next();

        taskHelper.reportWork(user, task, 10);
    }

    @Test
    @Transactional
    public void reportWorkPersistence() throws WrongParameterException, NotMemberOfException, WrongRoleException {
        User user = userAPI.createUser("TestUser", "pwd", "Some name", "a@b.c");
        User nonParticipant = userAPI.createUser("NotAParticipant", "pwd", "Some name", "a@b.c");
        Task task = taskHelper.createTask(user, "TestTask", null, null, 0, LocalDateTime.now().plusDays(10));
        Long taskId = task.getId();

        TaskParticipant participant = user.getParticipants().iterator().next();
        participant.setRole(TaskRole.WORKER);

        taskHelper.reportWork(user, task, 10);
        Assert.assertTrue(task.getWorkReports().size() == 1);

        taskHelper.reportWork(user, task, 5);
        Assert.assertTrue(task.getWorkReports().size() == 2);

        taskHelper.reportWork(user, task, 12);
        Assert.assertTrue(task.getWorkReports().size() == 3);

        Task retrievedTask = taskDAO.findById(taskId);
        Assert.assertNotNull(retrievedTask);

        Assert.assertTrue(retrievedTask.getWorkReports().size() == 3);
        Assert.assertTrue(retrievedTask.manhoursWorked() == 10 + 5 + 12);
    }


}