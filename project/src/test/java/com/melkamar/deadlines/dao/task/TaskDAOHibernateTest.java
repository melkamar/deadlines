package com.melkamar.deadlines.dao.task;

import com.melkamar.deadlines.DeadlinesApplication;
import com.melkamar.deadlines.exceptions.*;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.task.Priority;
import com.melkamar.deadlines.model.task.Task;
import com.melkamar.deadlines.model.task.TaskRole;
import com.melkamar.deadlines.model.task.TaskStatus;
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

import java.util.List;

/**
 * @author Martin Melka
 */
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DeadlinesApplication.class)
public class TaskDAOHibernateTest {


    @Autowired
    private TaskApi taskApi;
    @Autowired
    private UserApi userApi;
    @Autowired
    private TaskDAOHibernate taskDAO;


    @SuppressWarnings("Duplicates")
    @Transactional
    @Test
    public void findByStatus() throws WrongParameterException, NotMemberOfException, NotAllowedException, AlreadyExistsException, TaskPermissionException {
        User user = userApi.createUser("User1", "pwd", "Name", "email@something.cz");
        Task task1 = taskApi.createTask(user, "Task1", "Description", Priority.NORMAL, 10, 1);
        Task task2 = taskApi.createTask(user, "Task2", "Description", Priority.NORMAL, 10, 1);
        Task task3 = taskApi.createTask(user, "Task3", "Description", Priority.NORMAL, 10, 1);
        Task task4 = taskApi.createTask(user, "Task4", "Description", Priority.NORMAL, 10, 1);
        Task task5 = taskApi.createTask(user, "Task5", "Description", Priority.NORMAL, 10, 1);
        Task task6 = taskApi.createTask(user, "Task6", "Description", Priority.NORMAL, 10, 1);
        Task task7 = taskApi.createTask(user, "Task7", "Description", Priority.NORMAL, 10, 1);

        taskApi.setTaskRole(user, task1, TaskRole.WORKER);
        taskApi.setTaskRole(user, task2, TaskRole.WORKER);
        taskApi.setTaskRole(user, task3, TaskRole.WORKER);
        taskApi.setTaskRole(user, task4, TaskRole.WORKER);
        taskApi.setTaskRole(user, task5, TaskRole.WORKER);
        taskApi.setTaskRole(user, task6, TaskRole.WORKER);
        taskApi.setTaskRole(user, task7, TaskRole.WORKER);

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

        taskApi.setTaskStatus(user, task1, TaskStatus.OPEN);
        taskApi.setTaskStatus(user, task2, TaskStatus.CANCELLED);
        taskApi.setTaskStatus(user, task3, TaskStatus.IN_PROGRESS);
        taskApi.setTaskStatus(user, task4, TaskStatus.CANCELLED);
        taskApi.setTaskStatus(user, task5, TaskStatus.COMPLETED);
        taskApi.setTaskStatus(user, task6, TaskStatus.OPEN);
        taskApi.setTaskStatus(user, task7, TaskStatus.CANCELLED);

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