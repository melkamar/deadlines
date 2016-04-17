package com.melkamar.deadlines.services.api;

import com.melkamar.deadlines.DeadlinesApplication;
import com.melkamar.deadlines.dao.group.GroupDAO;
import com.melkamar.deadlines.dao.offer.grouptask.GroupTaskSharingDAOHibernate;
import com.melkamar.deadlines.dao.offer.membership.MembershipSharingDAOHibernate;
import com.melkamar.deadlines.dao.taskparticipant.TaskParticipantDAO;
import com.melkamar.deadlines.dao.user.UserDAO;
import com.melkamar.deadlines.exceptions.*;
import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.MemberRole;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.task.Task;
import com.melkamar.deadlines.services.helpers.GroupMemberHelper;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 26.03.2016 16:07
 */
//@Rollback(value = false)
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DeadlinesApplication.class)
@WebAppConfiguration
public class GroupApiImplTest {
    @Autowired
    private GroupDAO groupDAO;
    @Autowired
    private GroupApi groupApi;


    @Autowired
    private UserDAO userDAO;
    @Autowired
    private UserApi userApi;

    @Autowired
    private GroupMemberHelper groupMemberHelper;
    @Autowired
    private TaskParticipantDAO taskParticipantDAO;
    @Autowired
    private TaskApi taskApi;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @Autowired
    private SharingApi sharingApi;
    @Autowired
    private GroupTaskSharingDAOHibernate groupTaskSharingDao;
    @Autowired
    private MembershipSharingDAOHibernate membershipSharingDao;


    @Test(expected = WrongParameterException.class)
    @Transactional
    public void nullParameters() throws WrongParameterException, AlreadyExistsException {
        groupApi.createGroup(null, null, null);
    }

    @Test(expected = WrongParameterException.class)
    @Transactional
    public void nullFounder() throws WrongParameterException, AlreadyExistsException {
        groupApi.createGroup("SomeName", null, null);
    }

    @Test
    @Transactional
    public void founderAdmin() throws WrongParameterException, AlreadyExistsException, AlreadyExistsException {
        User user = userApi.createUser("GroupAdmin", "pwd", null, null);
        Group group = groupApi.createGroup("AGroup", user, null);

        User retrievedUser = userDAO.findByUsername("GroupAdmin");
        Group retrievedGroup = groupDAO.findByName("AGroup");

        Assert.assertEquals(retrievedUser, retrievedGroup.getGroupMembers(MemberRole.ADMIN).iterator().next().getUser());
        Assert.assertTrue(groupMemberHelper.getGroupMember(retrievedUser, retrievedGroup).getRole() == MemberRole.ADMIN);
    }

    @Test(expected = AlreadyExistsException.class)
    @Transactional
    public void duplicateName() throws WrongParameterException, AlreadyExistsException, AlreadyExistsException {
        User user = userApi.createUser("GroupAdmin", "pwd", null, null);
        Group group = groupApi.createGroup("AGroup", user, null);
        Group group2 = groupApi.createGroup("AGroup", user, null);
    }

    @Test(expected = WrongParameterException.class)
    @Transactional
    public void setManagerWrongParam1() throws WrongParameterException, GroupPermissionException, NotMemberOfException, NotAllowedException, AlreadyExistsException, AlreadyExistsException {
        User userMember = userApi.createUser("Member", "password", "John Doe", "a@b.c");
        User userManager = userApi.createUser("Manager", "password", "John Doe", "b@b.c");
        User userAdmin = userApi.createUser("Admin", "password", "John Doe", "c@b.c");
        User userNonmember = userApi.createUser("Nonmember", "password", "John Doe", "d@b.c");

        Group group = groupApi.createGroup("Groupname", userAdmin, "Random description");

        groupApi.setManager(null, group, userMember, true);
    }

    @Test(expected = WrongParameterException.class)
    @Transactional
    public void setManagerWrongParam2() throws AlreadyExistsException, WrongParameterException, GroupPermissionException, NotMemberOfException, NotAllowedException, AlreadyExistsException {
        User userMember = userApi.createUser("Member", "password", "John Doe", "a@b.c");
        User userManager = userApi.createUser("Manager", "password", "John Doe", "b@b.c");
        User userAdmin = userApi.createUser("Admin", "password", "John Doe", "c@b.cb");
        User userNonmember = userApi.createUser("Nonmember", "password", "John Doe", "d@b.c");

        Group group = groupApi.createGroup("Groupname", userAdmin, "Random description");

        groupApi.setManager(userAdmin, null, userMember, true);
    }

    @Test(expected = WrongParameterException.class)
    @Transactional
    public void setManagerWrongParam3() throws AlreadyExistsException, WrongParameterException, GroupPermissionException, NotMemberOfException, AlreadyExistsException, NotAllowedException {
        User userMember = userApi.createUser("Member", "password", "John Doe", "a@b.c");
        User userManager = userApi.createUser("Manager", "password", "John Doe", "b@b.ca");
        User userAdmin = userApi.createUser("Admin", "password", "John Doe", "c@b.c");
        User userNonmember = userApi.createUser("Nonmember", "password", "John Doe", "d@b.c");

        Group group = groupApi.createGroup("Groupname", userAdmin, "Random description");

        groupApi.setManager(userAdmin, group, null, true);
    }

