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
import com.melkamar.deadlines.services.helpers.TaskParticipantHelper;
import com.melkamar.deadlines.utils.DateConvertor;
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
 * @author Martin Melka
 */
//@Rollback(value = false)
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DeadlinesApplication.class)
@WebAppConfiguration
public class TaskApiTest {
    @Autowired
    private TaskApi taskApi;
    @Autowired
    private UserApi userApi;

    @Autowired
    private UserDAO userDAO;
    @Autowired
    private TaskDAO taskDAO;
    @Autowired
    private GroupApi groupApi;


    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @Autowired
    private TaskParticipantHelper taskParticipantHelper;
    @Autowired
    private InternalApi internalApi;

    @Test(expected = WrongParameterException.class)
    @Transactional
    public void nullParameters() throws AlreadyExistsException, WrongParameterException {
        taskApi.createTask(null, null, null, null, 0, null);
    }

    @Test(expected = WrongParameterException.class)
    @Transactional
    public void nullDeadline() throws AlreadyExistsException, WrongParameterException, AlreadyExistsException {
        User user = userApi.createUser("TestUser", "pwd", "Some name", "a@b.c");
        taskApi.createTask(user, "TestTask", "Task Description", Priority.NORMAL, 0, null);
    }

    @Test(expected = WrongParameterException.class)
    @Transactional
    public void negativeGrowspeed() throws AlreadyExistsException, WrongParameterException, AlreadyExistsException {
        User user = userApi.createUser("TestUser", "pwd", "Some name", "a@b.c");
        taskApi.createTask(user, "TestTask", "Task Description", Priority.NORMAL, 0, -10);
    }

    @Test
    @Transactional
    public void minimumInfoDeadline() throws AlreadyExistsException, WrongParameterException, AlreadyExistsException {
        User user = userApi.createUser("TestUser", "pwd", "Some name", "a@b.c");
        taskApi.createTask(user, "TestTask", null, null, 0, LocalDateTime.now().plusDays(10));
    }

    @Test
    @Transactional
    public void minimumInfoGrowing() throws AlreadyExistsException, WrongParameterException, AlreadyExistsException {
        User user = userApi.createUser("TestUser", "pwd", "Some name", "a@b.c");
        taskApi.createTask(user, "TestTask", null, null, 0, 10);
    }

    @Test
    @Transactional
    public void creatorMemberOfTask() throws AlreadyExistsException, WrongParameterException, AlreadyExistsException {
        User user = userApi.createUser("TestUser", "pwd", "Some name", "a@b.c");
        Task task = taskApi.createTask(user, "TestTask", null, null, 0, LocalDateTime.now().plusDays(10));

        Assert.assertTrue(task.getUsersOnTask().contains(user));
        Assert.assertTrue(user.getTasksOfUser().contains(task));
    }

    @Test
    @Transactional
    public void userTaskRelationPersistence() throws AlreadyExistsException, WrongParameterException, AlreadyExistsException {
        User user = userApi.createUser("TestUser", "pwd", "Some name", "a@b.c");
        Task task = taskApi.createTask(user, "TestTask", null, null, 0, LocalDateTime.now().plusDays(10));

        User retrievedUser = userDAO.findByUsername("TestUser");
        Assert.assertTrue(retrievedUser.getTasksOfUser().size() == 1);

        Task retrievedTask = retrievedUser.getTasksOfUser().iterator().next();
        Assert.assertTrue(retrievedTask.getName().equals("TestTask"));

        System.out.println("Original:  " + task);
        System.out.println("Retrieved: " + retrievedTask);
    }

    @Test
    @Transactional
    public void createGroupTasks() throws AlreadyExistsException, WrongParameterException, GroupPermissionException, NotMemberOfException, AlreadyExistsException {
        User user = userApi.createUser("TestUser", "pwd", "Some name", "a@b.c");
        User userNonMember = userApi.createUser("TestUserNonMember", "pwd", "Some name", "a@b.c");
        Group group = groupApi.createGroup("TestGroup", user, null);

        List<Group> groupList = new ArrayList<>();
        groupList.add(group);

        Task task = taskApi.createTask(user, "TestTask", null, null, 0, groupList, LocalDateTime.now().plusDays(10));
        Task task2 = taskApi.createTask(user, "TestTask2", null, null, 0, groupList, LocalDateTime.now().plusDays(101));
        Task task3 = taskApi.createTask(user, "TestTask3", null, null, 0, groupList, LocalDateTime.now().plusDays(102));
        Task task4 = taskApi.createTask(user, "TestTask4", null, null, 0, null, LocalDateTime.now().plusDays(102));

        Assert.assertEquals(user.getTasksOfUser().size(), 4);
        Assert.assertEquals(userNonMember.getTasksOfUser().size(), 0);
        Assert.assertEquals(group.getSharedTasks().size(), 3);
    }

    @Test(expected = NotMemberOfException.class)
    @Transactional
    public void createGroupTaskByNonMember() throws AlreadyExistsException, WrongParameterException, GroupPermissionException, NotMemberOfException, AlreadyExistsException {
        User user = userApi.createUser("TestUser", "pwd", "Some name", "a@b.c");
        User userNonMember = userApi.createUser("TestUserNonMember", "pwd", "Some name", "a@b.c");
        Group group = groupApi.createGroup("TestGroup", user, null);

        List<Group> groupList = new ArrayList<>();
        groupList.add(group);

        Task task = taskApi.createTask(userNonMember, "TestTask", null, null, 0, groupList, LocalDateTime.now().plusDays(10));
    }

