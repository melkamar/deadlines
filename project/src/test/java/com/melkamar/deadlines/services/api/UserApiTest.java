package com.melkamar.deadlines.services.api;

import com.melkamar.deadlines.DeadlinesApplication;
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
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * @author Martin Melka
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DeadlinesApplication.class)
@WebAppConfiguration
public class UserApiTest {
    @Autowired
    private UserApi userApi;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private Authenticator authenticator;
    @Autowired
    private GroupApi groupApi;
    @Autowired
    private TaskApi taskApi;
    @Autowired
    private TaskParticipantDAOHibernate taskparticipantDAO;


    @Test(expected = WrongParameterException.class)
    @Transactional
    public void nullParameters() throws WrongParameterException, AlreadyExistsException, AlreadyExistsException {
        userApi.createUser(null, null, null, null);
    }

    @Test(expected = WrongParameterException.class)
    @Transactional
    public void emptyParameters() throws WrongParameterException, AlreadyExistsException, AlreadyExistsException {
        userApi.createUser("", "", "", "");
    }

    @Test
    @Transactional
    public void plainPersistence() throws WrongParameterException, AlreadyExistsException, AlreadyExistsException {
        User user = userApi.createUser("User1", "password", null, null);

        Assert.assertNotNull(userDAO.findByUsername("User1"));
    }

    @Test(expected = AlreadyExistsException.class)
    @Transactional
    public void uniqueUsername() throws WrongParameterException, AlreadyExistsException {
        User user = userApi.createUser("uniq1", "password", null, null);
        User user2 = userApi.createUser("uniq2", "password", null, null);
        User user3 = userApi.createUser("uniq2", "password", null, null);
    }

    @Test
    @Transactional
    public void fieldsPersistence() throws WrongParameterException, AlreadyExistsException {
        User user = userApi.createUser("User2", "password", "somename", "someemail@address.com");
        User retrieved = userDAO.findByUsername("User2");

        System.out.println(user);
        System.out.println(retrieved);

        Assert.assertNotNull(retrieved);
        Assert.assertEquals(user, retrieved);
    }

    @Test
    @Transactional
    public void editUserDetails() throws WrongParameterException, AlreadyExistsException {
        User user = userApi.createUser("UserDetails", "password", "somename", "someemailDetails@address.com");

        Assert.assertTrue(user.getUsername().equals("UserDetails"));
        Assert.assertTrue(user.getName().equals("somename"));
        Assert.assertTrue(user.getEmail().equals("someemailDetails@address.com"));
        Assert.assertNotNull(authenticator.authenticate(user, "password"));

        userApi.editUserDetails(user, "NewName", null, null);
        Assert.assertTrue(user.getUsername().equals("UserDetails"));
        Assert.assertTrue(user.getName().equals("NewName"));
        Assert.assertTrue(user.getEmail().equals("someemailDetails@address.com"));
        Assert.assertNotNull(authenticator.authenticate(user, "password"));

        userApi.editUserDetails(user, null, "newemail@c.a", null);
        Assert.assertTrue(user.getUsername().equals("UserDetails"));
        Assert.assertTrue(user.getName().equals("NewName"));
        Assert.assertTrue(user.getEmail().equals("newemail@c.a"));
        Assert.assertNotNull(authenticator.authenticate(user, "password"));

        userApi.editUserDetails(user, null, null, "newpassword");
        Assert.assertTrue(user.getUsername().equals("UserDetails"));
        Assert.assertTrue(user.getName().equals("NewName"));
        Assert.assertTrue(user.getEmail().equals("newemail@c.a"));
        Assert.assertNotNull(authenticator.authenticate(user, "newpassword"));
        Assert.assertNull(authenticator.authenticate(user, "password"));
    }

    @Test
    @Transactional
    public void listUsers() throws WrongParameterException, AlreadyExistsException {
        int size = userApi.listUsers().size();

        Assert.assertEquals(userApi.listUsers().size(), size);
        userApi.createUser("someuser" + size, "password", "somename", "someemail@address.com" + size);
        size++;

        Assert.assertEquals(userApi.listUsers().size(), size);
        userApi.createUser("someuser" + size, "password", "somename", "someemail@address.com" + size);
        size++;

        Assert.assertEquals(userApi.listUsers().size(), size);
        userApi.createUser("someuser" + size, "password", "somename", "someemail@address.com" + size);
        size++;
    }

