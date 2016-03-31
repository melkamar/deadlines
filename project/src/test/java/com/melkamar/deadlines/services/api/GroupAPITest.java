package com.melkamar.deadlines.services.api;

import com.melkamar.deadlines.DeadlinesApplication;
import com.melkamar.deadlines.dao.group.GroupDAO;
import com.melkamar.deadlines.dao.taskparticipant.TaskParticipantDAO;
import com.melkamar.deadlines.dao.taskparticipant.TaskParticipantDAOHibernate;
import com.melkamar.deadlines.dao.user.UserDAO;
import com.melkamar.deadlines.exceptions.*;
import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.MemberRole;
import com.melkamar.deadlines.model.TaskParticipant;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.task.Task;
import com.melkamar.deadlines.services.api.GroupAPI;
import com.melkamar.deadlines.services.api.UserAPI;
import com.melkamar.deadlines.services.helpers.GroupMemberHelper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 26.03.2016 16:07
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DeadlinesApplication.class)
public class GroupAPITest {
    @Autowired
    private GroupDAO groupDAO;
    @Autowired
    private GroupAPI groupAPI;


    @Autowired
    private UserDAO userDAO;
    @Autowired
    private UserAPI userAPI;

    @Autowired
    private GroupMemberHelper groupMemberHelper;
    @Autowired
    private TaskParticipantDAO taskParticipantDAO;
    @Autowired
    private TaskAPI taskAPI;

    @Test(expected = WrongParameterException.class)
    @Transactional
    public void nullParameters() throws WrongParameterException {
        groupAPI.createGroup(null, null, null);
    }

    @Test(expected = WrongParameterException.class)
    @Transactional
    public void nullFounder() throws WrongParameterException {
        groupAPI.createGroup("SomeName", null, null);
    }

    @Test
    @Transactional
    public void founderAdmin() throws WrongParameterException {
        User user = userAPI.createUser("GroupAdmin", "pwd", null, null);
        Group group = groupAPI.createGroup("AGroup", user, null);

        User retrievedUser = userDAO.findByUsername("GroupAdmin");
        Group retrievedGroup = groupDAO.findByName("AGroup");

        Assert.assertEquals(retrievedUser, retrievedGroup.getGroupMembers(MemberRole.ADMIN).iterator().next().getUser());
        Assert.assertTrue(groupMemberHelper.getGroupMember(retrievedUser, retrievedGroup).getRole() == MemberRole.ADMIN);
    }

    @Test(expected = WrongParameterException.class)
    @Transactional
    public void setManagerWrongParam1() throws WrongParameterException, GroupPermissionException, NotMemberOfException, NotAllowedException {
        User userMember = userAPI.createUser("Member", "password", "John Doe", "a@b.c");
        User userManager = userAPI.createUser("Manager", "password", "John Doe", "b@b.c");
        User userAdmin = userAPI.createUser("Admin", "password", "John Doe", "c@b.c");
        User userNonmember = userAPI.createUser("Nonmember", "password", "John Doe", "d@b.c");

        Group group = groupAPI.createGroup("Groupname", userAdmin, "Random description");

        groupAPI.setManager(null, group, userMember, true);
    }

    @Test(expected = WrongParameterException.class)
    @Transactional
    public void setManagerWrongParam2() throws WrongParameterException, GroupPermissionException, NotMemberOfException, NotAllowedException {
        User userMember = userAPI.createUser("Member", "password", "John Doe", "a@b.c");
        User userManager = userAPI.createUser("Manager", "password", "John Doe", "b@b.c");
        User userAdmin = userAPI.createUser("Admin", "password", "John Doe", "c@b.cb");
        User userNonmember = userAPI.createUser("Nonmember", "password", "John Doe", "d@b.c");

        Group group = groupAPI.createGroup("Groupname", userAdmin, "Random description");

        groupAPI.setManager(userAdmin, null, userMember, true);
    }

    @Test(expected = WrongParameterException.class)
    @Transactional
    public void setManagerWrongParam3() throws WrongParameterException, GroupPermissionException, NotMemberOfException, AlreadyExistsException, NotAllowedException {
        User userMember = userAPI.createUser("Member", "password", "John Doe", "a@b.c");
        User userManager = userAPI.createUser("Manager", "password", "John Doe", "b@b.ca");
        User userAdmin = userAPI.createUser("Admin", "password", "John Doe", "c@b.c");
        User userNonmember = userAPI.createUser("Nonmember", "password", "John Doe", "d@b.c");

        Group group = groupAPI.createGroup("Groupname", userAdmin, "Random description");

        groupAPI.setManager(userAdmin, group, null, true);
    }