    @Test(expected = GroupPermissionException.class)
    @Transactional
    public void setManagerNoPermission() throws AlreadyExistsException, WrongParameterException, GroupPermissionException, NotMemberOfException, AlreadyExistsException, NotAllowedException {
        User userMember = userApi.createUser("Member", "password", "John Doe", "a@b.c");
        User userManager = userApi.createUser("Manager", "password", "John Doe", "b@b.ca");
        User userAdmin = userApi.createUser("Admin", "password", "John Doe", "c@b.c");
        User userNonmember = userApi.createUser("Nonmember", "password", "John Doe", "d@b.ca");

        Group group = groupApi.createGroup("Groupname", userAdmin, "Random description");
        groupMemberHelper.createGroupMember(userMember, group, MemberRole.MEMBER);
        groupMemberHelper.createGroupMember(userManager, group, MemberRole.MANAGER);

        groupApi.setManager(userManager, group, userMember, true);
    }

    @Test(expected = NotMemberOfException.class)
    @Transactional
    public void setManagerTargetNotMember() throws AlreadyExistsException, WrongParameterException, GroupPermissionException, NotMemberOfException, AlreadyExistsException, NotAllowedException {
        User userMember = userApi.createUser("Member", "password", "John Doe", "a@b.c");
        User userManager = userApi.createUser("Manager", "password", "John Doe", "b@b.ca");
        User userAdmin = userApi.createUser("Admin", "password", "John Doe", "c@b.c");
        User userNonmember = userApi.createUser("Nonmember", "password", "John Doe", "d@b.ca");

        Group group = groupApi.createGroup("Groupname", userAdmin, "Random description");

        groupApi.setManager(userAdmin, group, userMember, true);
    }

    @Test
    @Transactional
    public void setManagerOk() throws AlreadyExistsException, WrongParameterException, GroupPermissionException, NotMemberOfException, AlreadyExistsException, NotAllowedException {
        User userMember = userApi.createUser("Member", "password", "John Doe", "a@b.c");
        User userManager = userApi.createUser("Manager", "password", "John Doe", "b@b.ca");
        User userAdmin = userApi.createUser("Admin", "password", "John Doe", "c@b.c");
        User userNonmember = userApi.createUser("Nonmember", "password", "John Doe", "d@b.ca");

        Group group = groupApi.createGroup("Groupname", userAdmin, "Random description");
        groupMemberHelper.createGroupMember(userMember, group, MemberRole.MEMBER);
        groupMemberHelper.createGroupMember(userManager, group, MemberRole.MANAGER);

        Assert.assertTrue(groupMemberHelper.getGroupMember(userMember, group).getRole() == MemberRole.MEMBER);
        groupApi.setManager(userAdmin, group, userMember, true);
        Assert.assertTrue(groupMemberHelper.getGroupMember(userMember, group).getRole() == MemberRole.MANAGER);

        Assert.assertTrue(groupMemberHelper.getGroupMember(userManager, group).getRole() == MemberRole.MANAGER);
        groupApi.setManager(userAdmin, group, userManager, false);
        Assert.assertTrue(groupMemberHelper.getGroupMember(userManager, group).getRole() == MemberRole.MEMBER);
    }

    @Test(expected = NotAllowedException.class)
    @Transactional
    public void setManagerOnAdmin() throws AlreadyExistsException, WrongParameterException, GroupPermissionException, NotMemberOfException, AlreadyExistsException, NotAllowedException {
        User userMember = userApi.createUser("Member", "password", "John Doe", "a@b.c");
        User userManager = userApi.createUser("Manager", "password", "John Doe", "b@b.cab");
        User userAdmin = userApi.createUser("Admin", "password", "John Doe", "c@b.c");
        User userNonmember = userApi.createUser("Nonmember", "password", "John Doe", "d@b.ca");

        Group group = groupApi.createGroup("Groupname", userAdmin, "Random description");
        groupMemberHelper.createGroupMember(userMember, group, MemberRole.MEMBER);
        groupMemberHelper.createGroupMember(userManager, group, MemberRole.MANAGER);

        groupApi.setManager(userAdmin, group, userAdmin, true);
    }

    @Test
    @Transactional
    public void addMember() throws AlreadyExistsException, WrongParameterException, GroupPermissionException, NotMemberOfException, AlreadyExistsException {
        User userToBeMember = userApi.createUser("Member", "password", "John Doe", "a@b.c");
        User userAdmin = userApi.createUser("Admin", "password", "John Doe", "c@b.c");
        Group group = groupApi.createGroup("Groupname", userAdmin, "Random description");

        List<Group> groupList = new ArrayList<>();
        groupList.add(group);

        Task task = taskApi.createTask(userAdmin, "TestTask", null, null, 0, groupList, LocalDateTime.now().plusDays(10));
        Task task2 = taskApi.createTask(userAdmin, "TestTask2", null, null, 0, groupList, LocalDateTime.now().plusDays(101));
        Task task3 = taskApi.createTask(userToBeMember, "TestTask3", null, null, 0, null, LocalDateTime.now().plusDays(102));

        Assert.assertEquals(userAdmin.getTasksOfUser().size(), 2);
        Assert.assertEquals(userToBeMember.getTasksOfUser().size(), 1);
        Assert.assertEquals(group.getSharedTasks().size(), 2);

        groupApi.addMember(userAdmin, group, userToBeMember);
        Assert.assertEquals(userAdmin.getTasksOfUser().size(), 2);
        Assert.assertEquals(userToBeMember.getTasksOfUser().size(), 3);
        Assert.assertEquals(group.getSharedTasks().size(), 2);
    }


