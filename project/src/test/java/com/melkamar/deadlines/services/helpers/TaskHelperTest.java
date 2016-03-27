package com.melkamar.deadlines.services.helpers;

import com.melkamar.deadlines.DeadlinesApplication;
import com.melkamar.deadlines.exceptions.WrongParameterException;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.task.Priority;
import com.melkamar.deadlines.model.task.Task;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 27.03.2016 14:36
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DeadlinesApplication.class)
public class TaskHelperTest {
    @Autowired
    private TaskHelper taskHelper;
    @Autowired
    private UserHelper userHelper;


    @Test(expected = WrongParameterException.class)
    @Transactional
    public void nullParameters() throws WrongParameterException {
        taskHelper.createTask(null, null, null, null, 0, null);
    }

    @Test(expected = WrongParameterException.class)
    @Transactional
    public void nullDeadline() throws WrongParameterException {
        User user = userHelper.createUser("TestUser", "pwd", "Some name", "a@b.c");
        taskHelper.createTask(user, "TestTask", "Task Description", Priority.NORMAL, 0, null);
    }

    @Test(expected = WrongParameterException.class)
    @Transactional
    public void negativeGrowspeed() throws WrongParameterException {
        User user = userHelper.createUser("TestUser", "pwd", "Some name", "a@b.c");
        taskHelper.createTask(user, "TestTask", "Task Description", Priority.NORMAL, 0, -10);
    }

    @Test
    @Transactional
    public void minimumInfoDeadline() throws WrongParameterException {
        User user = userHelper.createUser("TestUser", "pwd", "Some name", "a@b.c");
        taskHelper.createTask(user, "TestTask", null, null, 0, LocalDateTime.now().plusDays(10));
    }

    @Test
    @Transactional
    public void minimumInfoGrowing() throws WrongParameterException {
        User user = userHelper.createUser("TestUser", "pwd", "Some name", "a@b.c");
        taskHelper.createTask(user, "TestTask", null, null, 0, 10);
    }

    @Test
    @Transactional
    public void creatorMemberOfTask() throws WrongParameterException {
        User user = userHelper.createUser("TestUser", "pwd", "Some name", "a@b.c");
        Task task = taskHelper.createTask(user, "TestTask", null, null, 0, LocalDateTime.now().plusDays(10));

        Assert.assertTrue(task.usersOnTask().contains(user));
        Assert.assertTrue(user.tasksOfUser().contains(task));
    }
}