    @Test(expected = GroupPermissionException.class)
    @Transactional
    public void setManagerNoPermission() throws WrongParameterException, GroupPermissionException, NotMemberOfException, AlreadyExistsException, NotAllowedException {
        User userMember = userAPI.createUser("Member", "password", "John Doe", "a@b.c");
        User userManager = userAPI.createUser("Manager", "password", "John Doe", "b@b.ca");
        User userAdmin = userAPI.createUser("Admin", "password", "John Doe", "c@b.c");
        User userNonmember = userAPI.createUser("Nonmember", "password", "John Doe", "d@b.ca");

        Group group = groupAPI.createGroup("Groupname", userAdmin, "Random description");
        groupMemberHelper.createGroupMember(userMember, group, MemberRole.MEMBER);
        groupMemberHelper.createGroupMember(userManager, group, MemberRole.MANAGER);

        groupAPI.setManager(userManager, group, userMember, true);
    }

    @Test(expected = NotMemberOfException.class)
    @Transactional
    public void setManagerTargetNotMember() throws WrongParameterException, GroupPermissionException, NotMemberOfException, AlreadyExistsException, NotAllowedException {
        User userMember = userAPI.createUser("Member", "password", "John Doe", "a@b.c");
        User userManager = userAPI.createUser("Manager", "password", "John Doe", "b@b.ca");
        User userAdmin = userAPI.createUser("Admin", "password", "John Doe", "c@b.c");
        User userNonmember = userAPI.createUser("Nonmember", "password", "John Doe", "d@b.ca");

        Group group = groupAPI.createGroup("Groupname", userAdmin, "Random description");

        groupAPI.setManager(userAdmin, group, userMember, true);
    }

    @Test
    @Transactional
    public void setManagerOk() throws WrongParameterException, GroupPermissionException, NotMemberOfException, AlreadyExistsException, NotAllowedException {
        User userMember = userAPI.createUser("Member", "password", "John Doe", "a@b.c");
        User userManager = userAPI.createUser("Manager", "password", "John Doe", "b@b.ca");
        User userAdmin = userAPI.createUser("Admin", "password", "John Doe", "c@b.c");
        User userNonmember = userAPI.createUser("Nonmember", "password", "John Doe", "d@b.ca");

        Group group = groupAPI.createGroup("Groupname", userAdmin, "Random description");
        groupMemberHelper.createGroupMember(userMember, group, MemberRole.MEMBER);
        groupMemberHelper.createGroupMember(userManager, group, MemberRole.MANAGER);

        Assert.assertTrue(groupMemberHelper.getGroupMember(userMember, group).getRole() == MemberRole.MEMBER);
        groupAPI.setManager(userAdmin, group, userMember, true);
        Assert.assertTrue(groupMemberHelper.getGroupMember(userMember, group).getRole() == MemberRole.MANAGER);

        Assert.assertTrue(groupMemberHelper.getGroupMember(userManager, group).getRole() == MemberRole.MANAGER);
        groupAPI.setManager(userAdmin, group, userManager, false);
        Assert.assertTrue(groupMemberHelper.getGroupMember(userManager, group).getRole() == MemberRole.MEMBER);
    }

    @Test(expected = NotAllowedException.class)
    @Transactional
    public void setManagerOnAdmin() throws WrongParameterException, GroupPermissionException, NotMemberOfException, AlreadyExistsException, NotAllowedException {
        User userMember = userAPI.createUser("Member", "password", "John Doe", "a@b.c");
        User userManager = userAPI.createUser("Manager", "password", "John Doe", "b@b.cab");
        User userAdmin = userAPI.createUser("Admin", "password", "John Doe", "c@b.c");
        User userNonmember = userAPI.createUser("Nonmember", "password", "John Doe", "d@b.ca");

        Group group = groupAPI.createGroup("Groupname", userAdmin, "Random description");
        groupMemberHelper.createGroupMember(userMember, group, MemberRole.MEMBER);
        groupMemberHelper.createGroupMember(userManager, group, MemberRole.MANAGER);

        groupAPI.setManager(userAdmin, group, userAdmin, true);
    }