    @Test
    @Transactional
    public void addAndRemoveMember() throws AlreadyExistsException, WrongParameterException, GroupPermissionException, NotMemberOfException, AlreadyExistsException, NotAllowedException {
        /**
         * Scenario:
         * 1) Have some tests and a group with admin
         * 2) Share tests with group
         * 3) Add userMember to group, check if group's jobs are shared with him
         * 4) Add userMember2 to group, check if group's jobs are shared with him
         * 5) Remove userMember from group, check if jobs only from group are removed (including TaskParticipant) and that jobs that were both solo and from group are not removed
         * 6) Remove userMember2 from group, same as 5)
         */
        User userMember = userApi.createUser("Member", "password", "John Doe", "a@b.c");
        User userMember2 = userApi.createUser("Member2", "password", "John Doe", "a@b.c");
        User userAdmin = userApi.createUser("Admin", "password", "John Doe", "c@b.c");
        Group group = groupApi.createGroup("Groupname", userAdmin, "Random description");

        Task task = taskApi.createTask(userMember, "TestTask", null, null, 0, LocalDateTime.now().plusDays(10));
        Task task2 = taskApi.createTask(userMember, "TestTask2", null, null, 0, LocalDateTime.now().plusDays(101));
        Task task3 = taskApi.createTask(userMember, "TestTask3", null, null, 0, LocalDateTime.now().plusDays(101));
        Task task4 = taskApi.createTask(userMember2, "TestTask4", null, null, 0, LocalDateTime.now().plusDays(101));
        Task task5 = taskApi.createTask(userAdmin, "TestTask5", null, null, 0, LocalDateTime.now().plusDays(102));
        Task task6 = taskApi.createTask(userAdmin, "TestTask6", null, null, 0, LocalDateTime.now().plusDays(102));

        Assert.assertTrue(task2.getUsersOnTask().size() == 1);

        groupApi.addTask(userAdmin, group, task2);
        groupApi.addTask(userAdmin, group, task3);
        groupApi.addTask(userAdmin, group, task5);

        /**
         * (s) - solo | (g) - group
         * group: task2, task3, task5 (members: admin)
         * member: task(s), task2(s), task3(s)
         * member2: task4(s)
         * admin: task5(sg), task6, task2(g) task3(g)
         */
        Assert.assertTrue(userMember.getTasksOfUser().size() == 3);
        Assert.assertTrue(userMember.getTasksOfUser().contains(task));
        Assert.assertTrue(userMember.getTasksOfUser().contains(task2));
        Assert.assertTrue(userMember.getTasksOfUser().contains(task3));
        Assert.assertNotNull(taskParticipantDAO.findByUserAndTask(userMember, task2)); // test if solo-TaskParticipant will be preserved after user is removed from group
        Assert.assertNull(taskParticipantDAO.findByUserAndTask(userMember, task5)); // test if taskParticipant will be created and then destroyed when user joins/leaves the group

        Assert.assertTrue(userMember2.getTasksOfUser().size() == 1);
        Assert.assertTrue(userMember2.getTasksOfUser().contains(task4));

        Assert.assertTrue(userAdmin.getTasksOfUser().size() == 4);
        Assert.assertTrue(userAdmin.getTasksOfUser().contains(task5));
        Assert.assertTrue(userAdmin.getTasksOfUser().contains(task6));
        Assert.assertTrue(userAdmin.getTasksOfUser().contains(task2));
        Assert.assertTrue(userAdmin.getTasksOfUser().contains(task3));

        Assert.assertTrue(task2.getUsersOnTask().size() == 2);

        Assert.assertEquals(group.getGroupMembers().size(), 1);

        Assert.assertEquals(userAdmin.getMemberAs().size(), 1);
        Assert.assertEquals(userMember.getMemberAs().size(), 0);
        Assert.assertEquals(userMember2.getMemberAs().size(), 0);

        //
        groupApi.addMember(userAdmin, group, userMember);
        /**
         * (s) - solo | (g) - group
         * group: task2, task3, task5 (members: admin, member)
         * member: task(s), task2(sg), task3(sg), task5(g)
         * member2: task4(s)
         * admin: task5(sg), task6, task2(g) task3(g)
         */
        Assert.assertTrue(userMember.getTasksOfUser().size() == 4);
        Assert.assertTrue(userMember.getTasksOfUser().contains(task));
        Assert.assertTrue(userMember.getTasksOfUser().contains(task2));
        Assert.assertTrue(userMember.getTasksOfUser().contains(task3));
        Assert.assertTrue(userMember.getTasksOfUser().contains(task5));
        Assert.assertNotNull(taskParticipantDAO.findByUserAndTask(userMember, task2)); // test if solo-TaskParticipant will be preserved after user is removed from group
        Assert.assertNotNull(taskParticipantDAO.findByUserAndTask(userMember, task5)); // test if taskParticipant will be created and then destroyed when user joins/leaves the group

        Assert.assertTrue(userMember2.getTasksOfUser().size() == 1);
        Assert.assertTrue(userMember2.getTasksOfUser().contains(task4));
        Assert.assertNotNull(taskParticipantDAO.findByUserAndTask(userMember2, task4)); // test if taskParticipant will be created and then destroyed when user joins/leaves the group
        Assert.assertNull(taskParticipantDAO.findByUserAndTask(userMember2, task2)); // test if taskParticipant will be created and then destroyed when user joins/leaves the group

        Assert.assertTrue(userAdmin.getTasksOfUser().size() == 4);
        Assert.assertTrue(userAdmin.getTasksOfUser().contains(task5));
        Assert.assertTrue(userAdmin.getTasksOfUser().contains(task6));
        Assert.assertTrue(userAdmin.getTasksOfUser().contains(task2));
        Assert.assertTrue(userAdmin.getTasksOfUser().contains(task3));

        Assert.assertTrue(task2.getUsersOnTask().size() == 2);

        Assert.assertEquals(group.getGroupMembers().size(), 2);

        Assert.assertEquals(userAdmin.getMemberAs().size(), 1);
        Assert.assertEquals(userMember.getMemberAs().size(), 1);
        Assert.assertEquals(userMember2.getMemberAs().size(), 0);


        groupApi.addMember(userAdmin, group, userMember2);
        /**
         * (s) - solo | (g) - group
         * group: task2, task3, task5 (members: admin, member, member2)
         * member: task(s), task2(sg), task3(sg), task5(g)
         * member2: task4(s), task2(g), task3(g), task5(g)
         * admin: task5(sg), task6, task2(g) task3(g)
         */
        Assert.assertTrue(userMember.getTasksOfUser().size() == 4);
        Assert.assertTrue(userMember.getTasksOfUser().contains(task));
        Assert.assertTrue(userMember.getTasksOfUser().contains(task2));
        Assert.assertTrue(userMember.getTasksOfUser().contains(task3));
        Assert.assertTrue(userMember.getTasksOfUser().contains(task5));
        Assert.assertNotNull(taskParticipantDAO.findByUserAndTask(userMember, task2)); // test if solo-TaskParticipant will be preserved after user is removed from group
        Assert.assertNotNull(taskParticipantDAO.findByUserAndTask(userMember, task5)); // test if taskParticipant will be created and then destroyed when user joins/leaves the group

        Assert.assertTrue(userMember2.getTasksOfUser().size() == 4);
        Assert.assertTrue(userMember2.getTasksOfUser().contains(task4));
        Assert.assertTrue(userMember2.getTasksOfUser().contains(task2));
        Assert.assertTrue(userMember2.getTasksOfUser().contains(task3));
        Assert.assertTrue(userMember2.getTasksOfUser().contains(task5));
        Assert.assertNotNull(taskParticipantDAO.findByUserAndTask(userMember2, task4)); // test if taskParticipant will be created and then destroyed when user joins/leaves the group
        Assert.assertNotNull(taskParticipantDAO.findByUserAndTask(userMember2, task2)); // test if taskParticipant will be created and then destroyed when user joins/leaves the group


        Assert.assertTrue(userAdmin.getTasksOfUser().size() == 4);
        Assert.assertTrue(userAdmin.getTasksOfUser().contains(task5));
        Assert.assertTrue(userAdmin.getTasksOfUser().contains(task6));
        Assert.assertTrue(userAdmin.getTasksOfUser().contains(task2));
        Assert.assertTrue(userAdmin.getTasksOfUser().contains(task3));

        Assert.assertTrue(task2.getUsersOnTask().size() == 3);

        Assert.assertEquals(group.getGroupMembers().size(), 3);

        Assert.assertEquals(userAdmin.getMemberAs().size(), 1);
        Assert.assertEquals(userMember.getMemberAs().size(), 1);
        Assert.assertEquals(userMember2.getMemberAs().size(), 1);

        //
        groupApi.removeMember(userAdmin, group, userMember);
        /**
         * (s) - solo | (g) - group
         * group: task2, task3, task5 (members: admin, member2)
         * member: task(s), task2(s), task3(s)
         * member2: task4(s), task2(g), task3(g), task5(g)
         * admin: task5(sg), task6, task2(g) task3(g)
         */
        Assert.assertTrue(userMember.getTasksOfUser().size() == 3);
        Assert.assertTrue(userMember.getTasksOfUser().contains(task));
        Assert.assertTrue(userMember.getTasksOfUser().contains(task2));
        Assert.assertTrue(userMember.getTasksOfUser().contains(task3));
        Assert.assertNotNull(taskParticipantDAO.findByUserAndTask(userMember, task2)); // test if solo-TaskParticipant will be preserved after user is removed from group
        Assert.assertNull(taskParticipantDAO.findByUserAndTask(userMember, task5)); // test if taskParticipant will be created and then destroyed when user joins/leaves the group

        Assert.assertTrue(userMember2.getTasksOfUser().size() == 4);
        Assert.assertTrue(userMember2.getTasksOfUser().contains(task4));
        Assert.assertTrue(userMember2.getTasksOfUser().contains(task2));
        Assert.assertTrue(userMember2.getTasksOfUser().contains(task3));
        Assert.assertTrue(userMember2.getTasksOfUser().contains(task5));
        Assert.assertNotNull(taskParticipantDAO.findByUserAndTask(userMember2, task4)); // test if taskParticipant will be created and then destroyed when user joins/leaves the group
        Assert.assertNotNull(taskParticipantDAO.findByUserAndTask(userMember2, task2)); // test if taskParticipant will be created and then destroyed when user joins/leaves the group

        Assert.assertTrue(userAdmin.getTasksOfUser().size() == 4);
        Assert.assertTrue(userAdmin.getTasksOfUser().contains(task5));
        Assert.assertTrue(userAdmin.getTasksOfUser().contains(task6));
        Assert.assertTrue(userAdmin.getTasksOfUser().contains(task2));
        Assert.assertTrue(userAdmin.getTasksOfUser().contains(task3));

        Assert.assertTrue(task2.getUsersOnTask().size() == 3);
        Assert.assertEquals(group.getGroupMembers().size(), 2);

        Assert.assertEquals(userAdmin.getMemberAs().size(), 1);
        Assert.assertEquals(userMember.getMemberAs().size(), 0);
        Assert.assertEquals(userMember2.getMemberAs().size(), 1);

        //
        groupApi.removeMember(userAdmin, group, userMember2);
        /**
         * (s) - solo | (g) - group
         * group: task2, task3, task5 (members: admin)
         * member: task(s), task2(s), task3(s)
         * member2: task4(s)
         * admin: task5(sg), task6, task2(g) task3(g)
         */
        Assert.assertTrue(userMember.getTasksOfUser().size() == 3);
        Assert.assertTrue(userMember.getTasksOfUser().contains(task));
        Assert.assertTrue(userMember.getTasksOfUser().contains(task2));
        Assert.assertTrue(userMember.getTasksOfUser().contains(task3));
        Assert.assertNotNull(taskParticipantDAO.findByUserAndTask(userMember, task2)); // test if solo-TaskParticipant will be preserved after user is removed from group
        Assert.assertNull(taskParticipantDAO.findByUserAndTask(userMember, task5)); // test if taskParticipant will be created and then destroyed when user joins/leaves the group

        Assert.assertTrue(userMember2.getTasksOfUser().size() == 1);
        Assert.assertTrue(userMember2.getTasksOfUser().contains(task4));
        Assert.assertNotNull(taskParticipantDAO.findByUserAndTask(userMember2, task4)); // test if taskParticipant will be created and then destroyed when user joins/leaves the group
        Assert.assertNull(taskParticipantDAO.findByUserAndTask(userMember2, task2)); // test if taskParticipant will be created and then destroyed when user joins/leaves the group

        Assert.assertTrue(userAdmin.getTasksOfUser().size() == 4);
        Assert.assertTrue(userAdmin.getTasksOfUser().contains(task5));
        Assert.assertTrue(userAdmin.getTasksOfUser().contains(task6));
        Assert.assertTrue(userAdmin.getTasksOfUser().contains(task2));
        Assert.assertTrue(userAdmin.getTasksOfUser().contains(task3));

        Assert.assertTrue(task2.getUsersOnTask().size() == 2);
        Assert.assertEquals(group.getGroupMembers().size(), 1);

        Assert.assertEquals(userAdmin.getMemberAs().size(), 1);
        Assert.assertEquals(userMember.getMemberAs().size(), 0);
        Assert.assertEquals(userMember2.getMemberAs().size(), 0);
    }