    @Test
    @Transactional
    public void createGroupTaskByMemberNotManager() throws AlreadyExistsException, WrongParameterException, GroupPermissionException, NotMemberOfException, AlreadyExistsException {
        User userAdmin = userApi.createUser("TestUserd", "pwd", "Some name", "a@b.c");
        User userMember = userApi.createUser("TestUserNonMember", "pwd", "Some name", "a@b.c");
        Group group = groupApi.createGroup("TestGroup", userAdmin, null);

        groupApi.addMember(userAdmin, group, userMember);

        List<Group> groupList = new ArrayList<>();
        groupList.add(group);

        expectedException.expect(GroupPermissionException.class);
        Task task = taskApi.createTask(userMember, "TestTask", null, null, 0, groupList, LocalDateTime.now().plusDays(10));
    }

    // WORK REPORTS TESTS
    @Test(expected = WrongParameterException.class)
    @Transactional
    public void reportWorkInvalidManhours() throws AlreadyExistsException, WrongParameterException, NotMemberOfException, TaskPermissionException, AlreadyExistsException {
        User user = userApi.createUser("TestUser", "pwd", "Some name", "a@b.c");
        Task task = taskApi.createTask(user, "TestTask", null, null, 0, LocalDateTime.now().plusDays(10));

        TaskParticipant participant = user.getParticipants().iterator().next();
        participant.setRole(TaskRole.WORKER);

        taskApi.reportWork(user, task, -1d);
    }

    @Test(expected = NotMemberOfException.class)
    @Transactional
    public void reportWorkUserNotParticipant() throws AlreadyExistsException, WrongParameterException, NotMemberOfException, TaskPermissionException, AlreadyExistsException {
        User user = userApi.createUser("TestUser", "pwd", "Some name", "a@b.c");
        User nonParticipant = userApi.createUser("NotAParticipant", "pwd", "Some name", "a@b.c");
        Task task = taskApi.createTask(user, "TestTask", null, null, 0, LocalDateTime.now().plusDays(10));

        TaskParticipant participant = user.getParticipants().iterator().next();
        participant.setRole(TaskRole.WORKER);

        taskApi.reportWork(nonParticipant, task, 10d);
    }

    @Test(expected = TaskPermissionException.class)
    @Transactional
    public void reportWorkUserNotWorker() throws AlreadyExistsException, WrongParameterException, NotMemberOfException, TaskPermissionException, AlreadyExistsException {
        User user = userApi.createUser("TestUser", "pwd", "Some name", "a@b.c");
        Task task = taskApi.createTask(user, "TestTask", null, null, 0, LocalDateTime.now().plusDays(10));

        TaskParticipant participant = user.getParticipants().iterator().next();

        taskApi.reportWork(user, task, 10d);
    }

    @Test
    @Transactional
    public void reportWorkPersistence() throws AlreadyExistsException, WrongParameterException, NotMemberOfException, TaskPermissionException, AlreadyExistsException {
        User user = userApi.createUser("TestUser", "pwd", "Some name", "a@b.c");
        User nonParticipant = userApi.createUser("NotAParticipant", "pwd", "Some name", "a@b.c");
        Task task = taskApi.createTask(user, "TestTask", null, null, 0, LocalDateTime.now().plusDays(10));
        Long taskId = task.getId();

        TaskParticipant participant = user.getParticipants().iterator().next();
        participant.setRole(TaskRole.WORKER);

        taskApi.reportWork(user, task, 10d);
        Assert.assertTrue(task.getWorkReports().size() == 1);

        taskApi.reportWork(user, task, 5d);
        Assert.assertTrue(task.getWorkReports().size() == 2);

        taskApi.reportWork(user, task, 12d);
        Assert.assertTrue(task.getWorkReports().size() == 3);

        Task retrievedTask = taskDAO.findById(taskId);
        Assert.assertNotNull(retrievedTask);

        Assert.assertTrue(retrievedTask.getWorkReports().size() == 3);
        Assert.assertTrue(retrievedTask.getManhoursWorked() == 10 + 5 + 12);
    }

    @Test
    @Transactional
    public void listTasksSortByName() throws AlreadyExistsException, WrongParameterException, AlreadyExistsException {
        User user = userApi.createUser("TestUser", "pwd", "Some name", "a@b.c");
        Task task1 = taskApi.createTask(user, "CCC", null, null, 0, LocalDateTime.now().plusDays(10));
        Task task2 = taskApi.createTask(user, "AAA", null, null, 0, LocalDateTime.now().plusDays(11));
        Task task3 = taskApi.createTask(user, "BBB", null, null, 0, LocalDateTime.now().plusDays(11));


        List<Task> resultList;

        resultList = taskApi.listTasks(user, TaskOrdering.NAME_ASC);
        Assert.assertTrue(resultList.get(0).equals(task2));
        Assert.assertTrue(resultList.get(1).equals(task3));
        Assert.assertTrue(resultList.get(2).equals(task1));

        resultList = taskApi.listTasks(user, TaskOrdering.NAME_DESC);
        Assert.assertTrue(resultList.get(2).equals(task2));
        Assert.assertTrue(resultList.get(1).equals(task3));
        Assert.assertTrue(resultList.get(0).equals(task1));
    }

