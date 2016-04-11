package com.melkamar.deadlines.dao.task;

import com.melkamar.deadlines.DeadlinesApplication;
import com.melkamar.deadlines.exceptions.*;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.task.Priority;
import com.melkamar.deadlines.model.task.Task;
import com.melkamar.deadlines.model.task.TaskRole;
import com.melkamar.deadlines.model.task.TaskStatus;
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

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 07.04.2016 19:37
 */
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DeadlinesApplication.class)
public class TaskDAOHibernateTest {


    @Autowired
    private TaskAPI taskAPI;
    @Autowired
    private UserAPI userAPI;
    @Autowired
    private TaskDAOHibernate taskDAO;


    @SuppressWarnings("Duplicates")
    @Transactional
    @Test
    public void findByStatus() throws WrongParameterException, NotMemberOfException, NotAllowedException, UserAlreadyExistsException {
        User user = userAPI.createUser("User1", "pwd", "Name", "email@something.cz");
        Task task1 = taskAPI.createTask(user, "Task1", "Description", Priority.NORMAL, 10, 1);
        Task task2 = taskAPI.createTask(user, "Task2", "Description", Priority.NORMAL, 10, 1);
        Task task3 = taskAPI.createTask(user, "Task3", "Description", Priority.NORMAL, 10, 1);
        Task task4 = taskAPI.createTask(user, "Task4", "Description", Priority.NORMAL, 10, 1);
        Task task5 = taskAPI.createTask(user, "Task5", "Description", Priority.NORMAL, 10, 1);
        Task task6 = taskAPI.createTask(user, "Task6", "Description", Priority.NORMAL, 10, 1);
        Task task7 = taskAPI.createTask(user, "Task7", "Description", Priority.NORMAL, 10, 1);

        taskAPI.setTaskRole(user, task1, TaskRole.WORKER);
        taskAPI.setTaskRole(user, task2, TaskRole.WORKER);
        taskAPI.setTaskRole(user, task3, TaskRole.WORKER);
        taskAPI.setTaskRole(user, task4, TaskRole.WORKER);
        taskAPI.setTaskRole(user, task5, TaskRole.WORKER);
        taskAPI.setTaskRole(user, task6, TaskRole.WORKER);
        taskAPI.setTaskRole(user, task7, TaskRole.WORKER);

        List<Task> allTasks = taskDAO.findAll();
        int open = 0;
        int inprogress = 0;
        int cancelled = 0;
        int completed = 0;
        for (Task task : allTasks) {
            switch (task.getStatus()) {
                case OPEN:
                    open++;
                    break;
                case IN_PROGRESS:
                    inprogress++;
                    break;
                case CANCELLED:
                    cancelled++;
                    break;
                case COMPLETED:
                    completed++;
                    break;
            }
        }

        Assert.assertEquals(open, taskDAO.findByStatus(TaskStatus.OPEN).size());
        Assert.assertEquals(inprogress, taskDAO.findByStatus(TaskStatus.IN_PROGRESS).size());
        Assert.assertEquals(cancelled, taskDAO.findByStatus(TaskStatus.CANCELLED).size());
        Assert.assertEquals(completed, taskDAO.findByStatus(TaskStatus.COMPLETED).size());

        taskAPI.setTaskStatus(user, task1, TaskStatus.OPEN);
        taskAPI.setTaskStatus(user, task2, TaskStatus.CANCELLED);
        taskAPI.setTaskStatus(user, task3, TaskStatus.IN_PROGRESS);
        taskAPI.setTaskStatus(user, task4, TaskStatus.CANCELLED);
        taskAPI.setTaskStatus(user, task5, TaskStatus.COMPLETED);
        taskAPI.setTaskStatus(user, task6, TaskStatus.OPEN);
        taskAPI.setTaskStatus(user, task7, TaskStatus.CANCELLED);

        open = 0;
        inprogress = 0;
        cancelled = 0;
        completed = 0;
        for (Task task : allTasks) {
            switch (task.getStatus()) {
                case OPEN:
                    open++;
                    break;
                case IN_PROGRESS:
                    inprogress++;
                    break;
                case CANCELLED:
                    cancelled++;
                    break;
                case COMPLETED:
                    completed++;
                    break;
            }
        }

        Assert.assertEquals(open, taskDAO.findByStatus(TaskStatus.OPEN).size());
        Assert.assertEquals(inprogress, taskDAO.findByStatus(TaskStatus.IN_PROGRESS).size());
        Assert.assertEquals(cancelled, taskDAO.findByStatus(TaskStatus.CANCELLED).size());
        Assert.assertEquals(completed, taskDAO.findByStatus(TaskStatus.COMPLETED).size());

    }
}