    @Test
    @Transactional
    public void addTask() throws AlreadyExistsException, WrongParameterException, GroupPermissionException, NotMemberOfException, AlreadyExistsException {
        User userNonMember = userApi.createUser("Member", "password", "John Doe", "a@b.c");
        User userAdmin = userApi.createUser("Admin", "password", "John Doe", "c@b.c");
        Group group = groupApi.createGroup("Groupname", userAdmin, "Random description");

        Task task = taskApi.createTask(userNonMember, "TestTask", null, null, 0, LocalDateTime.now().plusDays(10));
        Task task2 = taskApi.createTask(userNonMember, "TestTask2", null, null, 0, LocalDateTime.now().plusDays(101));
        Task task3 = taskApi.createTask(userAdmin, "TestTask3", null, null, 0, LocalDateTime.now().plusDays(102));

        Assert.assertEquals(group.getSharedTasks().size(), 0);
        Assert.assertEquals(userNonMember.getTasksOfUser().size(), 2);
        Assert.assertEquals(userAdmin.getTasksOfUser().size(), 1);

        groupApi.addTask(userAdmin, group, task);
        Assert.assertEquals(group.getSharedTasks().size(), 1);
        Assert.assertEquals(userNonMember.getTasksOfUser().size(), 2);
        Assert.assertEquals(userAdmin.getTasksOfUser().size(), 2);

        groupApi.addTask(userAdmin, group, task2);
        Assert.assertEquals(group.getSharedTasks().size(), 2);
        Assert.assertEquals(userNonMember.getTasksOfUser().size(), 2);
        Assert.assertEquals(userAdmin.getTasksOfUser().size(), 3);

        groupApi.addTask(userAdmin, group, task3);
        Assert.assertEquals(group.getSharedTasks().size(), 3);
        Assert.assertEquals(userNonMember.getTasksOfUser().size(), 2);
        Assert.assertEquals(userAdmin.getTasksOfUser().size(), 3);
    }