    @Test
    @Transactional
    public void listTasksSortByDateCreated() throws AlreadyExistsException, WrongParameterException, AlreadyExistsException {
        User user = userApi.createUser("TestUser", "pwd", "Some name", "a@b.c");
        Task task1 = taskApi.createTask(user, "CCC", null, null, 0, LocalDateTime.now().plusDays(10));
        sleepALittle();
        Task task2 = taskApi.createTask(user, "AAA", null, null, 0, LocalDateTime.now().plusDays(11));
        sleepALittle();
        Task task3 = taskApi.createTask(user, "BBB", null, null, 0, LocalDateTime.now().plusDays(12));
        sleepALittle();


        List<Task> resultList;

        resultList = taskApi.listTasks(user, TaskOrdering.DATE_START_ASC);
        System.out.println("-------------");
        for (Task task: resultList) System.out.println(task);
        System.out.println("-------------");
        Assert.assertTrue(resultList.get(0).equals(task1));
        Assert.assertTrue(resultList.get(1).equals(task2));
        Assert.assertTrue(resultList.get(2).equals(task3));

        resultList = taskApi.listTasks(user, TaskOrdering.DATE_START_DESC);
        System.out.println("-------------");
        for (Task task: resultList) System.out.println(task);
        System.out.println("-------------");
        Assert.assertTrue(resultList.get(2).equals(task1));
        Assert.assertTrue(resultList.get(1).equals(task2));
        Assert.assertTrue(resultList.get(0).equals(task3));
    }

    @Test
    @Transactional
    public void listTasksSortByDeadline() throws AlreadyExistsException, WrongParameterException, AlreadyExistsException {
        User user = userApi.createUser("TestUser", "pwd", "Some name", "a@b.c");
        Task task1 = taskApi.createTask(user, "CCC", null, null, 0, LocalDateTime.now().plusDays(12));
        Task task2 = taskApi.createTask(user, "AAA", null, null, 0, LocalDateTime.now().plusDays(10));
        Task task3 = taskApi.createTask(user, "BBB", null, null, 0, LocalDateTime.now().plusDays(11));

        Assert.assertNotNull(((DeadlineTask) task1).getDeadline());
        Assert.assertNotNull(((DeadlineTask) task2).getDeadline());
        Assert.assertNotNull(((DeadlineTask) task3).getDeadline());

        List<Task> resultList;

        resultList = taskApi.listTasks(user, TaskOrdering.DATE_DEADLINE_ASC);
        Assert.assertTrue(resultList.get(0).equals(task2));
        Assert.assertTrue(resultList.get(1).equals(task3));
        Assert.assertTrue(resultList.get(2).equals(task1));

        resultList = taskApi.listTasks(user, TaskOrdering.DATE_DEADLINE_DESC);
        Assert.assertTrue(resultList.get(2).equals(task2));
        Assert.assertTrue(resultList.get(1).equals(task3));
        Assert.assertTrue(resultList.get(0).equals(task1));
    }