    @Test
    @Transactional
    public void addMember() throws WrongParameterException, GroupPermissionException, NotMemberOfException, AlreadyExistsException {
        User userToBeMember = userAPI.createUser("Member", "password", "John Doe", "a@b.c");
        User userAdmin = userAPI.createUser("Admin", "password", "John Doe", "c@b.c");
        Group group = groupAPI.createGroup("Groupname", userAdmin, "Random description");

        Set<Group> groupSet = new HashSet<>();
        groupSet.add(group);

        Task task = taskAPI.createTask(userAdmin, "TestTask", null, null, 0, groupSet, LocalDateTime.now().plusDays(10));
        Task task2 = taskAPI.createTask(userAdmin, "TestTask2", null, null, 0, groupSet, LocalDateTime.now().plusDays(101));
        Task task3 = taskAPI.createTask(userToBeMember, "TestTask3", null, null, 0, null, LocalDateTime.now().plusDays(102));

        Assert.assertEquals(userAdmin.tasksOfUser().size(), 2);
        Assert.assertEquals(userToBeMember.tasksOfUser().size(), 1);
        Assert.assertEquals(group.getSharedTasks().size(), 2);

        groupAPI.addMember(userAdmin, group, userToBeMember);
        Assert.assertEquals(userAdmin.tasksOfUser().size(), 2);
        Assert.assertEquals(userToBeMember.tasksOfUser().size(), 3);
        Assert.assertEquals(group.getSharedTasks().size(), 2);
    }