    @Test(expected = AlreadyExistsException.class)
    @Transactional
    public void addTask_Twice() throws AlreadyExistsException, WrongParameterException, GroupPermissionException, NotMemberOfException, AlreadyExistsException {
        User userAdmin = userApi.createUser("Admin", "password", "John Doe", "c@b.c");
        Group group = groupApi.createGroup("Groupname", userAdmin, "Random description");

        Task task = taskApi.createTask(userAdmin, "TestTask", null, null, 0, LocalDateTime.now().plusDays(10));
        groupApi.addTask(userAdmin, group, task);
        groupApi.addTask(userAdmin, group, task);
    }

    @Test
    @Transactional
    public void leaveTask() throws AlreadyExistsException, WrongParameterException, GroupPermissionException, NotMemberOfException, AlreadyExistsException {
        User userMember = userApi.createUser("Member", "password", "John Doe", "a@b.c");
        User userAdmin = userApi.createUser("Admin", "password", "John Doe", "c@b.c");
        Group group = groupApi.createGroup("Groupname", userAdmin, "Random description");

        Task task = taskApi.createTask(userMember, "TestTask", null, null, 0, LocalDateTime.now().plusDays(10));
        Task task2 = taskApi.createTask(userMember, "TestTask2", null, null, 0, LocalDateTime.now().plusDays(101));
        Task task3 = taskApi.createTask(userAdmin, "TestTask3", null, null, 0, LocalDateTime.now().plusDays(102));
        groupApi.addMember(userAdmin, group, userMember);


        Assert.assertTrue(task.getUsersOnTask().size() == 1); // userMember
        Assert.assertTrue(group.getSharedTasks().size() == 0); // No shared task at start
        Assert.assertTrue(userMember.getTasksOfUser().size() == 2);
        Assert.assertTrue(userAdmin.getTasksOfUser().size() == 1);
        Assert.assertNull(taskParticipantDAO.findByUserAndTask(userAdmin, task));

        groupApi.addTask(userAdmin, group, task);

        Assert.assertTrue(task.getUsersOnTask().size() == 2); // userMember, admin
        Assert.assertTrue(group.getSharedTasks().size() == 1);
        Assert.assertTrue(userMember.getTasksOfUser().size() == 2);
        Assert.assertTrue(userAdmin.getTasksOfUser().size() == 2);
        Assert.assertNotNull(taskParticipantDAO.findByUserAndTask(userAdmin, task));

        groupApi.leaveTask(userAdmin, group, task);

        Assert.assertTrue(task.getUsersOnTask().size() == 1); // userMember
        Assert.assertTrue(group.getSharedTasks().size() == 0);
        Assert.assertTrue(userMember.getTasksOfUser().size() == 2);
        Assert.assertTrue(userAdmin.getTasksOfUser().size() == 1);
        Assert.assertNull(taskParticipantDAO.findByUserAndTask(userAdmin, task));
    }

