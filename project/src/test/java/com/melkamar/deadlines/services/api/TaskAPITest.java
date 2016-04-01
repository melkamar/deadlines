package com.melkamar.deadlines.services.api;

import com.melkamar.deadlines.DeadlinesApplication;
import com.melkamar.deadlines.dao.processing.TaskFilterRole;
import com.melkamar.deadlines.dao.processing.TaskFilterType;
import com.melkamar.deadlines.dao.processing.TaskOrdering;
import com.melkamar.deadlines.dao.task.TaskDAO;
import com.melkamar.deadlines.dao.user.UserDAO;
import com.melkamar.deadlines.exceptions.*;
import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.TaskParticipant;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.task.*;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.SystemProfileValueSource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

    @Test
    @Transactional
    public void listTasksSortByName() throws WrongParameterException {
        User user = userAPI.createUser("TestUser", "pwd", "Some name", "a@b.c");
        Task task1 = taskAPI.createTask(user, "CCC", null, null, 0, LocalDateTime.now().plusDays(10));
        Task task2 = taskAPI.createTask(user, "AAA", null, null, 0, LocalDateTime.now().plusDays(11));
        Task task3 = taskAPI.createTask(user, "BBB", null, null, 0, LocalDateTime.now().plusDays(11));


        List<Task> resultList;

        resultList = taskAPI.listTasks(user, TaskOrdering.NAME_ASC);
        Assert.assertTrue(resultList.get(0).equals(task2));
        Assert.assertTrue(resultList.get(1).equals(task3));
        Assert.assertTrue(resultList.get(2).equals(task1));

        resultList = taskAPI.listTasks(user, TaskOrdering.NAME_DESC);
        Assert.assertTrue(resultList.get(2).equals(task2));
        Assert.assertTrue(resultList.get(1).equals(task3));
        Assert.assertTrue(resultList.get(0).equals(task1));
    }

    @Test
    @Transactional
    public void listTasksSortByDateCreated() throws WrongParameterException {
        User user = userAPI.createUser("TestUser", "pwd", "Some name", "a@b.c");
        Task task1 = taskAPI.createTask(user, "CCC", null, null, 0, LocalDateTime.now().plusDays(10));
        sleepALittle();
        Task task2 = taskAPI.createTask(user, "AAA", null, null, 0, LocalDateTime.now().plusDays(11));
        sleepALittle();
        Task task3 = taskAPI.createTask(user, "BBB", null, null, 0, LocalDateTime.now().plusDays(12));
        sleepALittle();


        List<Task> resultList;

        resultList = taskAPI.listTasks(user, TaskOrdering.DATE_START_ASC);
        Assert.assertTrue(resultList.get(0).equals(task1));
        Assert.assertTrue(resultList.get(1).equals(task2));
        Assert.assertTrue(resultList.get(2).equals(task3));

        resultList = taskAPI.listTasks(user, TaskOrdering.DATE_START_DESC);
        Assert.assertTrue(resultList.get(2).equals(task1));
        Assert.assertTrue(resultList.get(1).equals(task2));
        Assert.assertTrue(resultList.get(0).equals(task3));
    }

    @Test
    @Transactional
    public void listTasksSortByDeadline() throws WrongParameterException {
        User user = userAPI.createUser("TestUser", "pwd", "Some name", "a@b.c");
        Task task1 = taskAPI.createTask(user, "CCC", null, null, 0, LocalDateTime.now().plusDays(12));
        Task task2 = taskAPI.createTask(user, "AAA", null, null, 0, LocalDateTime.now().plusDays(10));
        Task task3 = taskAPI.createTask(user, "BBB", null, null, 0, LocalDateTime.now().plusDays(11));

        Assert.assertNotNull(((DeadlineTask) task1).getDeadline());
        Assert.assertNotNull(((DeadlineTask) task2).getDeadline());
        Assert.assertNotNull(((DeadlineTask) task3).getDeadline());

        List<Task> resultList;

        resultList = taskAPI.listTasks(user, TaskOrdering.DATE_DEADLINE_ASC);
        Assert.assertTrue(resultList.get(0).equals(task2));
        Assert.assertTrue(resultList.get(1).equals(task3));
        Assert.assertTrue(resultList.get(2).equals(task1));

        resultList = taskAPI.listTasks(user, TaskOrdering.DATE_DEADLINE_DESC);
        Assert.assertTrue(resultList.get(2).equals(task2));
        Assert.assertTrue(resultList.get(1).equals(task3));
        Assert.assertTrue(resultList.get(0).equals(task1));
    }

    @Test
    @Transactional
    public void listTasksSortByDeadlineMixedWithGrowing() throws WrongParameterException {
        User user = userAPI.createUser("TestUser", "pwd", "Some name", "a@b.c");
        Task task1 = taskAPI.createTask(user, "CCC", null, null, 0, LocalDateTime.now().plusDays(12));
        Task task5 = taskAPI.createTask(user, "BBB", null, null, 0, 20);
        Task task2 = taskAPI.createTask(user, "AAA", null, null, 0, LocalDateTime.now().plusDays(10));
        Task task3 = taskAPI.createTask(user, "BBB", null, null, 0, LocalDateTime.now().plusDays(11));
        Task task4 = taskAPI.createTask(user, "BBB", null, null, 0, 10);

        Assert.assertNotNull(((DeadlineTask) task1).getDeadline());
        Assert.assertNotNull(((DeadlineTask) task2).getDeadline());
        Assert.assertNotNull(((DeadlineTask) task3).getDeadline());

        List<Task> resultList;
        List<Task> nonSorted = new ArrayList<>();


        resultList = taskAPI.listTasks(user, TaskOrdering.DATE_DEADLINE_ASC);
        nonSorted.clear();
        nonSorted.add(task4);
        nonSorted.add(task5);

        Assert.assertTrue(resultList.get(0).equals(task2));
        Assert.assertTrue(resultList.get(1).equals(task3));
        Assert.assertTrue(resultList.get(2).equals(task1));
        Assert.assertTrue(nonSorted.contains(resultList.get(3)));
        Assert.assertTrue(nonSorted.contains(resultList.get(4)));


        resultList = taskAPI.listTasks(user, TaskOrdering.DATE_DEADLINE_DESC);
        nonSorted.clear();
        nonSorted.add(task4);
        nonSorted.add(task5);

        Assert.assertTrue(resultList.get(4).equals(task2));
        Assert.assertTrue(resultList.get(3).equals(task3));
        Assert.assertTrue(resultList.get(2).equals(task1));
        Assert.assertTrue(nonSorted.contains(resultList.get(1)));
        Assert.assertTrue(nonSorted.contains(resultList.get(0)));
    }

    @Test
    @Transactional
    public void listTasksSortByWorkedPercent() throws WrongParameterException {
        User user = userAPI.createUser("TestUser", "pwd", "Some name", "a@b.c");
        Task task1 = taskAPI.createTask(user, "CCC", null, null, 0, LocalDateTime.now().plusDays(12));
        Task task2 = taskAPI.createTask(user, "AAA", null, null, 0, LocalDateTime.now().plusDays(10));
        Task task3 = taskAPI.createTask(user, "BBB", null, null, 0, LocalDateTime.now().plusDays(11));

        List<Task> resultList;

        throw new NotImplementedException();
    }

    @Test
    @Transactional
    public void listTasksSortByPriority() throws WrongParameterException {
        User user = userAPI.createUser("TestUser", "pwd", "Some name", "a@b.ca");
        Task task1 = taskAPI.createTask(user, "CCC", null, Priority.NORMAL, 0, LocalDateTime.now().plusDays(12));
        Task task2 = taskAPI.createTask(user, "AAA", null, Priority.HIGH, 0, LocalDateTime.now().plusDays(10));
        Task task3 = taskAPI.createTask(user, "BBB", null, Priority.LOW, 0, LocalDateTime.now().plusDays(11));
        Task task4 = taskAPI.createTask(user, "BBBC", null, Priority.LOWEST, 0, LocalDateTime.now().plusDays(11));
        Task task5 = taskAPI.createTask(user, "BBBD", null, Priority.HIGHEST, 0, LocalDateTime.now().plusDays(11));


        List<Task> resultList;

        resultList = taskAPI.listTasks(user, TaskOrdering.PRIORITY_ASC);
        for (Task task: resultList){
            System.out.println("TASK: "+task);
        }
        Assert.assertTrue(resultList.get(0).equals(task4));
        Assert.assertTrue(resultList.get(1).equals(task3));
        Assert.assertTrue(resultList.get(2).equals(task1));
        Assert.assertTrue(resultList.get(3).equals(task2));
        Assert.assertTrue(resultList.get(4).equals(task5));

        resultList = taskAPI.listTasks(user, TaskOrdering.PRIORITY_DESC);
        Assert.assertTrue(resultList.get(4).equals(task4));
        Assert.assertTrue(resultList.get(3).equals(task3));
        Assert.assertTrue(resultList.get(2).equals(task1));
        Assert.assertTrue(resultList.get(1).equals(task2));
        Assert.assertTrue(resultList.get(0).equals(task5));
    }

    @Test
    @Transactional
    public void listTasksSortByUrgency() throws WrongParameterException {
        User user = userAPI.createUser("TestUser", "pwd", "Some name", "a@b.cb");
        Task task1 = taskAPI.createTask(user, "CCC", null, null, 0, LocalDateTime.now().plusDays(12));
        Task task2 = taskAPI.createTask(user, "AAA", null, null, 0, LocalDateTime.now().plusDays(10));
        Task task3 = taskAPI.createTask(user, "BBB", null, null, 0, LocalDateTime.now().plusDays(11));


        List<Task> resultList;

        throw new NotImplementedException();
    }

    @Test
    @Transactional
    public void listTasksFilterByRole() throws WrongParameterException, NotMemberOfException {
        User user = userAPI.createUser("TestUser", "pwd", "Some name", "a@b.cb");
        Task task1 = taskAPI.createTask(user, "CCC", null, null, 0, LocalDateTime.now().plusDays(12));
        Task task2 = taskAPI.createTask(user, "AAA", null, null, 0, LocalDateTime.now().plusDays(10));
        Task task3 = taskAPI.createTask(user, "BBB", null, null, 0, LocalDateTime.now().plusDays(11));
        Task task4 = taskAPI.createTask(user, "BBB", null, null, 0, LocalDateTime.now().plusDays(11));
        Task task5 = taskAPI.createTask(user, "BBB", null, null, 0, LocalDateTime.now().plusDays(11));

        taskAPI.setTaskRole(user, task2, TaskRole.WORKER);
        taskAPI.setTaskRole(user, task4, TaskRole.WORKER);

        List<Task> resultList;
        resultList = taskAPI.listTasks(user, TaskOrdering.NONE, new TaskFilterRole(user, TaskRole.WATCHER));
        Assert.assertEquals(3, resultList.size());
        Assert.assertTrue(resultList.contains(task1));
        Assert.assertTrue(resultList.contains(task3));
        Assert.assertTrue(resultList.contains(task5));

        resultList = taskAPI.listTasks(user, TaskOrdering.NONE, new TaskFilterRole(user, TaskRole.WORKER));
        Assert.assertEquals(2, resultList.size());
        Assert.assertTrue(resultList.contains(task2));
        Assert.assertTrue(resultList.contains(task4));
    }

    @Test
    @Transactional
    public void listTasksFilterByType() throws WrongParameterException, NotMemberOfException {
        User user = userAPI.createUser("TestUser", "pwd", "Some name", "a@b.cb");
        Task task1 = taskAPI.createTask(user, "CCC", null, null, 0, LocalDateTime.now().plusDays(12));
        Task task2 = taskAPI.createTask(user, "AAA", null, null, 0, 1);
        Task task3 = taskAPI.createTask(user, "BBB", null, null, 0, LocalDateTime.now().plusDays(11));
        Task task4 = taskAPI.createTask(user, "BBB", null, null, 0, LocalDateTime.now().plusDays(11));
        Task task5 = taskAPI.createTask(user, "BBB", null, null, 0, 2);

        List<Task> resultList;
        resultList = taskAPI.listTasks(user, TaskOrdering.NONE, new TaskFilterType(DeadlineTask.class));
        Assert.assertEquals(3, resultList.size());
        Assert.assertTrue(resultList.contains(task1));
        Assert.assertTrue(resultList.contains(task3));
        Assert.assertTrue(resultList.contains(task4));

        resultList = taskAPI.listTasks(user, TaskOrdering.NONE, new TaskFilterType(GrowingTask.class));
        Assert.assertEquals(2, resultList.size());
        Assert.assertTrue(resultList.contains(task2));
        Assert.assertTrue(resultList.contains(task5));
    }





    private void sleepALittle() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}