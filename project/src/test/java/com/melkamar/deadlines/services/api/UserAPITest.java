package com.melkamar.deadlines.services.api;

import com.melkamar.deadlines.DeadlinesApplication;
import com.melkamar.deadlines.dao.taskparticipant.TaskParticipantDAO;
import com.melkamar.deadlines.dao.taskparticipant.TaskParticipantDAOHibernate;
import com.melkamar.deadlines.dao.user.UserDAO;
import com.melkamar.deadlines.exceptions.*;
import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.task.Priority;
import com.melkamar.deadlines.model.task.Task;
import com.melkamar.deadlines.services.security.Authenticator;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 26.03.2016 15:21
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DeadlinesApplication.class)
@WebAppConfiguration
public class UserAPITest {
    @Autowired
    private UserAPI userAPI;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private Authenticator authenticator;
    @Autowired
    private GroupAPI groupAPI;
    @Autowired
    private TaskAPI taskAPI;
    @Autowired
    private TaskParticipantDAOHibernate taskparticipantDAO;


    @Test(expected = WrongParameterException.class)
    @Transactional
    public void nullParameters() throws WrongParameterException, AlreadyExistsException, UserAlreadyExistsException {
        userAPI.createUser(null, null, null, null);
    }

    @Test(expected = WrongParameterException.class)
    @Transactional
    public void emptyParameters() throws WrongParameterException, AlreadyExistsException, UserAlreadyExistsException {
        userAPI.createUser("", "", "", "");
    }

    @Test
    @Transactional
    public void plainPersistence() throws WrongParameterException, AlreadyExistsException, UserAlreadyExistsException {
        User user = userAPI.createUser("User1", "password", null, null);

        Assert.assertNotNull(userDAO.findByUsername("User1"));
    }

    @Test(expected = UserAlreadyExistsException.class)
    @Transactional
    public void uniqueUsername() throws WrongParameterException, UserAlreadyExistsException {
        User user = userAPI.createUser("uniq1", "password", null, null);
        User user2 = userAPI.createUser("uniq2", "password", null, null);
        User user3 = userAPI.createUser("uniq2", "password", null, null);
    }

    @Test
    @Transactional
    public void fieldsPersistence() throws WrongParameterException, UserAlreadyExistsException {
        User user = userAPI.createUser("User2", "password", "somename", "someemail");
        User retrieved = userDAO.findByUsername("User2");

        System.out.println(user);
        System.out.println(retrieved);

        Assert.assertNotNull(retrieved);
        Assert.assertEquals(user, retrieved);
    }

    @Test
    @Transactional
    public void editUserDetails() throws WrongParameterException, UserAlreadyExistsException {
        User user = userAPI.createUser("UserDetails", "password", "somename", "someemailDetails");

        Assert.assertTrue(user.getUsername().equals("UserDetails"));
        Assert.assertTrue(user.getName().equals("somename"));
        Assert.assertTrue(user.getEmail().equals("someemailDetails"));
        Assert.assertNotNull(authenticator.authenticate(user, "password"));

        userAPI.editUserDetails(user, "NewName", null, null);
        Assert.assertTrue(user.getUsername().equals("UserDetails"));
        Assert.assertTrue(user.getName().equals("NewName"));
        Assert.assertTrue(user.getEmail().equals("someemailDetails"));
        Assert.assertNotNull(authenticator.authenticate(user, "password"));

        userAPI.editUserDetails(user, null, "newemail", null);
        Assert.assertTrue(user.getUsername().equals("UserDetails"));
        Assert.assertTrue(user.getName().equals("NewName"));
        Assert.assertTrue(user.getEmail().equals("newemail"));
        Assert.assertNotNull(authenticator.authenticate(user, "password"));

        userAPI.editUserDetails(user, null, null, "newpassword");
        Assert.assertTrue(user.getUsername().equals("UserDetails"));
        Assert.assertTrue(user.getName().equals("NewName"));
        Assert.assertTrue(user.getEmail().equals("newemail"));
        Assert.assertNotNull(authenticator.authenticate(user, "newpassword"));
        Assert.assertNull(authenticator.authenticate(user, "password"));
    }

    @Test
    @Transactional
    public void listUsers() throws WrongParameterException, UserAlreadyExistsException {
        int size = userAPI.listUsers().size();

        Assert.assertEquals(userAPI.listUsers().size(), size);
        userAPI.createUser("someuser" + size, "password", "somename", "someemail" + size);
        size++;

        Assert.assertEquals(userAPI.listUsers().size(), size);
        userAPI.createUser("someuser" + size, "password", "somename", "someemail" + size);
        size++;

        Assert.assertEquals(userAPI.listUsers().size(), size);
        userAPI.createUser("someuser" + size, "password", "somename", "someemail" + size);
        size++;
    }

