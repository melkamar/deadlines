package com.melkamar.deadlines.dao.taskparticipant;

import com.melkamar.deadlines.DeadlinesApplication;
import com.melkamar.deadlines.exceptions.WrongParameterException;
import com.melkamar.deadlines.model.TaskParticipant;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.task.Task;
import com.melkamar.deadlines.services.helpers.TaskHelper;
import com.melkamar.deadlines.services.helpers.UserHelper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 27.03.2016 18:51
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DeadlinesApplication.class)
public class TaskParticipantDAOHibernateTest {
    @Autowired
    private TaskParticipantDAOHibernate daoHibernate;
    @Autowired
    private TaskHelper taskHelper;
    @Autowired
    private UserHelper userHelper;

    @Test
    @Transactional
    public void findByUserAndTask() throws WrongParameterException {
        User user = userHelper.createUser("TestUser", "pwd", "Some name", "a@b.c");
        Task task = taskHelper.createTask(user, "TestTask", null, null, 0, LocalDateTime.now().plusDays(10));

        TaskParticipant taskParticipant = daoHibernate.findByUserAndTask(user, task);
        Assert.assertNotNull(taskParticipant);
        Assert.assertTrue(taskParticipant.getUser().equals(user));
        Assert.assertTrue(taskParticipant.getTask().equals(task));
    }
}