    @Test
    @Transactional
    public void getGroupsOfUser() throws WrongParameterException, AlreadyExistsException, AlreadyExistsException {
        int groupsSize = 0;

        User user = userApi.createUser("someuser", "password", "somename", "someemail@address.com");
        User anotherUser = userApi.createUser("anotherUser", "password", "somename", "someemail2@address.com");
        Assert.assertEquals(userApi.getGroupsOfUser(user).size(), groupsSize);

        Group groupA = groupApi.createGroup("group" + groupsSize, user, "desc");
        groupsSize++;
        Assert.assertEquals(userApi.getGroupsOfUser(user).size(), groupsSize);
        Assert.assertEquals(userApi.getGroupsOfUser(anotherUser).size(), 0);


        Group groupB = groupApi.createGroup("group" + groupsSize, user, "desc");
        groupsSize++;
        Assert.assertEquals(userApi.getGroupsOfUser(user).size(), groupsSize);
        Assert.assertEquals(userApi.getGroupsOfUser(anotherUser).size(), 0);

        Group groupC = groupApi.createGroup("group" + groupsSize, anotherUser, "desc");
        Assert.assertEquals(userApi.getGroupsOfUser(user).size(), groupsSize);
        Assert.assertEquals(userApi.getGroupsOfUser(anotherUser).size(), 1);


        Assert.assertTrue(userApi.getGroupsOfUser(user).contains(groupA));
        Assert.assertTrue(userApi.getGroupsOfUser(user).contains(groupB));
        Assert.assertFalse(userApi.getGroupsOfUser(user).contains(groupC));

        Assert.assertFalse(userApi.getGroupsOfUser(anotherUser).contains(groupA));
        Assert.assertFalse(userApi.getGroupsOfUser(anotherUser).contains(groupB));
        Assert.assertTrue(userApi.getGroupsOfUser(anotherUser).contains(groupC));
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    @Transactional
    public void leaveTask() throws WrongParameterException, NotMemberOfException, AlreadyExistsException {
        User user = userApi.createUser("someuser", "password", "somename", "someemail@address.com");
        User anotherUser = userApi.createUser("anotherUser", "password", "somename", "someemail2@address.com");

        Assert.assertEquals(0, user.getTasksOfUser().size());
        Assert.assertEquals(0, anotherUser.getTasksOfUser().size());

        Task task1 = taskApi.createTask(user, "Task1", null, Priority.NORMAL, 10, LocalDateTime.now().plusDays(5));
        Task task2 = taskApi.createTask(user, "Task2", null, Priority.NORMAL, 10, LocalDateTime.now().plusDays(5));

        Assert.assertEquals(2, user.getTasksOfUser().size());
        Assert.assertEquals(0, anotherUser.getTasksOfUser().size());
        Assert.assertNotNull(taskparticipantDAO.findByUserAndTask(user, task1));
        Assert.assertNotNull(taskparticipantDAO.findByUserAndTask(user, task2));
        Assert.assertEquals(2, taskparticipantDAO.findByUser(user).size());
        Assert.assertEquals(0, taskparticipantDAO.findByUser(anotherUser).size());

        userApi.leaveTask(user ,task2);

        Assert.assertEquals(1, user.getTasksOfUser().size());
        Assert.assertEquals(0, anotherUser.getTasksOfUser().size());
        Assert.assertNotNull(taskparticipantDAO.findByUserAndTask(user, task1));
        Assert.assertNull(taskparticipantDAO.findByUserAndTask(user, task2));
        Assert.assertEquals(1, taskparticipantDAO.findByUser(user).size());
        Assert.assertEquals(0, taskparticipantDAO.findByUser(anotherUser).size());

        expectedException.expect(NotMemberOfException.class);
        userApi.leaveTask(anotherUser, task1);
    }

    @Test
    @Transactional
    public void leaveGroup() throws WrongParameterException, GroupPermissionException, NotMemberOfException, AlreadyExistsException, NotAllowedException, AlreadyExistsException {
        User userMember = userApi.createUser("Member", "password", "John Doe", "a@b.c");
        User userAdmin = userApi.createUser("Admin", "password", "John Doe", "c@b.c");

        Group group = groupApi.createGroup("Groupname", userAdmin, "Random description");
        Group group2 = groupApi.createGroup("Groupname2", userAdmin, "Random description");

        Assert.assertEquals(0, userMember.getGroupsOfUser().size());
        Assert.assertEquals(1, group.getGroupMembers().size());
        Assert.assertEquals(1, group2.getGroupMembers().size());

        groupApi.addMember(userAdmin, group, userMember);
        groupApi.addMember(userAdmin, group2, userMember);

        Assert.assertEquals(2, userMember.getGroupsOfUser().size());
        Assert.assertEquals(2, group.getGroupMembers().size());
        Assert.assertEquals(2, group2.getGroupMembers().size());

        userApi.leaveGroup(userMember, group);

        Assert.assertEquals(1, userMember.getGroupsOfUser().size());
        Assert.assertEquals(1, group.getGroupMembers().size());
        Assert.assertEquals(2, group2.getGroupMembers().size());
    }

    @Test
    @Transactional
    public void leaveGroupAsAdmin() throws WrongParameterException, GroupPermissionException, NotMemberOfException, AlreadyExistsException, NotAllowedException, AlreadyExistsException {
        User userMember = userApi.createUser("Member", "password", "John Doe", "a@b.c");
        User userAdmin = userApi.createUser("Admin", "password", "John Doe", "c@b.c");

        Group group = groupApi.createGroup("Groupname", userAdmin, "Random description");
        Group group2 = groupApi.createGroup("Groupname2", userAdmin, "Random description");

        Assert.assertEquals(0, userMember.getGroupsOfUser().size());
        Assert.assertEquals(1, group.getGroupMembers().size());
        Assert.assertEquals(1, group2.getGroupMembers().size());

        groupApi.addMember(userAdmin, group, userMember);
        groupApi.addMember(userAdmin, group2, userMember);

        Assert.assertEquals(2, userMember.getGroupsOfUser().size());
        Assert.assertEquals(2, group.getGroupMembers().size());
        Assert.assertEquals(2, group2.getGroupMembers().size());

        expectedException.expect(NotAllowedException.class);
        userApi.leaveGroup(userAdmin, group);
    }

    @Test
    @Transactional
    public void findById() throws AlreadyExistsException, WrongParameterException {
        User userMember = userApi.createUser("Member", "password", "John Doe", "a@b.c");
        User userAdmin = userApi.createUser("Admin", "password", "John Doe", "c@b.c");

        ArrayList<Long> ids = new ArrayList<>(3);
        ids.add(1657L);
        ids.add(6873L);
        ids.add(1656L);

        ids.remove(userMember.getId());
        ids.remove(userAdmin.getId());

        Assert.assertNotNull(userDAO.findById(userMember.getId()));
        Assert.assertNotNull(userDAO.findById(userAdmin.getId()));
        Assert.assertNull(userDAO.findById(ids.get(0)));
    }
}