    @Test
    @Transactional
    public void editDetails() throws AlreadyExistsException, WrongParameterException, GroupPermissionException, NotMemberOfException, AlreadyExistsException {
        User userAdmin = userApi.createUser("Admin", "password", "John Doe", "c@b.c");
        Group group = groupApi.createGroup("Groupname", userAdmin, "Random description");

        Assert.assertEquals(group.getDescription(), "Random description");
        groupApi.editDetails(userAdmin, group, "New thing");
        Assert.assertEquals(group.getDescription(), "New thing");
    }

    @Test
    @Transactional
    public void changeAdminUserNotMember() throws AlreadyExistsException, WrongParameterException, GroupPermissionException, NotMemberOfException, AlreadyExistsException {
        User userAdmin = userApi.createUser("Admin", "password", "John Doe", "c@b.ca");
        User newAdmin = userApi.createUser("NewAdmin", "password", "John Doe", "c@b.c");
        Group group = groupApi.createGroup("Groupname", userAdmin, "Random description");

        groupApi.editDetails(userAdmin, group, "New thing"); // Check if it passes just in case

        expectedException.expect(NotMemberOfException.class);
        groupApi.changeAdmin(userAdmin, group, newAdmin);
    }

    @Test
    @Transactional
    public void changeAdminOkFormerAdminFails() throws AlreadyExistsException, WrongParameterException, GroupPermissionException, NotMemberOfException, AlreadyExistsException {
        User userAdmin = userApi.createUser("Admin", "password", "John Doe", "c@b.ca");
        User newAdmin = userApi.createUser("NewAdmin", "password", "John Doe", "c@b.ca");
        Group group = groupApi.createGroup("Groupname", userAdmin, "Random description");

        groupApi.addMember(userAdmin, group, newAdmin);
        groupApi.editDetails(userAdmin, group, "New thing"); // Check if it passes just in case

        groupApi.changeAdmin(userAdmin, group, newAdmin);

        groupApi.editDetails(newAdmin, group, "Yet another");
        Assert.assertEquals(group.getDescription(), "Yet another");

        expectedException.expect(GroupPermissionException.class);
        groupApi.editDetails(userAdmin, group, "New thing");
    }

