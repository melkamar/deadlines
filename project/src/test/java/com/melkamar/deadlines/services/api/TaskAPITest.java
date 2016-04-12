package com.melkamar.deadlines.services.api;

import com.melkamar.deadlines.DeadlinesApplication;
import com.melkamar.deadlines.dao.processing.*;
import com.melkamar.deadlines.dao.task.TaskDAO;
import com.melkamar.deadlines.dao.user.UserDAO;
import com.melkamar.deadlines.exceptions.*;
import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.TaskParticipant;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.task.*;
import com.melkamar.deadlines.services.DateConvertor;
import com.melkamar.deadlines.services.helpers.TaskParticipantHelper;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 27.03.2016 14:36
 */
//@Rollback(value = false)
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DeadlinesApplication.class)
@WebAppConfiguration
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
    @Autowired
    private TaskParticipantHelper taskParticipantHelper;
    @Autowired
    private InternalAPI internalAPI;

    @Test(expected = WrongParameterException.class)
    @Transactional
    public void nullParameters() throws UserAlreadyExistsException, WrongParameterException {
        taskAPI.createTask(null, null, null, null, 0, null);
    }

    @Test(expected = WrongParameterException.class)
    @Transactional
    public void nullDeadline() throws UserAlreadyExistsException, WrongParameterException, AlreadyExistsException {
        User user = userAPI.createUser("TestUser", "pwd", "Some name", "a@b.c");
        taskAPI.createTask(user, "TestTask", "Task Description", Priority.NORMAL, 0, null);
    }

    @Test(expected = WrongParameterException.class)
    @Transactional
    public void negativeGrowspeed() throws UserAlreadyExistsException, WrongParameterException, AlreadyExistsException {
        User user = userAPI.createUser("TestUser", "pwd", "Some name", "a@b.c");
        taskAPI.createTask(user, "TestTask", "Task Description", Priority.NORMAL, 0, -10);
    }

    @Test
    @Transactional
    public void minimumInfoDeadline() throws UserAlreadyExistsException, WrongParameterException, AlreadyExistsException {
        User user = userAPI.createUser("TestUser", "pwd", "Some name", "a@b.c");
        taskAPI.createTask(user, "TestTask", null, null, 0, LocalDateTime.now().plusDays(10));
    }

    @Test
    @Transactional
    public void minimumInfoGrowing() throws UserAlreadyExistsException, WrongParameterException, AlreadyExistsException {
        User user = userAPI.createUser("TestUser", "pwd", "Some name", "a@b.c");
        taskAPI.createTask(user, "TestTask", null, null, 0, 10);
    }

    @Test
    @Transactional
    public void creatorMemberOfTask() throws UserAlreadyExistsException, WrongParameterException, AlreadyExistsException {
        User user = userAPI.createUser("TestUser", "pwd", "Some name", "a@b.c");
        Task task = taskAPI.createTask(user, "TestTask", null, null, 0, LocalDateTime.now().plusDays(10));

        Assert.assertTrue(task.getUsersOnTask().contains(user));
        Assert.assertTrue(user.getTasksOfUser().contains(task));
    }

    @Test
    @Transactional
    public void userTaskRelationPersistence() throws UserAlreadyExistsException, WrongParameterException, AlreadyExistsException {
        User user = userAPI.createUser("TestUser", "pwd", "Some name", "a@b.c");
        Task task = taskAPI.createTask(user, "TestTask", null, null, 0, LocalDateTime.now().plusDays(10));

        User retrievedUser = userDAO.findByUsername("TestUser");
        Assert.assertTrue(retrievedUser.getTasksOfUser().size() == 1);

        Task retrievedTask = retrievedUser.getTasksOfUser().iterator().next();
        Assert.assertTrue(retrievedTask.getName().equals("TestTask"));

        System.out.println("Original:  " + task);
        System.out.println("Retrieved: " + retrievedTask);
    }

    @Test
    @Transactional
    public void createGroupTasks() throws UserAlreadyExistsException, WrongParameterException, GroupPermissionException, NotMemberOfException, AlreadyExistsException {
        User user = userAPI.createUser("TestUser", "pwd", "Some name", "a@b.c");
        User userNonMember = userAPI.createUser("TestUserNonMember", "pwd", "Some name", "a@b.c");
        Group group = groupAPI.createGroup("TestGroup", user, null);

        List<Group> groupList = new ArrayList<>();
        groupList.add(group);

        Task task = taskAPI.createTask(user, "TestTask", null, null, 0, groupList, LocalDateTime.now().plusDays(10));
        Task task2 = taskAPI.createTask(user, "TestTask2", null, null, 0, groupList, LocalDateTime.now().plusDays(101));
        Task task3 = taskAPI.createTask(user, "TestTask3", null, null, 0, groupList, LocalDateTime.now().plusDays(102));
        Task task4 = taskAPI.createTask(user, "TestTask4", null, null, 0, null, LocalDateTime.now().plusDays(102));

        Assert.assertEquals(user.getTasksOfUser().size(), 4);
        Assert.assertEquals(userNonMember.getTasksOfUser().size(), 0);
        Assert.assertEquals(group.getSharedTasks().size(), 3);
    }

    @Test(expected = NotMemberOfException.class)
    @Transactional
    public void createGroupTaskByNonMember() throws UserAlreadyExistsException, WrongParameterException, GroupPermissionException, NotMemberOfException, AlreadyExistsException {
        User user = userAPI.createUser("TestUser", "pwd", "Some name", "a@b.c");
        User userNonMember = userAPI.createUser("TestUserNonMember", "pwd", "Some name", "a@b.c");
        Group group = groupAPI.createGroup("TestGroup", user, null);

        List<Group> groupList = new ArrayList<>();
        groupList.add(group);

        Task task = taskAPI.createTask(userNonMember, "TestTask", null, null, 0, groupList, LocalDateTime.now().plusDays(10));
    }

    @Test
    @Transactional
    public void createGroupTaskByMemberNotManager() throws UserAlreadyExistsException, WrongParameterException, GroupPermissionException, NotMemberOfException, AlreadyExistsException {
        User userAdmin = userAPI.createUser("TestUserd", "pwd", "Some name", "a@b.c");
        User userMember = userAPI.createUser("TestUserNonMember", "pwd", "Some name", "a@b.c");
        Group group = groupAPI.createGroup("TestGroup", userAdmin, null);

        groupAPI.addMember(userAdmin, group, userMember);

        List<Group> groupList = new ArrayList<>();
        groupList.add(group);

        expectedException.expect(GroupPermissionException.class);
        Task task = taskAPI.createTask(userMember, "TestTask", null, null, 0, groupList, LocalDateTime.now().plusDays(10));
    }

    // WORK REPORTS TESTS
    @Test(expected = WrongParameterException.class)
    @Transactional
    public void reportWorkInvalidManhours() throws UserAlreadyExistsException, WrongParameterException, NotMemberOfException, TaskPermissionException, AlreadyExistsException {
        User user = userAPI.createUser("TestUser", "pwd", "Some name", "a@b.c");
        Task task = taskAPI.createTask(user, "TestTask", null, null, 0, LocalDateTime.now().plusDays(10));

        TaskParticipant participant = user.getParticipants().iterator().next();
        participant.setRole(TaskRole.WORKER);

        taskAPI.reportWork(user, task, -1d);
    }

    @Test(expected = NotMemberOfException.class)
    @Transactional
    public void reportWorkUserNotParticipant() throws UserAlreadyExistsException, WrongParameterException, NotMemberOfException, TaskPermissionException, AlreadyExistsException {
        User user = userAPI.createUser("TestUser", "pwd", "Some name", "a@b.c");
        User nonParticipant = userAPI.createUser("NotAParticipant", "pwd", "Some name", "a@b.c");
        Task task = taskAPI.createTask(user, "TestTask", null, null, 0, LocalDateTime.now().plusDays(10));

        TaskParticipant participant = user.getParticipants().iterator().next();
        participant.setRole(TaskRole.WORKER);

        taskAPI.reportWork(nonParticipant, task, 10d);
    }

    @Test(expected = TaskPermissionException.class)
    @Transactional
    public void reportWorkUserNotWorker() throws UserAlreadyExistsException, WrongParameterException, NotMemberOfException, TaskPermissionException, AlreadyExistsException {
        User user = userAPI.createUser("TestUser", "pwd", "Some name", "a@b.c");
        Task task = taskAPI.createTask(user, "TestTask", null, null, 0, LocalDateTime.now().plusDays(10));

        TaskParticipant participant = user.getParticipants().iterator().next();

        taskAPI.reportWork(user, task, 10d);
    }

    @Test
    @Transactional
    public void reportWorkPersistence() throws UserAlreadyExistsException, WrongParameterException, NotMemberOfException, TaskPermissionException, AlreadyExistsException {
        User user = userAPI.createUser("TestUser", "pwd", "Some name", "a@b.c");
        User nonParticipant = userAPI.createUser("NotAParticipant", "pwd", "Some name", "a@b.c");
        Task task = taskAPI.createTask(user, "TestTask", null, null, 0, LocalDateTime.now().plusDays(10));
        Long taskId = task.getId();

        TaskParticipant participant = user.getParticipants().iterator().next();
        participant.setRole(TaskRole.WORKER);

        taskAPI.reportWork(user, task, 10d);
        Assert.assertTrue(task.getWorkReports().size() == 1);

        taskAPI.reportWork(user, task, 5d);
        Assert.assertTrue(task.getWorkReports().size() == 2);

        taskAPI.reportWork(user, task, 12d);
        Assert.assertTrue(task.getWorkReports().size() == 3);

        Task retrievedTask = taskDAO.findById(taskId);
        Assert.assertNotNull(retrievedTask);

        Assert.assertTrue(retrievedTask.getWorkReports().size() == 3);
        Assert.assertTrue(retrievedTask.getManhoursWorked() == 10 + 5 + 12);
    }

    @Test
    @Transactional
    public void listTasksSortByName() throws UserAlreadyExistsException, WrongParameterException, AlreadyExistsException {
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
    public void listTasksSortByDateCreated() throws UserAlreadyExistsException, WrongParameterException, AlreadyExistsException {
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
    public void listTasksSortByDeadline() throws UserAlreadyExistsException, WrongParameterException, AlreadyExistsException {
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
    public void listTasksSortByDeadlineMixedWithGrowing() throws UserAlreadyExistsException, WrongParameterException, AlreadyExistsException {
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
    public void listTasksSortByWorkedPercent() throws UserAlreadyExistsException, WrongParameterException, NotMemberOfException, TaskPermissionException, AlreadyExistsException {
        User user = userAPI.createUser("TestUser", "pwd", "Some name", "a@b.c");
        Task task1 = taskAPI.createTask(user, "CCC", null, null, 10, LocalDateTime.now().plusDays(12));
        Task task2 = taskAPI.createTask(user, "AAA", null, null, 20, LocalDateTime.now().plusDays(10));

        taskAPI.setTaskRole(user, task1, TaskRole.WORKER);
        taskAPI.setTaskRole(user, task2, TaskRole.WORKER);

        List<Task> resultList;

        taskAPI.reportWork(user, task1, 8d); // 80%
        taskAPI.reportWork(user, task2, 10d); // 50%
        taskAPI.reportWork(user, task2, 2d); // 60%

        resultList = taskAPI.listTasks(user, TaskOrdering.WORKED_PERCENT_ASC);
        Assert.assertTrue(resultList.get(0).equals(task2));
        Assert.assertTrue(resultList.get(1).equals(task1));


        resultList = taskAPI.listTasks(user, TaskOrdering.WORKED_PERCENT_DESC);
        Assert.assertTrue(resultList.get(1).equals(task2));
        Assert.assertTrue(resultList.get(0).equals(task1));


        taskAPI.reportWork(user, task2, 6d); // 90%

        resultList = taskAPI.listTasks(user, TaskOrdering.WORKED_PERCENT_ASC);
        Assert.assertTrue(resultList.get(0).equals(task1));
        Assert.assertTrue(resultList.get(1).equals(task2));


        resultList = taskAPI.listTasks(user, TaskOrdering.WORKED_PERCENT_DESC);
        Assert.assertTrue(resultList.get(1).equals(task1));
        Assert.assertTrue(resultList.get(0).equals(task2));

    }

    @Test
    @Transactional
    public void listTasksSortByPriority() throws UserAlreadyExistsException, WrongParameterException, AlreadyExistsException {
        User user = userAPI.createUser("TestUser", "pwd", "Some name", "a@b.ca");
        Task task1 = taskAPI.createTask(user, "CCC", null, Priority.NORMAL, 0, LocalDateTime.now().plusDays(12));
        Task task2 = taskAPI.createTask(user, "AAA", null, Priority.HIGH, 0, LocalDateTime.now().plusDays(10));
        Task task3 = taskAPI.createTask(user, "BBB", null, Priority.LOW, 0, LocalDateTime.now().plusDays(11));
        Task task4 = taskAPI.createTask(user, "BBBC", null, Priority.LOWEST, 0, LocalDateTime.now().plusDays(11));
        Task task5 = taskAPI.createTask(user, "BBBD", null, Priority.HIGHEST, 0, LocalDateTime.now().plusDays(11));


        List<Task> resultList;

        resultList = taskAPI.listTasks(user, TaskOrdering.PRIORITY_ASC);
        for (Task task : resultList) {
            System.out.println("TASK: " + task);
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
    public void listTasksSortByUrgency() throws UserAlreadyExistsException, WrongParameterException, AlreadyExistsException {
        User user = userAPI.createUser("TestUser", "pwd", "Some name", "a@b.cb");
        Task task1 = taskAPI.createTask(user, "CCC", null, null, 0, LocalDateTime.now().plusDays(12));
        Task task2 = taskAPI.createTask(user, "AAA", null, null, 0, LocalDateTime.now().plusDays(10));
        Task task3 = taskAPI.createTask(user, "BBB", null, null, 0, LocalDateTime.now().plusDays(11));


        List<Task> resultList = taskAPI.listTasks(user, TaskOrdering.URGENCY_DESC);
        for (Task task : resultList) {
            System.out.println("TASK: " + task);
        }
        Assert.assertTrue(resultList.get(0).equals(task2));
        Assert.assertTrue(resultList.get(1).equals(task3));
        Assert.assertTrue(resultList.get(2).equals(task1));
    }

    @Test
    @Transactional
    public void listTasksSortByUrgencyGrowing() throws UserAlreadyExistsException, WrongParameterException, AlreadyExistsException {
        User user = userAPI.createUser("TestUser", "pwd", "Some name", "a@b.cb");
        Task task1 = taskAPI.createTask(user, "CCC", null, null, 0, 10);
        Task task2 = taskAPI.createTask(user, "AAA", null, null, 0, 13);
        Task task3 = taskAPI.createTask(user, "BBB", null, null, 0, 12);

        task1.getUrgency().setLastUpdate(DateConvertor.localDateTimeToDate(LocalDateTime.now().minusHours(10)));
        task2.getUrgency().setLastUpdate(DateConvertor.localDateTimeToDate(LocalDateTime.now().minusHours(10)));
        task3.getUrgency().setLastUpdate(DateConvertor.localDateTimeToDate(LocalDateTime.now().minusHours(10)));

        internalAPI.updateAllUrgencies(true);

        List<Task> resultList = taskAPI.listTasks(user, TaskOrdering.URGENCY_DESC);
        for (Task task : resultList) {
            System.out.println("TASK: " + task);
        }
        Assert.assertTrue(resultList.get(0).equals(task1));
        Assert.assertTrue(resultList.get(1).equals(task3));
        Assert.assertTrue(resultList.get(2).equals(task2));
    }

    @Test
    @Transactional
    public void listTasksFilterByRole() throws UserAlreadyExistsException, WrongParameterException, NotMemberOfException, AlreadyExistsException {
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
    public void listTasksFilterByType() throws UserAlreadyExistsException, WrongParameterException, NotMemberOfException, AlreadyExistsException {
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

    @Test
    @Transactional
    public void listTasksFilterByStatus() throws UserAlreadyExistsException, WrongParameterException, NotMemberOfException, NotAllowedException, AlreadyExistsException {
        User user = userAPI.createUser("TestUser", "pwd", "Some name", "a@b.cb");
        Task task1 = taskAPI.createTask(user, "CCC", null, null, 0, LocalDateTime.now().plusDays(12));
        Task task2 = taskAPI.createTask(user, "AAA", null, null, 0, 1);
        Task task3 = taskAPI.createTask(user, "BBB", null, null, 0, LocalDateTime.now().plusDays(11));
        Task task4 = taskAPI.createTask(user, "BBB", null, null, 0, LocalDateTime.now().plusDays(11));
        Task task5 = taskAPI.createTask(user, "BBB", null, null, 0, 2);
        Task task6 = taskAPI.createTask(user, "BBB", null, null, 0, 2);
        Task task7 = taskAPI.createTask(user, "BBB", null, null, 0, 2);

        taskAPI.setTaskRole(user, task1, TaskRole.WORKER);
        taskAPI.setTaskRole(user, task2, TaskRole.WORKER);
        taskAPI.setTaskRole(user, task3, TaskRole.WORKER);
        taskAPI.setTaskRole(user, task4, TaskRole.WORKER);
        taskAPI.setTaskRole(user, task5, TaskRole.WORKER);
        taskAPI.setTaskRole(user, task6, TaskRole.WORKER);
        taskAPI.setTaskRole(user, task7, TaskRole.WORKER);

        taskAPI.setTaskStatus(user, task1, TaskStatus.IN_PROGRESS);
        taskAPI.setTaskStatus(user, task2, TaskStatus.CANCELLED);
        taskAPI.setTaskStatus(user, task3, TaskStatus.IN_PROGRESS);
        taskAPI.setTaskStatus(user, task4, TaskStatus.COMPLETED);
//        taskAPI.setTaskStatus(user, task5, TaskStatus.OPEN);
//        taskAPI.setTaskStatus(user, task6, TaskStatus.OPEN);
        taskAPI.setTaskStatus(user, task7, TaskStatus.COMPLETED);


        List<Task> resultList;
        resultList = taskAPI.listTasks(user, TaskOrdering.NONE, new TaskFilterStatus(TaskStatus.OPEN));
        Assert.assertEquals(2, resultList.size());
        Assert.assertTrue(resultList.contains(task5));
        Assert.assertTrue(resultList.contains(task6));

        resultList = taskAPI.listTasks(user, TaskOrdering.NONE, new TaskFilterStatus(TaskStatus.IN_PROGRESS));
        Assert.assertEquals(2, resultList.size());
        Assert.assertTrue(resultList.contains(task1));
        Assert.assertTrue(resultList.contains(task3));

        resultList = taskAPI.listTasks(user, TaskOrdering.NONE, new TaskFilterStatus(TaskStatus.CANCELLED));
        Assert.assertEquals(1, resultList.size());
        Assert.assertTrue(resultList.contains(task2));

        resultList = taskAPI.listTasks(user, TaskOrdering.NONE, new TaskFilterStatus(TaskStatus.COMPLETED));
        Assert.assertEquals(2, resultList.size());
        Assert.assertTrue(resultList.contains(task4));
        Assert.assertTrue(resultList.contains(task7));
    }

    @Test
    @Transactional
    public void listTasksFilterByPriority() throws UserAlreadyExistsException, WrongParameterException, AlreadyExistsException {
        User user = userAPI.createUser("TestUser", "pwd", "Some name", "a@b.ca");
        Task task1 = taskAPI.createTask(user, "CCC", null, Priority.NORMAL, 0, LocalDateTime.now().plusDays(12));
        Task task2 = taskAPI.createTask(user, "AAA", null, Priority.HIGH, 0, LocalDateTime.now().plusDays(10));
        Task task3 = taskAPI.createTask(user, "BBB", null, Priority.LOW, 0, LocalDateTime.now().plusDays(11));
        Task task4 = taskAPI.createTask(user, "BBBC", null, Priority.LOWEST, 0, LocalDateTime.now().plusDays(11));
        Task task5 = taskAPI.createTask(user, "BBBD", null, Priority.HIGHEST, 0, LocalDateTime.now().plusDays(11));
        Task task6 = taskAPI.createTask(user, "BBBD", null, Priority.HIGHEST, 0, LocalDateTime.now().plusDays(11));

        List<Task> resultList;
        resultList = taskAPI.listTasks(user, TaskOrdering.NONE, new TaskFilterPriority(Priority.LOWEST));
        Assert.assertTrue(resultList.size() == 1);
        Assert.assertTrue(resultList.contains(task4));

        resultList = taskAPI.listTasks(user, TaskOrdering.NONE, new TaskFilterPriority(Priority.LOW));
        Assert.assertTrue(resultList.size() == 1);
        Assert.assertTrue(resultList.contains(task3));

        resultList = taskAPI.listTasks(user, TaskOrdering.NONE, new TaskFilterPriority(Priority.NORMAL));
        Assert.assertTrue(resultList.size() == 1);
        Assert.assertTrue(resultList.contains(task1));

        resultList = taskAPI.listTasks(user, TaskOrdering.NONE, new TaskFilterPriority(Priority.HIGH));
        Assert.assertTrue(resultList.size() == 1);
        Assert.assertTrue(resultList.contains(task2));

        resultList = taskAPI.listTasks(user, TaskOrdering.NONE, new TaskFilterPriority(Priority.HIGHEST));
        Assert.assertTrue(resultList.size() == 2);
        Assert.assertTrue(resultList.contains(task5));
        Assert.assertTrue(resultList.contains(task6));

        // Multiple priorities
        resultList = taskAPI.listTasks(user, TaskOrdering.NONE, new TaskFilterPriority(Priority.LOWEST, Priority.LOW));
        Assert.assertTrue(resultList.size() == 2);
        Assert.assertTrue(resultList.contains(task3));
        Assert.assertTrue(resultList.contains(task4));

        resultList = taskAPI.listTasks(user, TaskOrdering.NONE, new TaskFilterPriority(Priority.LOWEST,
                Priority.LOW, Priority.HIGHEST));
        Assert.assertTrue(resultList.size() == 4);
        Assert.assertTrue(resultList.contains(task3));
        Assert.assertTrue(resultList.contains(task4));
        Assert.assertTrue(resultList.contains(task5));
        Assert.assertTrue(resultList.contains(task6));
    }

    @Test
    @Transactional
    public void setTaskRoleByGroupManager() throws UserAlreadyExistsException, WrongParameterException, GroupPermissionException, NotMemberOfException, AlreadyExistsException, NotAllowedException {
        User userMember = userAPI.createUser("Member", "password", "John Doe", "a@b.c");
        User userMember2 = userAPI.createUser("Member2", "password", "John Doe", "a@b.c");
        User userAdmin = userAPI.createUser("Admin", "password", "John Doe", "c@b.c");
        Group group = groupAPI.createGroup("Groupname", userAdmin, "Random description");

        Task task = taskAPI.createTask(userMember, "TestTask", null, null, 0, LocalDateTime.now().plusDays(10));
        Task task2 = taskAPI.createTask(userMember, "TestTask2", null, null, 0, LocalDateTime.now().plusDays(101));
        Task task3 = taskAPI.createTask(userAdmin, "TestTask3", null, null, 0, LocalDateTime.now().plusDays(102));

        groupAPI.addTask(userAdmin, group, task);
        groupAPI.addTask(userAdmin, group, task2);
        groupAPI.addTask(userAdmin, group, task3);

        groupAPI.addMember(userAdmin, group, userMember);

        Assert.assertTrue(taskAPI.getTaskParticipant(userMember, task).getRole() == TaskRole.WATCHER);
        Assert.assertTrue(taskAPI.getTaskParticipant(userMember, task2).getRole() == TaskRole.WATCHER);

        taskAPI.setTaskRole(userMember, task, TaskRole.WORKER, userAdmin, group);

        Assert.assertTrue(taskAPI.getTaskParticipant(userMember, task).getRole() == TaskRole.WORKER);
        Assert.assertTrue(taskAPI.getTaskParticipant(userMember, task2).getRole() == TaskRole.WATCHER);
    }

    @Test(expected = NotAllowedException.class)
    @Transactional
    public void setTaskRoleByGroupManagerFail() throws UserAlreadyExistsException, WrongParameterException, GroupPermissionException, NotMemberOfException, AlreadyExistsException, NotAllowedException {
        User userMember = userAPI.createUser("Member", "password", "John Doe", "a@b.c");
        User userAdmin = userAPI.createUser("Admin", "password", "John Doe", "c@b.c");
        Group group = groupAPI.createGroup("Groupname", userAdmin, "Random description");

        Task task = taskAPI.createTask(userMember, "TestTask", null, null, 0, LocalDateTime.now().plusDays(10));
        Task task2 = taskAPI.createTask(userMember, "TestTask2", null, null, 0, LocalDateTime.now().plusDays(101));
        Task task3 = taskAPI.createTask(userAdmin, "TestTask3", null, null, 0, LocalDateTime.now().plusDays(102));

        groupAPI.addTask(userAdmin, group, task);
        groupAPI.addTask(userAdmin, group, task2);

        taskParticipantHelper.editOrCreateTaskParticipant(userMember, task3, TaskRole.WATCHER, null, false);

        groupAPI.addMember(userAdmin, group, userMember);

        Assert.assertTrue(taskAPI.getTaskParticipant(userMember, task).getRole() == TaskRole.WATCHER);
        Assert.assertTrue(taskAPI.getTaskParticipant(userMember, task2).getRole() == TaskRole.WATCHER);

        taskAPI.setTaskRole(userMember, task3, TaskRole.WORKER, userAdmin, group);
    }

    @Test
    @Transactional
    public void editTask() throws UserAlreadyExistsException, WrongParameterException, TaskPermissionException, NotMemberOfException, NotAllowedException, AlreadyExistsException {
        User userWorker = userAPI.createUser("Member", "password", "John Doe", "a@b.c");

        LocalDateTime deadlineDateTime = LocalDateTime.of(1999, 6, 2, 12, 45, 50);
        LocalDateTime newDeadlineDateTime = LocalDateTime.of(2001, 7, 3, 22, 45, 50);

        Task task1 = taskAPI.createTask(userWorker, "TestTask", "Desc1", Priority.LOWEST, 0, deadlineDateTime);

        taskAPI.setTaskRole(userWorker, task1, TaskRole.WORKER);

        Assert.assertEquals(Priority.LOWEST, task1.getPriority());
        taskAPI.editTask(userWorker, task1, null, null, null, Priority.HIGHEST);
        Assert.assertEquals(Priority.HIGHEST, task1.getPriority());

        Assert.assertEquals("Desc1", task1.getDescription());
        taskAPI.editTask(userWorker, task1, "NewDesc", null, null, null);
        Assert.assertEquals("NewDesc", task1.getDescription());

        Assert.assertEquals(DateConvertor.dateToLocalDateTime(((DeadlineTask) task1).getDeadline()), deadlineDateTime);
        taskAPI.editTask(userWorker, task1, null, newDeadlineDateTime, null, null);
        Assert.assertEquals(DateConvertor.dateToLocalDateTime(((DeadlineTask) task1).getDeadline()), newDeadlineDateTime);

        Assert.assertTrue(task1.getWorkEstimate() == 0);
        taskAPI.editTask(userWorker, task1, null, null, 10.5, null);
        Assert.assertTrue(task1.getWorkEstimate() == 10.5);

        taskAPI.setTaskRole(userWorker, task1, TaskRole.WATCHER);
        expectedException.expect(TaskPermissionException.class);
        taskAPI.editTask(userWorker, task1, null, null, 11d, null);
    }


    @Test
    @Transactional
    public void listGroupTasksSortByName() throws UserAlreadyExistsException, WrongParameterException, GroupPermissionException, NotMemberOfException, AlreadyExistsException {
        User user = userAPI.createUser("TestUser", "pwd", "Some name", "a@b.c");
        Group group1 = groupAPI.createGroup("Group1", user, null);
        Group group2 = groupAPI.createGroup("Group2", user, null);

        Task task1 = taskAPI.createTask(user, "CCC", null, null, 0, LocalDateTime.now().plusDays(10));
        Task task2 = taskAPI.createTask(user, "AAA", null, null, 0, LocalDateTime.now().plusDays(11));
        Task task3 = taskAPI.createTask(user, "BBB", null, null, 0, LocalDateTime.now().plusDays(11));

        groupAPI.addTask(user, group1, task1);
        groupAPI.addTask(user, group1, task2);
        groupAPI.addTask(user, group2, task3);


        List<Task> resultList;

        resultList = taskAPI.listTasks(group1, TaskOrdering.NAME_ASC);
        Assert.assertTrue(resultList.get(0).equals(task2));
        Assert.assertTrue(resultList.get(1).equals(task1));

        resultList = taskAPI.listTasks(group2, TaskOrdering.NAME_ASC);
        Assert.assertTrue(resultList.get(0).equals(task3));


        resultList = taskAPI.listTasks(group1, TaskOrdering.NAME_DESC);
        Assert.assertTrue(resultList.get(1).equals(task2));
        Assert.assertTrue(resultList.get(0).equals(task1));

        resultList = taskAPI.listTasks(group2, TaskOrdering.NAME_DESC);
        Assert.assertTrue(resultList.get(0).equals(task3));
    }

    @Test
    @Transactional
    public void listGroupTasksSortByDateCreated() throws UserAlreadyExistsException, WrongParameterException, GroupPermissionException, NotMemberOfException, AlreadyExistsException {
        User user = userAPI.createUser("TestUser", "pwd", "Some name", "a@b.c");
        Group group1 = groupAPI.createGroup("Group1", user, null);
        Group group2 = groupAPI.createGroup("Group2", user, null);

        Task task1 = taskAPI.createTask(user, "CCC", null, null, 0, LocalDateTime.now().plusDays(10));
        sleepALittle();
        Task task2 = taskAPI.createTask(user, "AAA", null, null, 0, LocalDateTime.now().plusDays(11));
        sleepALittle();
        Task task3 = taskAPI.createTask(user, "BBB", null, null, 0, LocalDateTime.now().plusDays(1));

        groupAPI.addTask(user, group1, task1);
        groupAPI.addTask(user, group2, task2);
        groupAPI.addTask(user, group2, task3);


        List<Task> resultList;

        resultList = taskAPI.listTasks(group1, TaskOrdering.DATE_START_ASC);
        Assert.assertTrue(resultList.get(0).equals(task1));

        resultList = taskAPI.listTasks(group2, TaskOrdering.DATE_START_ASC);
        Assert.assertTrue(resultList.get(0).equals(task2));
        Assert.assertTrue(resultList.get(1).equals(task3));


        resultList = taskAPI.listTasks(group1, TaskOrdering.DATE_START_DESC);
        Assert.assertTrue(resultList.get(0).equals(task1));

        resultList = taskAPI.listTasks(group2, TaskOrdering.DATE_START_DESC);
        Assert.assertTrue(resultList.get(1).equals(task2));
        Assert.assertTrue(resultList.get(0).equals(task3));
    }

    @Transactional
    @Test
    public void resetUrgency() throws AlreadyExistsException, WrongParameterException, UserAlreadyExistsException, TaskPermissionException, NotMemberOfException, NotAllowedException {
        User user = userAPI.createUser("TestUser", "pwd", "Some name", "a@b.c");

        Task task1 = taskAPI.createTask(user, "CCC", null, null, 0, 15);
        Task task2 = taskAPI.createTask(user, "AAA", null, null, 0, 20);
        Task task3 = taskAPI.createTask(user, "BBB", null, null, 0, 30);

        taskAPI.setTaskRole(user, task1, TaskRole.WORKER);
        taskAPI.setTaskRole(user, task2, TaskRole.WORKER);
        taskAPI.setTaskRole(user, task3, TaskRole.WORKER);

        task1.getUrgency().setLastUpdate(DateConvertor.localDateTimeToDate(LocalDateTime.now().minusHours(10)));
        task2.getUrgency().setLastUpdate(DateConvertor.localDateTimeToDate(LocalDateTime.now().minusHours(10)));
        task3.getUrgency().setLastUpdate(DateConvertor.localDateTimeToDate(LocalDateTime.now().minusHours(10)));

        internalAPI.updateAllUrgencies();

        Assert.assertTrue(task1.getUrgency().getValue() > 0);
        Assert.assertTrue(task2.getUrgency().getValue() > 0);
        Assert.assertTrue(task3.getUrgency().getValue() > 0);

        taskAPI.resetUrgency(user, task1);
        Assert.assertEquals(0, task1.getUrgency().getValue(), 0.1);
        Assert.assertTrue(task2.getUrgency().getValue() > 0);
        Assert.assertTrue(task3.getUrgency().getValue() > 0);

        taskAPI.resetUrgency(user, task2);
        Assert.assertEquals(0, task1.getUrgency().getValue(), 0.1);
        Assert.assertEquals(0, task2.getUrgency().getValue(), 0.1);
        Assert.assertTrue(task3.getUrgency().getValue() > 0);

        taskAPI.resetUrgency(user, task3);
        Assert.assertEquals(0, task1.getUrgency().getValue(), 0.1);
        Assert.assertEquals(0, task2.getUrgency().getValue(), 0.1);
        Assert.assertEquals(0, task3.getUrgency().getValue(), 0.1);
    }

    @Transactional
    @Test(expected = NotAllowedException.class)
    public void resetUrgencyNotGrowingTask() throws Exception {
        User user = userAPI.createUser("TestUser", "pwd", "Some name", "a@b.c");

        Task task1 = taskAPI.createTask(user, "CCC", null, null, 0, LocalDateTime.now().plusDays(1));

        taskAPI.setTaskRole(user, task1, TaskRole.WORKER);

        taskAPI.resetUrgency(user, task1);
    }

    private void sleepALittle() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}