    @Test
    @Transactional
    public void listTasksSortByDeadlineMixedWithGrowing() throws AlreadyExistsException, WrongParameterException, AlreadyExistsException {
        User user = userApi.createUser("TestUser", "pwd", "Some name", "a@b.c");
        Task task1 = taskApi.createTask(user, "CCC", null, null, 0, LocalDateTime.now().plusDays(12));
        Task task5 = taskApi.createTask(user, "BBB", null, null, 0, 20);
        Task task2 = taskApi.createTask(user, "AAA", null, null, 0, LocalDateTime.now().plusDays(10));
        Task task3 = taskApi.createTask(user, "BBB", null, null, 0, LocalDateTime.now().plusDays(11));
        Task task4 = taskApi.createTask(user, "BBB", null, null, 0, 10);

        Assert.assertNotNull(((DeadlineTask) task1).getDeadline());
        Assert.assertNotNull(((DeadlineTask) task2).getDeadline());
        Assert.assertNotNull(((DeadlineTask) task3).getDeadline());

        List<Task> resultList;
        List<Task> nonSorted = new ArrayList<>();


        resultList = taskApi.listTasks(user, TaskOrdering.DATE_DEADLINE_ASC);
        nonSorted.clear();
        nonSorted.add(task4);
        nonSorted.add(task5);

        Assert.assertTrue(resultList.get(0).equals(task2));
        Assert.assertTrue(resultList.get(1).equals(task3));
        Assert.assertTrue(resultList.get(2).equals(task1));
        Assert.assertTrue(nonSorted.contains(resultList.get(3)));
        Assert.assertTrue(nonSorted.contains(resultList.get(4)));


        resultList = taskApi.listTasks(user, TaskOrdering.DATE_DEADLINE_DESC);
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
    public void listTasksSortByWorkedPercent() throws AlreadyExistsException, WrongParameterException, NotMemberOfException, TaskPermissionException, AlreadyExistsException {
        User user = userApi.createUser("TestUser", "pwd", "Some name", "a@b.c");
        Task task1 = taskApi.createTask(user, "CCC", null, null, 10, LocalDateTime.now().plusDays(12));
        Task task2 = taskApi.createTask(user, "AAA", null, null, 20, LocalDateTime.now().plusDays(10));

        taskApi.setTaskRole(user, task1, TaskRole.WORKER);
        taskApi.setTaskRole(user, task2, TaskRole.WORKER);

        List<Task> resultList;

        taskApi.reportWork(user, task1, 8d); // 80%
        taskApi.reportWork(user, task2, 10d); // 50%
        taskApi.reportWork(user, task2, 2d); // 60%

        resultList = taskApi.listTasks(user, TaskOrdering.WORKED_PERCENT_ASC);
        Assert.assertTrue(resultList.get(0).equals(task2));
        Assert.assertTrue(resultList.get(1).equals(task1));


        resultList = taskApi.listTasks(user, TaskOrdering.WORKED_PERCENT_DESC);
        Assert.assertTrue(resultList.get(1).equals(task2));
        Assert.assertTrue(resultList.get(0).equals(task1));


        taskApi.reportWork(user, task2, 6d); // 90%

        resultList = taskApi.listTasks(user, TaskOrdering.WORKED_PERCENT_ASC);
        Assert.assertTrue(resultList.get(0).equals(task1));
        Assert.assertTrue(resultList.get(1).equals(task2));


        resultList = taskApi.listTasks(user, TaskOrdering.WORKED_PERCENT_DESC);
        Assert.assertTrue(resultList.get(1).equals(task1));
        Assert.assertTrue(resultList.get(0).equals(task2));

    }

    @Test
    @Transactional
    public void listTasksSortByPriority() throws AlreadyExistsException, WrongParameterException, AlreadyExistsException {
        User user = userApi.createUser("TestUser", "pwd", "Some name", "a@b.ca");
        Task task1 = taskApi.createTask(user, "CCC", null, Priority.NORMAL, 0, LocalDateTime.now().plusDays(12));
        Task task2 = taskApi.createTask(user, "AAA", null, Priority.HIGH, 0, LocalDateTime.now().plusDays(10));
        Task task3 = taskApi.createTask(user, "BBB", null, Priority.LOW, 0, LocalDateTime.now().plusDays(11));
        Task task4 = taskApi.createTask(user, "BBBC", null, Priority.LOWEST, 0, LocalDateTime.now().plusDays(11));
        Task task5 = taskApi.createTask(user, "BBBD", null, Priority.HIGHEST, 0, LocalDateTime.now().plusDays(11));


        List<Task> resultList;

        resultList = taskApi.listTasks(user, TaskOrdering.PRIORITY_ASC);
        for (Task task : resultList) {
            System.out.println("TASK: " + task);
        }
        Assert.assertTrue(resultList.get(0).equals(task4));
        Assert.assertTrue(resultList.get(1).equals(task3));
        Assert.assertTrue(resultList.get(2).equals(task1));
        Assert.assertTrue(resultList.get(3).equals(task2));
        Assert.assertTrue(resultList.get(4).equals(task5));

        resultList = taskApi.listTasks(user, TaskOrdering.PRIORITY_DESC);
        Assert.assertTrue(resultList.get(4).equals(task4));
        Assert.assertTrue(resultList.get(3).equals(task3));
        Assert.assertTrue(resultList.get(2).equals(task1));
        Assert.assertTrue(resultList.get(1).equals(task2));
        Assert.assertTrue(resultList.get(0).equals(task5));
    }

    @Test
    @Transactional
    public void listTasksSortByUrgency() throws AlreadyExistsException, WrongParameterException, AlreadyExistsException {
        User user = userApi.createUser("TestUser", "pwd", "Some name", "a@b.cb");
        Task task1 = taskApi.createTask(user, "CCC", null, null, 0, LocalDateTime.now().plusDays(12));
        Task task2 = taskApi.createTask(user, "AAA", null, null, 0, LocalDateTime.now().plusDays(10));
        Task task3 = taskApi.createTask(user, "BBB", null, null, 0, LocalDateTime.now().plusDays(11));


        List<Task> resultList = taskApi.listTasks(user, TaskOrdering.URGENCY_DESC);
        for (Task task : resultList) {
            System.out.println("TASK: " + task);
        }
        Assert.assertTrue(resultList.get(0).equals(task2));
        Assert.assertTrue(resultList.get(1).equals(task3));
        Assert.assertTrue(resultList.get(2).equals(task1));
    }

    @Test
    @Transactional
    public void listTasksSortByUrgencyGrowing() throws AlreadyExistsException, WrongParameterException, AlreadyExistsException {
        User user = userApi.createUser("TestUser", "pwd", "Some name", "a@b.cb");
        Task task1 = taskApi.createTask(user, "CCC", null, null, 0, 10);
        Task task2 = taskApi.createTask(user, "AAA", null, null, 0, 13);
        Task task3 = taskApi.createTask(user, "BBB", null, null, 0, 12);

        task1.getUrgency().setLastUpdate(DateConvertor.localDateTimeToDate(LocalDateTime.now().minusHours(10)));
        task2.getUrgency().setLastUpdate(DateConvertor.localDateTimeToDate(LocalDateTime.now().minusHours(10)));
        task3.getUrgency().setLastUpdate(DateConvertor.localDateTimeToDate(LocalDateTime.now().minusHours(10)));

        internalApi.updateAllUrgencies(true);

        List<Task> resultList = taskApi.listTasks(user, TaskOrdering.URGENCY_DESC);
        for (Task task : resultList) {
            System.out.println("TASK: " + task);
        }
        Assert.assertTrue(resultList.get(0).equals(task1));
        Assert.assertTrue(resultList.get(1).equals(task3));
        Assert.assertTrue(resultList.get(2).equals(task2));
    }

    @Test
    @Transactional
    public void listTasksFilterByRole() throws AlreadyExistsException, WrongParameterException, NotMemberOfException, AlreadyExistsException {
        User user = userApi.createUser("TestUser", "pwd", "Some name", "a@b.cb");
        Task task1 = taskApi.createTask(user, "CCC", null, null, 0, LocalDateTime.now().plusDays(12));
        Task task2 = taskApi.createTask(user, "AAA", null, null, 0, LocalDateTime.now().plusDays(10));
        Task task3 = taskApi.createTask(user, "BBB", null, null, 0, LocalDateTime.now().plusDays(11));
        Task task4 = taskApi.createTask(user, "BBB", null, null, 0, LocalDateTime.now().plusDays(11));
        Task task5 = taskApi.createTask(user, "BBB", null, null, 0, LocalDateTime.now().plusDays(11));

        taskApi.setTaskRole(user, task2, TaskRole.WORKER);
        taskApi.setTaskRole(user, task4, TaskRole.WORKER);

        List<Task> resultList;
        resultList = taskApi.listTasks(user, TaskOrdering.NONE, new TaskFilterRole(user, TaskRole.WATCHER));
        Assert.assertEquals(3, resultList.size());
        Assert.assertTrue(resultList.contains(task1));
        Assert.assertTrue(resultList.contains(task3));
        Assert.assertTrue(resultList.contains(task5));

        resultList = taskApi.listTasks(user, TaskOrdering.NONE, new TaskFilterRole(user, TaskRole.WORKER));
        Assert.assertEquals(2, resultList.size());
        Assert.assertTrue(resultList.contains(task2));
        Assert.assertTrue(resultList.contains(task4));
    }

    @Test
    @Transactional
    public void listTasksFilterByType() throws AlreadyExistsException, WrongParameterException, NotMemberOfException, AlreadyExistsException {
        User user = userApi.createUser("TestUser", "pwd", "Some name", "a@b.cb");
        Task task1 = taskApi.createTask(user, "CCC", null, null, 0, LocalDateTime.now().plusDays(12));
        Task task2 = taskApi.createTask(user, "AAA", null, null, 0, 1);
        Task task3 = taskApi.createTask(user, "BBB", null, null, 0, LocalDateTime.now().plusDays(11));
        Task task4 = taskApi.createTask(user, "BBB", null, null, 0, LocalDateTime.now().plusDays(11));
        Task task5 = taskApi.createTask(user, "BBB", null, null, 0, 2);

        List<Task> resultList;
        resultList = taskApi.listTasks(user, TaskOrdering.NONE, new TaskFilterType(DeadlineTask.class));
        Assert.assertEquals(3, resultList.size());
        Assert.assertTrue(resultList.contains(task1));
        Assert.assertTrue(resultList.contains(task3));
        Assert.assertTrue(resultList.contains(task4));

        resultList = taskApi.listTasks(user, TaskOrdering.NONE, new TaskFilterType(GrowingTask.class));
        Assert.assertEquals(2, resultList.size());
        Assert.assertTrue(resultList.contains(task2));
        Assert.assertTrue(resultList.contains(task5));
    }

    @Test
    @Transactional
    public void listTasksFilterByStatus() throws AlreadyExistsException, WrongParameterException, NotMemberOfException, NotAllowedException, AlreadyExistsException, TaskPermissionException {
        User user = userApi.createUser("TestUser", "pwd", "Some name", "a@b.cb");
        Task task1 = taskApi.createTask(user, "CCC", null, null, 0, LocalDateTime.now().plusDays(12));
        Task task2 = taskApi.createTask(user, "AAA", null, null, 0, 1);
        Task task3 = taskApi.createTask(user, "BBB", null, null, 0, LocalDateTime.now().plusDays(11));
        Task task4 = taskApi.createTask(user, "BBB", null, null, 0, LocalDateTime.now().plusDays(11));
        Task task5 = taskApi.createTask(user, "BBB", null, null, 0, 2);
        Task task6 = taskApi.createTask(user, "BBB", null, null, 0, 2);
        Task task7 = taskApi.createTask(user, "BBB", null, null, 0, 2);

        taskApi.setTaskRole(user, task1, TaskRole.WORKER);
        taskApi.setTaskRole(user, task2, TaskRole.WORKER);
        taskApi.setTaskRole(user, task3, TaskRole.WORKER);
        taskApi.setTaskRole(user, task4, TaskRole.WORKER);
        taskApi.setTaskRole(user, task5, TaskRole.WORKER);
        taskApi.setTaskRole(user, task6, TaskRole.WORKER);
        taskApi.setTaskRole(user, task7, TaskRole.WORKER);

        taskApi.setTaskStatus(user, task1, TaskStatus.IN_PROGRESS);
        taskApi.setTaskStatus(user, task2, TaskStatus.CANCELLED);
        taskApi.setTaskStatus(user, task3, TaskStatus.IN_PROGRESS);
        taskApi.setTaskStatus(user, task4, TaskStatus.COMPLETED);
//        taskApi.setTaskStatus(user, task5, TaskStatus.OPEN);
//        taskApi.setTaskStatus(user, task6, TaskStatus.OPEN);
        taskApi.setTaskStatus(user, task7, TaskStatus.COMPLETED);


        List<Task> resultList;
        resultList = taskApi.listTasks(user, TaskOrdering.NONE, new TaskFilterStatus(TaskStatus.OPEN));
        Assert.assertEquals(2, resultList.size());
        Assert.assertTrue(resultList.contains(task5));
        Assert.assertTrue(resultList.contains(task6));

        resultList = taskApi.listTasks(user, TaskOrdering.NONE, new TaskFilterStatus(TaskStatus.IN_PROGRESS));
        Assert.assertEquals(2, resultList.size());
        Assert.assertTrue(resultList.contains(task1));
        Assert.assertTrue(resultList.contains(task3));

        resultList = taskApi.listTasks(user, TaskOrdering.NONE, new TaskFilterStatus(TaskStatus.CANCELLED));
        Assert.assertEquals(1, resultList.size());
        Assert.assertTrue(resultList.contains(task2));

        resultList = taskApi.listTasks(user, TaskOrdering.NONE, new TaskFilterStatus(TaskStatus.COMPLETED));
        Assert.assertEquals(2, resultList.size());
        Assert.assertTrue(resultList.contains(task4));
        Assert.assertTrue(resultList.contains(task7));
    }

    @Test
    @Transactional
    public void listTasksFilterByPriority() throws AlreadyExistsException, WrongParameterException, AlreadyExistsException {
        User user = userApi.createUser("TestUser", "pwd", "Some name", "a@b.ca");
        Task task1 = taskApi.createTask(user, "CCC", null, Priority.NORMAL, 0, LocalDateTime.now().plusDays(12));
        Task task2 = taskApi.createTask(user, "AAA", null, Priority.HIGH, 0, LocalDateTime.now().plusDays(10));
        Task task3 = taskApi.createTask(user, "BBB", null, Priority.LOW, 0, LocalDateTime.now().plusDays(11));
        Task task4 = taskApi.createTask(user, "BBBC", null, Priority.LOWEST, 0, LocalDateTime.now().plusDays(11));
        Task task5 = taskApi.createTask(user, "BBBD", null, Priority.HIGHEST, 0, LocalDateTime.now().plusDays(11));
        Task task6 = taskApi.createTask(user, "BBBD", null, Priority.HIGHEST, 0, LocalDateTime.now().plusDays(11));

        List<Task> resultList;
        resultList = taskApi.listTasks(user, TaskOrdering.NONE, new TaskFilterPriority(Priority.LOWEST));
        Assert.assertTrue(resultList.size() == 1);
        Assert.assertTrue(resultList.contains(task4));

        resultList = taskApi.listTasks(user, TaskOrdering.NONE, new TaskFilterPriority(Priority.LOW));
        Assert.assertTrue(resultList.size() == 1);
        Assert.assertTrue(resultList.contains(task3));

        resultList = taskApi.listTasks(user, TaskOrdering.NONE, new TaskFilterPriority(Priority.NORMAL));
        Assert.assertTrue(resultList.size() == 1);
        Assert.assertTrue(resultList.contains(task1));

        resultList = taskApi.listTasks(user, TaskOrdering.NONE, new TaskFilterPriority(Priority.HIGH));
        Assert.assertTrue(resultList.size() == 1);
        Assert.assertTrue(resultList.contains(task2));

        resultList = taskApi.listTasks(user, TaskOrdering.NONE, new TaskFilterPriority(Priority.HIGHEST));
        Assert.assertTrue(resultList.size() == 2);
        Assert.assertTrue(resultList.contains(task5));
        Assert.assertTrue(resultList.contains(task6));

        // Multiple priorities
        resultList = taskApi.listTasks(user, TaskOrdering.NONE, new TaskFilterPriority(Priority.LOWEST, Priority.LOW));
        Assert.assertTrue(resultList.size() == 2);
        Assert.assertTrue(resultList.contains(task3));
        Assert.assertTrue(resultList.contains(task4));

        resultList = taskApi.listTasks(user, TaskOrdering.NONE, new TaskFilterPriority(Priority.LOWEST,
                Priority.LOW, Priority.HIGHEST));
        Assert.assertTrue(resultList.size() == 4);
        Assert.assertTrue(resultList.contains(task3));
        Assert.assertTrue(resultList.contains(task4));
        Assert.assertTrue(resultList.contains(task5));
        Assert.assertTrue(resultList.contains(task6));
    }

    @Test
    @Transactional
    public void setTaskRoleByGroupManager() throws AlreadyExistsException, WrongParameterException, GroupPermissionException, NotMemberOfException, AlreadyExistsException, NotAllowedException {
        User userMember = userApi.createUser("Member", "password", "John Doe", "a@b.c");
        User userMember2 = userApi.createUser("Member2", "password", "John Doe", "a@b.c");
        User userAdmin = userApi.createUser("Admin", "password", "John Doe", "c@b.c");
        Group group = groupApi.createGroup("Groupname", userAdmin, "Random description");

        Task task = taskApi.createTask(userMember, "TestTask", null, null, 0, LocalDateTime.now().plusDays(10));
        Task task2 = taskApi.createTask(userMember, "TestTask2", null, null, 0, LocalDateTime.now().plusDays(101));
        Task task3 = taskApi.createTask(userAdmin, "TestTask3", null, null, 0, LocalDateTime.now().plusDays(102));

        groupApi.addTask(userAdmin, group, task);
        groupApi.addTask(userAdmin, group, task2);
        groupApi.addTask(userAdmin, group, task3);

        groupApi.addMember(userAdmin, group, userMember);

        Assert.assertTrue(taskApi.getTaskParticipant(userMember, task).getRole() == TaskRole.WATCHER);
        Assert.assertTrue(taskApi.getTaskParticipant(userMember, task2).getRole() == TaskRole.WATCHER);

        taskApi.setTaskRole(userMember, task, TaskRole.WORKER, userAdmin, group);

        Assert.assertTrue(taskApi.getTaskParticipant(userMember, task).getRole() == TaskRole.WORKER);
        Assert.assertTrue(taskApi.getTaskParticipant(userMember, task2).getRole() == TaskRole.WATCHER);
    }

    @Test(expected = NotAllowedException.class)
    @Transactional
    public void setTaskRoleByGroupManagerFail() throws AlreadyExistsException, WrongParameterException, GroupPermissionException, NotMemberOfException, AlreadyExistsException, NotAllowedException {
        User userMember = userApi.createUser("Member", "password", "John Doe", "a@b.c");
        User userAdmin = userApi.createUser("Admin", "password", "John Doe", "c@b.c");
        Group group = groupApi.createGroup("Groupname", userAdmin, "Random description");

        Task task = taskApi.createTask(userMember, "TestTask", null, null, 0, LocalDateTime.now().plusDays(10));
        Task task2 = taskApi.createTask(userMember, "TestTask2", null, null, 0, LocalDateTime.now().plusDays(101));
        Task task3 = taskApi.createTask(userAdmin, "TestTask3", null, null, 0, LocalDateTime.now().plusDays(102));

        groupApi.addTask(userAdmin, group, task);
        groupApi.addTask(userAdmin, group, task2);

        taskParticipantHelper.editOrCreateTaskParticipant(userMember, task3, TaskRole.WATCHER, null, false);

        groupApi.addMember(userAdmin, group, userMember);

        Assert.assertTrue(taskApi.getTaskParticipant(userMember, task).getRole() == TaskRole.WATCHER);
        Assert.assertTrue(taskApi.getTaskParticipant(userMember, task2).getRole() == TaskRole.WATCHER);

        taskApi.setTaskRole(userMember, task3, TaskRole.WORKER, userAdmin, group);
    }

    @Test
    @Transactional
    public void editTask() throws AlreadyExistsException, WrongParameterException, TaskPermissionException, NotMemberOfException, NotAllowedException, AlreadyExistsException {
        User userWorker = userApi.createUser("Member", "password", "John Doe", "a@b.c");

        LocalDateTime deadlineDateTime = LocalDateTime.of(1999, 6, 2, 12, 45, 50);
        LocalDateTime newDeadlineDateTime = LocalDateTime.of(2001, 7, 3, 22, 45, 50);

        Task task1 = taskApi.createTask(userWorker, "TestTask", "Desc1", Priority.LOWEST, 0, deadlineDateTime);

        taskApi.setTaskRole(userWorker, task1, TaskRole.WORKER);

        Assert.assertEquals(Priority.LOWEST, task1.getPriority());
        taskApi.editTask(userWorker, task1, null, null, null, Priority.HIGHEST);
        Assert.assertEquals(Priority.HIGHEST, task1.getPriority());

        Assert.assertEquals("Desc1", task1.getDescription());
        taskApi.editTask(userWorker, task1, "NewDesc", null, null, null);
        Assert.assertEquals("NewDesc", task1.getDescription());

        Assert.assertEquals(DateConvertor.dateToLocalDateTime(((DeadlineTask) task1).getDeadline()), deadlineDateTime);
        taskApi.editTask(userWorker, task1, null, newDeadlineDateTime, null, null);
        Assert.assertEquals(DateConvertor.dateToLocalDateTime(((DeadlineTask) task1).getDeadline()), newDeadlineDateTime);

        Assert.assertTrue(task1.getWorkEstimate() == 0);
        taskApi.editTask(userWorker, task1, null, null, 10.5, null);
        Assert.assertTrue(task1.getWorkEstimate() == 10.5);

        taskApi.setTaskRole(userWorker, task1, TaskRole.WATCHER);
        expectedException.expect(TaskPermissionException.class);
        taskApi.editTask(userWorker, task1, null, null, 11d, null);
    }


    @Test
    @Transactional
    public void listGroupTasksSortByName() throws AlreadyExistsException, WrongParameterException, GroupPermissionException, NotMemberOfException, AlreadyExistsException {
        User user = userApi.createUser("TestUser", "pwd", "Some name", "a@b.c");
        Group group1 = groupApi.createGroup("Group1", user, null);
        Group group2 = groupApi.createGroup("Group2", user, null);

        Task task1 = taskApi.createTask(user, "CCC", null, null, 0, LocalDateTime.now().plusDays(10));
        Task task2 = taskApi.createTask(user, "AAA", null, null, 0, LocalDateTime.now().plusDays(11));
        Task task3 = taskApi.createTask(user, "BBB", null, null, 0, LocalDateTime.now().plusDays(11));

        groupApi.addTask(user, group1, task1);
        groupApi.addTask(user, group1, task2);
        groupApi.addTask(user, group2, task3);


        List<Task> resultList;

        resultList = taskApi.listTasks(group1, TaskOrdering.NAME_ASC);
        Assert.assertTrue(resultList.get(0).equals(task2));
        Assert.assertTrue(resultList.get(1).equals(task1));

        resultList = taskApi.listTasks(group2, TaskOrdering.NAME_ASC);
        Assert.assertTrue(resultList.get(0).equals(task3));


        resultList = taskApi.listTasks(group1, TaskOrdering.NAME_DESC);
        Assert.assertTrue(resultList.get(1).equals(task2));
        Assert.assertTrue(resultList.get(0).equals(task1));

        resultList = taskApi.listTasks(group2, TaskOrdering.NAME_DESC);
        Assert.assertTrue(resultList.get(0).equals(task3));
    }

    @Test
    @Transactional
    public void listGroupTasksSortByDateCreated() throws AlreadyExistsException, WrongParameterException, GroupPermissionException, NotMemberOfException, AlreadyExistsException {
        User user = userApi.createUser("TestUser", "pwd", "Some name", "a@b.c");
        Group group1 = groupApi.createGroup("Group1", user, null);
        Group group2 = groupApi.createGroup("Group2", user, null);

        Task task1 = taskApi.createTask(user, "CCC", null, null, 0, LocalDateTime.now().plusDays(10));
        sleepALittle();
        Task task2 = taskApi.createTask(user, "AAA", null, null, 0, LocalDateTime.now().plusDays(11));
        sleepALittle();
        Task task3 = taskApi.createTask(user, "BBB", null, null, 0, LocalDateTime.now().plusDays(1));

        groupApi.addTask(user, group1, task1);
        groupApi.addTask(user, group2, task2);
        groupApi.addTask(user, group2, task3);


        List<Task> resultList;

        resultList = taskApi.listTasks(group1, TaskOrdering.DATE_START_ASC);
        Assert.assertTrue(resultList.get(0).equals(task1));

        resultList = taskApi.listTasks(group2, TaskOrdering.DATE_START_ASC);
        Assert.assertTrue(resultList.get(0).equals(task2));
        Assert.assertTrue(resultList.get(1).equals(task3));


        resultList = taskApi.listTasks(group1, TaskOrdering.DATE_START_DESC);
        Assert.assertTrue(resultList.get(0).equals(task1));

        resultList = taskApi.listTasks(group2, TaskOrdering.DATE_START_DESC);
        Assert.assertTrue(resultList.get(1).equals(task2));
        Assert.assertTrue(resultList.get(0).equals(task3));
    }

    @Transactional
    @Test
    public void resetUrgency() throws AlreadyExistsException, WrongParameterException, AlreadyExistsException, TaskPermissionException, NotMemberOfException, NotAllowedException {
        User user = userApi.createUser("TestUser", "pwd", "Some name", "a@b.c");

        Task task1 = taskApi.createTask(user, "CCC", null, null, 0, 15);
        Task task2 = taskApi.createTask(user, "AAA", null, null, 0, 20);
        Task task3 = taskApi.createTask(user, "BBB", null, null, 0, 30);

        taskApi.setTaskRole(user, task1, TaskRole.WORKER);
        taskApi.setTaskRole(user, task2, TaskRole.WORKER);
        taskApi.setTaskRole(user, task3, TaskRole.WORKER);

        task1.getUrgency().setLastUpdate(DateConvertor.localDateTimeToDate(LocalDateTime.now().minusHours(10)));
        task2.getUrgency().setLastUpdate(DateConvertor.localDateTimeToDate(LocalDateTime.now().minusHours(10)));
        task3.getUrgency().setLastUpdate(DateConvertor.localDateTimeToDate(LocalDateTime.now().minusHours(10)));

        internalApi.updateAllUrgencies();

        Assert.assertTrue(task1.getUrgency().getValue() > 0);
        Assert.assertTrue(task2.getUrgency().getValue() > 0);
        Assert.assertTrue(task3.getUrgency().getValue() > 0);

        taskApi.resetUrgency(user, task1);
        Assert.assertEquals(0, task1.getUrgency().getValue(), 0.1);
        Assert.assertTrue(task2.getUrgency().getValue() > 0);
        Assert.assertTrue(task3.getUrgency().getValue() > 0);

        taskApi.resetUrgency(user, task2);
        Assert.assertEquals(0, task1.getUrgency().getValue(), 0.1);
        Assert.assertEquals(0, task2.getUrgency().getValue(), 0.1);
        Assert.assertTrue(task3.getUrgency().getValue() > 0);

        taskApi.resetUrgency(user, task3);
        Assert.assertEquals(0, task1.getUrgency().getValue(), 0.1);
        Assert.assertEquals(0, task2.getUrgency().getValue(), 0.1);
        Assert.assertEquals(0, task3.getUrgency().getValue(), 0.1);
    }

    @Transactional
    @Test(expected = NotAllowedException.class)
    public void resetUrgencyNotGrowingTask() throws Exception {
        User user = userApi.createUser("TestUser", "pwd", "Some name", "a@b.c");

        Task task1 = taskApi.createTask(user, "CCC", null, null, 0, LocalDateTime.now().plusDays(1));

        taskApi.setTaskRole(user, task1, TaskRole.WORKER);

        taskApi.resetUrgency(user, task1);
    }

    private void sleepALittle() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}