    @Test
    @Transactional
    public void addAndRemoveMember() throws WrongParameterException, GroupPermissionException, NotMemberOfException, AlreadyExistsException, NotAllowedException {
        /**
         * Scenario:
         * 1) Have some tests and a group with admin
         * 2) Share tests with group
         * 3) Add userMember to group, check if group's tasks are shared with him
         * 4) Add userMember2 to group, check if group's tasks are shared with him
         * 5) Remove userMember from group, check if tasks only from group are removed (including TaskParticipant) and that tasks that were both solo and from group are not removed
         * 6) Remove userMember2 from group, same as 5)
         */
        User userMember = userAPI.createUser("Member", "password", "John Doe", "a@b.c");
        User userMember2 = userAPI.createUser("Member2", "password", "John Doe", "a@b.c");
        User userAdmin = userAPI.createUser("Admin", "password", "John Doe", "c@b.c");
        Group group = groupAPI.createGroup("Groupname", userAdmin, "Random description");

        Task task = taskAPI.createTask(userMember, "TestTask", null, null, 0, LocalDateTime.now().plusDays(10));
        Task task2 = taskAPI.createTask(userMember, "TestTask2", null, null, 0, LocalDateTime.now().plusDays(101));
        Task task3 = taskAPI.createTask(userMember, "TestTask3", null, null, 0, LocalDateTime.now().plusDays(101));
        Task task4 = taskAPI.createTask(userMember2, "TestTask4", null, null, 0, LocalDateTime.now().plusDays(101));
        Task task5 = taskAPI.createTask(userAdmin, "TestTask5", null, null, 0, LocalDateTime.now().plusDays(102));
        Task task6 = taskAPI.createTask(userAdmin, "TestTask6", null, null, 0, LocalDateTime.now().plusDays(102));

        Assert.assertTrue(task2.usersOnTask().size() == 1);

        groupAPI.addTask(userAdmin, group, task2);
        groupAPI.addTask(userAdmin, group, task3);
        groupAPI.addTask(userAdmin, group, task5);

        /**
         * (s) - solo | (g) - group
         * group: task2, task3, task5 (members: admin)
         * member: task(s), task2(s), task3(s)
         * member2: task4(s)
         * admin: task5(sg), task6, task2(g) task3(g)
         */
        Assert.assertTrue(userMember.tasksOfUser().size() == 3);
        Assert.assertTrue(userMember.tasksOfUser().contains(task));
        Assert.assertTrue(userMember.tasksOfUser().contains(task2));
        Assert.assertTrue(userMember.tasksOfUser().contains(task3));
        Assert.assertNotNull(taskParticipantDAO.findByUserAndTask(userMember, task2)); // test if solo-TaskParticipant will be preserved after user is removed from group
        Assert.assertNull(taskParticipantDAO.findByUserAndTask(userMember, task5)); // test if taskParticipant will be created and then destroyed when user joins/leaves the group

        Assert.assertTrue(userMember2.tasksOfUser().size() == 1);
        Assert.assertTrue(userMember2.tasksOfUser().contains(task4));

        Assert.assertTrue(userAdmin.tasksOfUser().size() == 4);
        Assert.assertTrue(userAdmin.tasksOfUser().contains(task5));
        Assert.assertTrue(userAdmin.tasksOfUser().contains(task6));
        Assert.assertTrue(userAdmin.tasksOfUser().contains(task2));
        Assert.assertTrue(userAdmin.tasksOfUser().contains(task3));

        Assert.assertTrue(task2.usersOnTask().size() == 2);

        Assert.assertEquals(group.getGroupMembers().size(), 1);


        //
        groupAPI.addMember(userAdmin, group, userMember);
        /**
         * (s) - solo | (g) - group
         * group: task2, task3, task5 (members: admin, member)
         * member: task(s), task2(sg), task3(sg), task5(g)
         * member2: task4(s)
         * admin: task5(sg), task6, task2(g) task3(g)
         */
        Assert.assertTrue(userMember.tasksOfUser().size() == 4);
        Assert.assertTrue(userMember.tasksOfUser().contains(task));
        Assert.assertTrue(userMember.tasksOfUser().contains(task2));
        Assert.assertTrue(userMember.tasksOfUser().contains(task3));
        Assert.assertTrue(userMember.tasksOfUser().contains(task5));
        Assert.assertNotNull(taskParticipantDAO.findByUserAndTask(userMember, task2)); // test if solo-TaskParticipant will be preserved after user is removed from group
        Assert.assertNotNull(taskParticipantDAO.findByUserAndTask(userMember, task5)); // test if taskParticipant will be created and then destroyed when user joins/leaves the group

        Assert.assertTrue(userMember2.tasksOfUser().size() == 1);
        Assert.assertTrue(userMember2.tasksOfUser().contains(task4));
        Assert.assertNotNull(taskParticipantDAO.findByUserAndTask(userMember2, task4)); // test if taskParticipant will be created and then destroyed when user joins/leaves the group
        Assert.assertNull(taskParticipantDAO.findByUserAndTask(userMember2, task2)); // test if taskParticipant will be created and then destroyed when user joins/leaves the group

        Assert.assertTrue(userAdmin.tasksOfUser().size() == 4);
        Assert.assertTrue(userAdmin.tasksOfUser().contains(task5));
        Assert.assertTrue(userAdmin.tasksOfUser().contains(task6));
        Assert.assertTrue(userAdmin.tasksOfUser().contains(task2));
        Assert.assertTrue(userAdmin.tasksOfUser().contains(task3));

        Assert.assertTrue(task2.usersOnTask().size() == 2);

        Assert.assertEquals(group.getGroupMembers().size(), 2);


        groupAPI.addMember(userAdmin, group, userMember2);
        /**
         * (s) - solo | (g) - group
         * group: task2, task3, task5 (members: admin, member, member2)
         * member: task(s), task2(sg), task3(sg), task5(g)
         * member2: task4(s), task2(g), task3(g), task5(g)
         * admin: task5(sg), task6, task2(g) task3(g)
         */
        Assert.assertTrue(userMember.tasksOfUser().size() == 4);
        Assert.assertTrue(userMember.tasksOfUser().contains(task));
        Assert.assertTrue(userMember.tasksOfUser().contains(task2));
        Assert.assertTrue(userMember.tasksOfUser().contains(task3));
        Assert.assertTrue(userMember.tasksOfUser().contains(task5));
        Assert.assertNotNull(taskParticipantDAO.findByUserAndTask(userMember, task2)); // test if solo-TaskParticipant will be preserved after user is removed from group
        Assert.assertNotNull(taskParticipantDAO.findByUserAndTask(userMember, task5)); // test if taskParticipant will be created and then destroyed when user joins/leaves the group

        Assert.assertTrue(userMember2.tasksOfUser().size() == 4);
        Assert.assertTrue(userMember2.tasksOfUser().contains(task4));
        Assert.assertTrue(userMember2.tasksOfUser().contains(task2));
        Assert.assertTrue(userMember2.tasksOfUser().contains(task3));
        Assert.assertTrue(userMember2.tasksOfUser().contains(task5));
        Assert.assertNotNull(taskParticipantDAO.findByUserAndTask(userMember2, task4)); // test if taskParticipant will be created and then destroyed when user joins/leaves the group
        Assert.assertNotNull(taskParticipantDAO.findByUserAndTask(userMember2, task2)); // test if taskParticipant will be created and then destroyed when user joins/leaves the group


        Assert.assertTrue(userAdmin.tasksOfUser().size() == 4);
        Assert.assertTrue(userAdmin.tasksOfUser().contains(task5));
        Assert.assertTrue(userAdmin.tasksOfUser().contains(task6));
        Assert.assertTrue(userAdmin.tasksOfUser().contains(task2));
        Assert.assertTrue(userAdmin.tasksOfUser().contains(task3));

        Assert.assertTrue(task2.usersOnTask().size() == 3);

        Assert.assertEquals(group.getGroupMembers().size(), 3);


        //
        groupAPI.removeMember(userAdmin, group, userMember);
        /**
         * (s) - solo | (g) - group
         * group: task2, task3, task5 (members: admin, member2)
         * member: task(s), task2(s), task3(s)
         * member2: task4(s), task2(g), task3(g), task5(g)
         * admin: task5(sg), task6, task2(g) task3(g)
         */
        Assert.assertTrue(userMember.tasksOfUser().size() == 3);
        Assert.assertTrue(userMember.tasksOfUser().contains(task));
        Assert.assertTrue(userMember.tasksOfUser().contains(task2));
        Assert.assertTrue(userMember.tasksOfUser().contains(task3));
        Assert.assertNotNull(taskParticipantDAO.findByUserAndTask(userMember, task2)); // test if solo-TaskParticipant will be preserved after user is removed from group
        Assert.assertNull(taskParticipantDAO.findByUserAndTask(userMember, task5)); // test if taskParticipant will be created and then destroyed when user joins/leaves the group

        Assert.assertTrue(userMember2.tasksOfUser().size() == 4);
        Assert.assertTrue(userMember2.tasksOfUser().contains(task4));
        Assert.assertTrue(userMember2.tasksOfUser().contains(task2));
        Assert.assertTrue(userMember2.tasksOfUser().contains(task3));
        Assert.assertTrue(userMember2.tasksOfUser().contains(task5));
        Assert.assertNotNull(taskParticipantDAO.findByUserAndTask(userMember2, task4)); // test if taskParticipant will be created and then destroyed when user joins/leaves the group
        Assert.assertNotNull(taskParticipantDAO.findByUserAndTask(userMember2, task2)); // test if taskParticipant will be created and then destroyed when user joins/leaves the group

        Assert.assertTrue(userAdmin.tasksOfUser().size() == 4);
        Assert.assertTrue(userAdmin.tasksOfUser().contains(task5));
        Assert.assertTrue(userAdmin.tasksOfUser().contains(task6));
        Assert.assertTrue(userAdmin.tasksOfUser().contains(task2));
        Assert.assertTrue(userAdmin.tasksOfUser().contains(task3));

        Assert.assertTrue(task2.usersOnTask().size() == 3);
        Assert.assertEquals(group.getGroupMembers().size(), 2);

        //
        groupAPI.removeMember(userAdmin, group, userMember2);
        /**
         * (s) - solo | (g) - group
         * group: task2, task3, task5 (members: admin)
         * member: task(s), task2(s), task3(s)
         * member2: task4(s)
         * admin: task5(sg), task6, task2(g) task3(g)
         */
        Assert.assertTrue(userMember.tasksOfUser().size() == 3);
        Assert.assertTrue(userMember.tasksOfUser().contains(task));
        Assert.assertTrue(userMember.tasksOfUser().contains(task2));
        Assert.assertTrue(userMember.tasksOfUser().contains(task3));
        Assert.assertNotNull(taskParticipantDAO.findByUserAndTask(userMember, task2)); // test if solo-TaskParticipant will be preserved after user is removed from group
        Assert.assertNull(taskParticipantDAO.findByUserAndTask(userMember, task5)); // test if taskParticipant will be created and then destroyed when user joins/leaves the group

        Assert.assertTrue(userMember2.tasksOfUser().size() == 1);
        Assert.assertTrue(userMember2.tasksOfUser().contains(task4));
        Assert.assertNotNull(taskParticipantDAO.findByUserAndTask(userMember2, task4)); // test if taskParticipant will be created and then destroyed when user joins/leaves the group
        Assert.assertNull(taskParticipantDAO.findByUserAndTask(userMember2, task2)); // test if taskParticipant will be created and then destroyed when user joins/leaves the group

        Assert.assertTrue(userAdmin.tasksOfUser().size() == 4);
        Assert.assertTrue(userAdmin.tasksOfUser().contains(task5));
        Assert.assertTrue(userAdmin.tasksOfUser().contains(task6));
        Assert.assertTrue(userAdmin.tasksOfUser().contains(task2));
        Assert.assertTrue(userAdmin.tasksOfUser().contains(task3));

        Assert.assertTrue(task2.usersOnTask().size() == 2);
        Assert.assertEquals(group.getGroupMembers().size(), 1);
    }

