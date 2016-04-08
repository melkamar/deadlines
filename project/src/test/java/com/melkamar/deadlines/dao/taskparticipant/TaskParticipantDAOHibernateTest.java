package com.melkamar.deadlines.dao.taskparticipant;

import com.melkamar.deadlines.DeadlinesApplication;
import com.melkamar.deadlines.exceptions.*;
import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.TaskParticipant;
import com.melkamar.deadlines.model.User;
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
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 27.03.2016 18:51
 */
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DeadlinesApplication.class)
public class TaskParticipantDAOHibernateTest {
    @Autowired
    private TaskParticipantDAOHibernate taskParticipantDAO;
    @Autowired
    private TaskAPI taskAPI;
    @Autowired
    private UserAPI userAPI;
    @Autowired
    private GroupAPI groupAPI;


    @Test
    @Transactional
    public void findByUserAndTask() throws WrongParameterException, UserAlreadyExistsException {
        User user = userAPI.createUser("TestUser", "pwd", "Some name", "a@b.c");
        Task task = taskAPI.createTask(user, "TestTask", null, null, 0, LocalDateTime.now().plusDays(10));

        TaskParticipant taskParticipant = taskParticipantDAO.findByUserAndTask(user, task);
        Assert.assertNotNull(taskParticipant);
        Assert.assertTrue(taskParticipant.getUser().equals(user));
        Assert.assertTrue(taskParticipant.getTask().equals(task));
    }

    @Test
    @Transactional
    public void findByUserAndGroup() throws WrongParameterException, GroupPermissionException, NotMemberOfException, AlreadyExistsException, UserAlreadyExistsException {
        User user = userAPI.createUser("TestUser", "pwd", "Some name", "a@b.c");
        Task task = taskAPI.createTask(user, "TestTask", null, null, 0, LocalDateTime.now().plusDays(10));
        Task task2 = taskAPI.createTask(user, "TestTask2", null, null, 0, LocalDateTime.now().plusDays(101));
        Task task3 = taskAPI.createTask(user, "TestTask3", null, null, 0, LocalDateTime.now().plusDays(102));
        Group group = groupAPI.createGroup("GroupName", user, "");

        TaskParticipant taskParticipant = taskParticipantDAO.findByUserAndTask(user, task);
        TaskParticipant taskParticipant2 = taskParticipantDAO.findByUserAndTask(user, task2);
        TaskParticipant taskParticipant3 = taskParticipantDAO.findByUserAndTask(user, task3);

        groupAPI.addTask(user, group, task);
        groupAPI.addTask(user, group, task2);

        Assert.assertTrue(taskParticipantDAO.findByUserAndGroups(user, group).size() == 2);
    }
}