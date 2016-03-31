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
    public void addMember() throws WrongParameterException {
        User userMember = userAPI.createUser("Member", "password", "John Doe", "a@b.c");
        User userAdmin = userAPI.createUser("Admin", "password", "John Doe", "c@b.c");
        Group group = groupAPI.createGroup("Groupname", userAdmin, "Random description");

        Set<Group> groupSet = new HashSet<>();
        groupSet.add(group);

        Task task = taskAPI.createTask(userAdmin, "TestTask", null, null, 0, groupSet, LocalDateTime.now().plusDays(10));
        Task task2 = taskAPI.createTask(userAdmin, "TestTask2", null, null, 0, groupSet, LocalDateTime.now().plusDays(101));
        Task task3 = taskAPI.createTask(userAdmin, "TestTask3", null, null, 0, groupSet, LocalDateTime.now().plusDays(102));

        throw new NotImplementedException();
    }

    @Test
    @Transactional
    public void removeMember() throws WrongParameterException, GroupPermissionException, NotMemberOfException, AlreadyExistsException {
        User userMember = userAPI.createUser("Member", "password", "John Doe", "a@b.c");
        User userAdmin = userAPI.createUser("Admin", "password", "John Doe", "c@b.c");
        Group group = groupAPI.createGroup("Groupname", userAdmin, "Random description");

        Task task = taskAPI.createTask(userMember, "TestTask", null, null, 0, LocalDateTime.now().plusDays(10));
        Task task2 = taskAPI.createTask(userMember, "TestTask2", null, null, 0, LocalDateTime.now().plusDays(101));
        Task task3 = taskAPI.createTask(userAdmin, "TestTask3", null, null, 0, LocalDateTime.now().plusDays(102));

        TaskParticipant taskParticipant = taskParticipantDAO.findByUserAndTask(userMember, task);
        TaskParticipant taskParticipant2 = taskParticipantDAO.findByUserAndTask(userMember, task2);
        TaskParticipant taskParticipant3 = taskParticipantDAO.findByUserAndTask(userAdmin, task3);

        groupAPI.addMember(userAdmin, group, userMember);
        throw new NotImplementedException();
    }


}