    @Test
    @Transactional
    public void addTask() throws WrongParameterException, GroupPermissionException, NotMemberOfException {
        User userNonMember = userAPI.createUser("Member", "password", "John Doe", "a@b.c");
        User userAdmin = userAPI.createUser("Admin", "password", "John Doe", "c@b.c");
        Group group = groupAPI.createGroup("Groupname", userAdmin, "Random description");

        Task task = taskAPI.createTask(userNonMember, "TestTask", null, null, 0, LocalDateTime.now().plusDays(10));
        Task task2 = taskAPI.createTask(userNonMember, "TestTask2", null, null, 0, LocalDateTime.now().plusDays(101));
        Task task3 = taskAPI.createTask(userAdmin, "TestTask3", null, null, 0, LocalDateTime.now().plusDays(102));

        Assert.assertEquals(group.getSharedTasks().size(), 0);
        Assert.assertEquals(userNonMember.tasksOfUser().size(), 2);
        Assert.assertEquals(userAdmin.tasksOfUser().size(), 1);

        groupAPI.addTask(userAdmin, group, task);
        Assert.assertEquals(group.getSharedTasks().size(), 1);
        Assert.assertEquals(userNonMember.tasksOfUser().size(), 2);
        Assert.assertEquals(userAdmin.tasksOfUser().size(), 2);

        groupAPI.addTask(userAdmin, group, task2);
        Assert.assertEquals(group.getSharedTasks().size(), 2);
        Assert.assertEquals(userNonMember.tasksOfUser().size(), 2);
        Assert.assertEquals(userAdmin.tasksOfUser().size(), 3);

        groupAPI.addTask(userAdmin, group, task3);
        Assert.assertEquals(group.getSharedTasks().size(), 3);
        Assert.assertEquals(userNonMember.tasksOfUser().size(), 2);
        Assert.assertEquals(userAdmin.tasksOfUser().size(), 3);
    }