    @Test
    @Transactional
    public void deleteGroup() throws AlreadyExistsException, WrongParameterException, GroupPermissionException, NotMemberOfException, AlreadyExistsException, NotAllowedException {
        User userMember = userApi.createUser("Member", "password", "John Doe", "a@b.c");
        User userMember2 = userApi.createUser("Member2", "password", "John Doe", "a@b.c");
        User userAdmin = userApi.createUser("Admin", "password", "John Doe", "c@b.c");
        Group group = groupApi.createGroup("Groupname", userAdmin, "Random description");
        Group toDeleteGroup = groupApi.createGroup("GroupnameToDelete", userAdmin, "Random description");

        Task task = taskApi.createTask(userMember, "TestTask", null, null, 0, LocalDateTime.now().plusDays(10));
        Task task2 = taskApi.createTask(userMember, "TestTask2", null, null, 0, LocalDateTime.now().plusDays(101));
        Task task3 = taskApi.createTask(userAdmin, "TestTask3", null, null, 0, LocalDateTime.now().plusDays(102));

        groupApi.addTask(userAdmin, toDeleteGroup, task);

        groupApi.addMember(userAdmin, group, userMember);
        groupApi.addMember(userAdmin, toDeleteGroup, userMember2);

        /**
         * group: admin, usermember |
         * todelgroup: admin, usermember2 | task
         *
         * task: usermember, admin(g), usermember2(g)
         */
        Assert.assertEquals(userAdmin.getMemberAs().size(), 2);
        Assert.assertEquals(userMember.getMemberAs().size(), 1);
        Assert.assertEquals(userMember2.getMemberAs().size(), 1);

        Assert.assertEquals(userAdmin.getTasksOfUser().size(), 2);
        Assert.assertEquals(userMember.getTasksOfUser().size(), 2);
        Assert.assertEquals(userMember2.getTasksOfUser().size(), 1);

        Assert.assertNotNull(groupDAO.findByName("GroupnameToDelete"));

        groupApi.deleteGroup(userAdmin, toDeleteGroup);


        Assert.assertEquals(userAdmin.getMemberAs().size(), 1);
        Assert.assertEquals(userMember.getMemberAs().size(), 1);
        Assert.assertEquals(userMember2.getMemberAs().size(), 0);

        Assert.assertEquals(userAdmin.getTasksOfUser().size(), 1);
        Assert.assertEquals(userMember.getTasksOfUser().size(), 2);
        Assert.assertEquals(userMember2.getTasksOfUser().size(), 0);

        Assert.assertNull(groupDAO.findByName("GroupnameToDelete"));
    }

    @Test
    @Transactional
    public void deleteGroup_OffersDeleted() throws AlreadyExistsException, WrongParameterException, GroupPermissionException, NotMemberOfException, AlreadyExistsException, NotAllowedException {
        User userMember = userApi.createUser("Member", "password", "John Doe", "a@b.c");
        User userAdmin = userApi.createUser("Admin", "password", "John Doe", "c@b.c");
        Group group = groupApi.createGroup("Groupname", userAdmin, "Random description");

        Task task = taskApi.createTask(userMember, "TestTask", null, null, 0, LocalDateTime.now().plusDays(10));

        Assert.assertEquals(0, groupTaskSharingDao.findByOfferedTo(group).size());
        Assert.assertEquals(0, membershipSharingDao.findByOfferedTo(userMember).size());

        sharingApi.offerMembership(userAdmin, group, userMember);
        sharingApi.offerTaskSharing(userMember, task, group);

        Assert.assertEquals(1, groupTaskSharingDao.findByOfferedTo(group).size());
        Assert.assertEquals(1, membershipSharingDao.findByOfferedTo(userMember).size());

        groupApi.deleteGroup(userAdmin, group);

        Assert.assertEquals(0, groupTaskSharingDao.findAll().size());
        Assert.assertEquals(0, membershipSharingDao.findAll().size());

        Assert.assertNull(groupDAO.findByName("Groupname"));
    }