    @Test
    @Transactional
    public void getGroupsOfUser() throws WrongParameterException, UserAlreadyExistsException {
        int groupsSize = 0;

        User user = userAPI.createUser("someuser", "password", "somename", "someemail");
        User anotherUser = userAPI.createUser("anotherUser", "password", "somename", "someemail2");
        Assert.assertEquals(userAPI.getGroupsOfUser(user).size(), groupsSize);

        Group groupA = groupAPI.createGroup("group" + groupsSize, user, "desc");
        groupsSize++;
        Assert.assertEquals(userAPI.getGroupsOfUser(user).size(), groupsSize);
        Assert.assertEquals(userAPI.getGroupsOfUser(anotherUser).size(), 0);


        Group groupB = groupAPI.createGroup("group" + groupsSize, user, "desc");
        groupsSize++;
        Assert.assertEquals(userAPI.getGroupsOfUser(user).size(), groupsSize);
        Assert.assertEquals(userAPI.getGroupsOfUser(anotherUser).size(), 0);

        Group groupC = groupAPI.createGroup("group" + groupsSize, anotherUser, "desc");
        Assert.assertEquals(userAPI.getGroupsOfUser(user).size(), groupsSize);
        Assert.assertEquals(userAPI.getGroupsOfUser(anotherUser).size(), 1);


        Assert.assertTrue(userAPI.getGroupsOfUser(user).contains(groupA));
        Assert.assertTrue(userAPI.getGroupsOfUser(user).contains(groupB));
        Assert.assertFalse(userAPI.getGroupsOfUser(user).contains(groupC));

        Assert.assertFalse(userAPI.getGroupsOfUser(anotherUser).contains(groupA));
        Assert.assertFalse(userAPI.getGroupsOfUser(anotherUser).contains(groupB));
        Assert.assertTrue(userAPI.getGroupsOfUser(anotherUser).contains(groupC));
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    @Transactional
    public void leaveTask() throws WrongParameterException, NotMemberOfException, UserAlreadyExistsException {
        User user = userAPI.createUser("someuser", "password", "somename", "someemail");
        User anotherUser = userAPI.createUser("anotherUser", "password", "somename", "someemail2");

        Assert.assertEquals(0, user.tasksOfUser().size());
        Assert.assertEquals(0, anotherUser.tasksOfUser().size());

        Task task1 = taskAPI.createTask(user, "Task1", null, Priority.NORMAL, 10, LocalDateTime.now().plusDays(5));
        Task task2 = taskAPI.createTask(user, "Task2", null, Priority.NORMAL, 10, LocalDateTime.now().plusDays(5));

        Assert.assertEquals(2, user.tasksOfUser().size());
        Assert.assertEquals(0, anotherUser.tasksOfUser().size());
        Assert.assertNotNull(taskparticipantDAO.findByUserAndTask(user, task1));
        Assert.assertNotNull(taskparticipantDAO.findByUserAndTask(user, task2));
        Assert.assertEquals(2, taskparticipantDAO.findByUser(user).size());
        Assert.assertEquals(0, taskparticipantDAO.findByUser(anotherUser).size());

        userAPI.leaveTask(user ,task2);

        Assert.assertEquals(1, user.tasksOfUser().size());
        Assert.assertEquals(0, anotherUser.tasksOfUser().size());
        Assert.assertNotNull(taskparticipantDAO.findByUserAndTask(user, task1));
        Assert.assertNull(taskparticipantDAO.findByUserAndTask(user, task2));
        Assert.assertEquals(1, taskparticipantDAO.findByUser(user).size());
        Assert.assertEquals(0, taskparticipantDAO.findByUser(anotherUser).size());

        expectedException.expect(NotMemberOfException.class);
        userAPI.leaveTask(anotherUser, task1);
    }

    @Test
    @Transactional
    public void leaveGroup() throws WrongParameterException, GroupPermissionException, NotMemberOfException, AlreadyExistsException, NotAllowedException, UserAlreadyExistsException {
        User userMember = userAPI.createUser("Member", "password", "John Doe", "a@b.c");
        User userAdmin = userAPI.createUser("Admin", "password", "John Doe", "c@b.c");

        Group group = groupAPI.createGroup("Groupname", userAdmin, "Random description");
        Group group2 = groupAPI.createGroup("Groupname2", userAdmin, "Random description");

        Assert.assertEquals(0, userMember.groupsOfUserAsList().size());
        Assert.assertEquals(1, group.getGroupMembers().size());
        Assert.assertEquals(1, group2.getGroupMembers().size());

        groupAPI.addMember(userAdmin, group, userMember);
        groupAPI.addMember(userAdmin, group2, userMember);

        Assert.assertEquals(2, userMember.groupsOfUserAsList().size());
        Assert.assertEquals(2, group.getGroupMembers().size());
        Assert.assertEquals(2, group2.getGroupMembers().size());

        userAPI.leaveGroup(userMember, group);

        Assert.assertEquals(1, userMember.groupsOfUserAsList().size());
        Assert.assertEquals(1, group.getGroupMembers().size());
        Assert.assertEquals(2, group2.getGroupMembers().size());
    }

    @Test
    @Transactional
    public void leaveGroupAsAdmin() throws WrongParameterException, GroupPermissionException, NotMemberOfException, AlreadyExistsException, NotAllowedException, UserAlreadyExistsException {
        User userMember = userAPI.createUser("Member", "password", "John Doe", "a@b.c");
        User userAdmin = userAPI.createUser("Admin", "password", "John Doe", "c@b.c");

        Group group = groupAPI.createGroup("Groupname", userAdmin, "Random description");
        Group group2 = groupAPI.createGroup("Groupname2", userAdmin, "Random description");

        Assert.assertEquals(0, userMember.groupsOfUserAsList().size());
        Assert.assertEquals(1, group.getGroupMembers().size());
        Assert.assertEquals(1, group2.getGroupMembers().size());

        groupAPI.addMember(userAdmin, group, userMember);
        groupAPI.addMember(userAdmin, group2, userMember);

        Assert.assertEquals(2, userMember.groupsOfUserAsList().size());
        Assert.assertEquals(2, group.getGroupMembers().size());
        Assert.assertEquals(2, group2.getGroupMembers().size());

        expectedException.expect(NotAllowedException.class);
        userAPI.leaveGroup(userAdmin, group);
    }
}