    @Test
    @Transactional
    public void leaveTask() throws WrongParameterException, GroupPermissionException, NotMemberOfException, AlreadyExistsException {
        User userMember = userAPI.createUser("Member", "password", "John Doe", "a@b.c");
        User userAdmin = userAPI.createUser("Admin", "password", "John Doe", "c@b.c");
        Group group = groupAPI.createGroup("Groupname", userAdmin, "Random description");

        Task task = taskAPI.createTask(userMember, "TestTask", null, null, 0, LocalDateTime.now().plusDays(10));
        Task task2 = taskAPI.createTask(userMember, "TestTask2", null, null, 0, LocalDateTime.now().plusDays(101));
        Task task3 = taskAPI.createTask(userAdmin, "TestTask3", null, null, 0, LocalDateTime.now().plusDays(102));
        groupAPI.addMember(userAdmin, group, userMember);


        Assert.assertTrue(task.usersOnTask().size() == 1); // userMember
        Assert.assertTrue(group.getSharedTasks().size() == 0); // No shared task at start
        Assert.assertTrue(userMember.tasksOfUser().size() == 2);
        Assert.assertTrue(userAdmin.tasksOfUser().size() == 1);
        Assert.assertNull(taskParticipantDAO.findByUserAndTask(userAdmin, task));

        groupAPI.addTask(userAdmin, group, task);

        Assert.assertTrue(task.usersOnTask().size() == 2); // userMember, admin
        Assert.assertTrue(group.getSharedTasks().size() == 1);
        Assert.assertTrue(userMember.tasksOfUser().size() == 2);
        Assert.assertTrue(userAdmin.tasksOfUser().size() == 2);
        Assert.assertNotNull(taskParticipantDAO.findByUserAndTask(userAdmin, task));

        groupAPI.leaveTask(userAdmin, group, task);

        Assert.assertTrue(task.usersOnTask().size() == 1); // userMember
        Assert.assertTrue(group.getSharedTasks().size() == 0);
        Assert.assertTrue(userMember.tasksOfUser().size() == 2);
        Assert.assertTrue(userAdmin.tasksOfUser().size() == 1);
        Assert.assertNull(taskParticipantDAO.findByUserAndTask(userAdmin, task));
    }


}