    @Test
    @Transactional
    public void listGroups() throws AlreadyExistsException, WrongParameterException, GroupPermissionException, NotMemberOfException, AlreadyExistsException {
        User userMember = userApi.createUser("Member", "password", "John Doe", "a@b.c");
        User userMember2 = userApi.createUser("Member2", "password", "John Doe", "a@b.c");
        User userAdmin = userApi.createUser("Admin", "password", "John Doe", "c@b.c");

        for (Group group: groupApi.listGroups()) System.out.println(group);

        Assert.assertEquals(groupApi.listGroups().size(), 0);
        Assert.assertEquals(groupApi.listGroups(userMember).size(), 0);
        Assert.assertEquals(groupApi.listGroups(userMember2).size(), 0);
        Assert.assertEquals(groupApi.listGroups(userAdmin).size(), 0);

        Group group = groupApi.createGroup("Groupname", userAdmin, "Random description");
        Group group2 = groupApi.createGroup("GroupnameToDelete", userAdmin, "Random description");

        Assert.assertEquals(groupApi.listGroups().size(), 2);
        Assert.assertEquals(groupApi.listGroups(userMember).size(), 0);
        Assert.assertEquals(groupApi.listGroups(userMember2).size(), 0);
        Assert.assertEquals(groupApi.listGroups(userAdmin).size(), 2);

        groupApi.addMember(userAdmin, group, userMember);

        Assert.assertEquals(groupApi.listGroups().size(), 2);
        Assert.assertEquals(groupApi.listGroups(userMember).size(), 1);
        Assert.assertEquals(groupApi.listGroups(userMember2).size(), 0);
        Assert.assertEquals(groupApi.listGroups(userAdmin).size(), 2);

        groupApi.addMember(userAdmin, group2, userMember);

        Assert.assertEquals(groupApi.listGroups().size(), 2);
        Assert.assertEquals(groupApi.listGroups(userMember).size(), 2);
        Assert.assertEquals(groupApi.listGroups(userMember2).size(), 0);
        Assert.assertEquals(groupApi.listGroups(userAdmin).size(), 2);

        groupApi.addMember(userAdmin, group, userMember2);

        Assert.assertEquals(groupApi.listGroups().size(), 2);
        Assert.assertEquals(groupApi.listGroups(userMember).size(), 2);
        Assert.assertEquals(groupApi.listGroups(userMember2).size(), 1);
        Assert.assertEquals(groupApi.listGroups(userAdmin).size(), 2);
    }

    @Test
    @Transactional
    public void findByMembers_UserAndRole() throws Exception {
        User user1 = userApi.createUser("User1", "pwd", null, null);
        User user2 = userApi.createUser("User2", "pwd", null, null);
        User user3 = userApi.createUser("User3", "pwd", null, null);
        User user4 = userApi.createUser("User4", "pwd", null, null);
        User user5 = userApi.createUser("User5", "pwd", null, null);

        Group group1 = groupApi.createGroup("AGroup", user1, null);
        Group group2 = groupApi.createGroup("BGroup", user1, null);
        Group group3 = groupApi.createGroup("CGroup", user2, null);
        Group group4 = groupApi.createGroup("DGroup", user3, null);
        Group group5 = groupApi.createGroup("EGroup", user3, null);

        /**
         *            ADMIN     |      MANAGER       |     MEMBER
         * user1:     1, 2      |        4           |      3, 5
         * user2:     3         |                    |      1, 2, 4
         * user3:     4, 5      |       1, 2, 3      |
         * user4:               |       1            |      3, 4
         * user5:               |                    |      2
         */
        groupApi.addMember(user1, group1, user2);
        groupApi.addMember(user1, group1, user3);
        groupApi.addMember(user1, group1, user4);
        groupApi.setManager(user1, group1, user3, true);
        groupApi.setManager(user1, group1, user4, true);

        groupApi.addMember(user1, group2, user5);
        groupApi.addMember(user1, group2, user3);
        groupApi.addMember(user1, group2, user2);
        groupApi.addMember(user1, group2, user4);
        groupApi.setManager(user1, group2, user3, true);

        groupApi.addMember(user2, group3, user1);
        groupApi.addMember(user2, group3, user3);
        groupApi.addMember(user2, group3, user4);
        groupApi.setManager(user2, group3, user3, true);

        groupApi.addMember(user3, group4, user1);
        groupApi.addMember(user3, group4, user2);
        groupApi.setManager(user3, group4, user1, true);

        groupApi.addMember(user3, group5, user1);

        Assert.assertEquals(2, groupApi.listGroups(user1, MemberRole.ADMIN).size());
        Assert.assertEquals(1, groupApi.listGroups(user1, MemberRole.MANAGER).size());
        Assert.assertEquals(2, groupApi.listGroups(user1, MemberRole.MEMBER).size());

        Assert.assertEquals(1, groupApi.listGroups(user2, MemberRole.ADMIN).size());
        Assert.assertEquals(0, groupApi.listGroups(user2, MemberRole.MANAGER).size());
        Assert.assertEquals(3, groupApi.listGroups(user2, MemberRole.MEMBER).size());

        Assert.assertEquals(2, groupApi.listGroups(user3, MemberRole.ADMIN).size());
        Assert.assertEquals(3, groupApi.listGroups(user3, MemberRole.MANAGER).size());
        Assert.assertEquals(0, groupApi.listGroups(user3, MemberRole.MEMBER).size());

        Assert.assertEquals(0, groupApi.listGroups(user4, MemberRole.ADMIN).size());
        Assert.assertEquals(1, groupApi.listGroups(user4, MemberRole.MANAGER).size());
        Assert.assertEquals(2, groupApi.listGroups(user4, MemberRole.MEMBER).size());

        Assert.assertEquals(0, groupApi.listGroups(user5, MemberRole.ADMIN).size());
        Assert.assertEquals(0, groupApi.listGroups(user5, MemberRole.MANAGER).size());
        Assert.assertEquals(1, groupApi.listGroups(user5, MemberRole.MEMBER).size());
    }
}


