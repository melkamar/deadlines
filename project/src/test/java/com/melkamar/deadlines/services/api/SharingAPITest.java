package com.melkamar.deadlines.services.api;

import com.melkamar.deadlines.DeadlinesApplication;
import com.melkamar.deadlines.dao.offer.OfferDAOHibernate;
import com.melkamar.deadlines.dao.offer.usertask.UserTaskSharingDAOHibernate;
import com.melkamar.deadlines.exceptions.*;
import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.offer.Offer;
import com.melkamar.deadlines.model.offer.UserTaskSharingOffer;
import com.melkamar.deadlines.model.task.Priority;
import com.melkamar.deadlines.model.task.Task;
import com.melkamar.deadlines.model.task.TaskRole;
import com.melkamar.deadlines.services.helpers.TaskParticipantHelper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 01.04.2016 18:22
 */
//@Rollback(value = false)
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DeadlinesApplication.class)
public class SharingAPITest {
    @Autowired
    private UserAPI userAPI;
    @Autowired
    private TaskAPI taskAPI;
    @Autowired
    private SharingAPI sharingAPI;
    @Autowired
    private TaskParticipantHelper taskParticipantHelper;
    @Autowired
    private GroupAPI groupAPI;
    @Autowired
    private OfferDAOHibernate offerDao;
    @Autowired
    private UserTaskSharingDAOHibernate userTaskSharingDao;

    @Test
    @Transactional
    public void offerTaskSharingUser() throws WrongParameterException, TaskPermissionException, NotMemberOfException, AlreadyExistsException {
        User user1 = userAPI.createUser("TestUser", "pwd", "Some name", "a@b.c");
        User user2 = userAPI.createUser("TestUserNonMember", "pwd", "Some name", "a@b.c");

        Task task1 = taskAPI.createTask(user1, "task1", null, Priority.NORMAL, 10, 10);
        Task task2 = taskAPI.createTask(user1, "task2", null, Priority.NORMAL, 10, 10);

        Assert.assertEquals(0, user1.getTaskOffers().size());
        Assert.assertEquals(0, user2.getTaskOffers().size());

        sharingAPI.offerTaskSharing(user1, task1, user2);
        Assert.assertEquals(0, user1.getTaskOffers().size());
        Assert.assertEquals(1, user2.getTaskOffers().size());

        sharingAPI.offerTaskSharing(user1, task2, user2);
        Assert.assertEquals(0, user1.getTaskOffers().size());
        Assert.assertEquals(2, user2.getTaskOffers().size());
    }

    @Test(expected = NotMemberOfException.class)
    @Transactional
    public void offerTaskSharingUser_OffererNotParticipant() throws WrongParameterException, TaskPermissionException, NotMemberOfException, AlreadyExistsException {
        User user1 = userAPI.createUser("User1", "pwd", "Some name", "a@b.c");
        User user2 = userAPI.createUser("User2", "pwd", "Some name", "a@b.c");
        User user3 = userAPI.createUser("User3", "pwd", "Some name", "a@b.c");

        Task task1 = taskAPI.createTask(user1, "task1", null, Priority.NORMAL, 10, 10);

        sharingAPI.offerTaskSharing(user2, task1, user3);
    }

    @Test(expected = AlreadyExistsException.class)
    @Transactional
    public void offerTaskSharingUser_UserAlreadyParticipant() throws WrongParameterException, TaskPermissionException, NotMemberOfException, AlreadyExistsException {
        User user1 = userAPI.createUser("User1", "pwd", "Some name", "a@b.c");
        User user2 = userAPI.createUser("User2", "pwd", "Some name", "a@b.c");
        User user3 = userAPI.createUser("User3", "pwd", "Some name", "a@b.c");

        Task task1 = taskAPI.createTask(user1, "task1", null, Priority.NORMAL, 10, 10);
        taskParticipantHelper.editOrCreateTaskParticipant(user2, task1, TaskRole.WATCHER, null, true);

        sharingAPI.offerTaskSharing(user2, task1, user1);
    }

    @Test(expected = AlreadyExistsException.class)
    @Transactional
    public void offerTaskSharingUser_OfferAlreadyExists() throws WrongParameterException, TaskPermissionException, NotMemberOfException, AlreadyExistsException {
        User user1 = userAPI.createUser("User1", "pwd", "Some name", "a@b.c");
        User user2 = userAPI.createUser("User2", "pwd", "Some name", "a@b.c");

        Task task1 = taskAPI.createTask(user1, "task1", null, Priority.NORMAL, 10, 10);
        taskParticipantHelper.editOrCreateTaskParticipant(user2, task1, TaskRole.WATCHER, null, true);

        sharingAPI.offerTaskSharing(user2, task1, user1);
        sharingAPI.offerTaskSharing(user2, task1, user1);
    }

    @Test
    @Transactional
    public void offerTaskSharingGroup() throws WrongParameterException, TaskPermissionException, NotMemberOfException, AlreadyExistsException {
        User user1 = userAPI.createUser("TestUser2", "pwd", "Some name", "a@b.c");
        Group group = groupAPI.createGroup("Group2", user1, null);

        Task task1 = taskAPI.createTask(user1, "task11", null, Priority.NORMAL, 10, 10);
        Task task2 = taskAPI.createTask(user1, "task12", null, Priority.NORMAL, 10, 10);

        Assert.assertEquals(0, group.getTaskOffers().size());

        sharingAPI.offerTaskSharing(user1, task1, group);
        Assert.assertEquals(1, group.getTaskOffers().size());

        sharingAPI.offerTaskSharing(user1, task2, group);
        Assert.assertEquals(2, group.getTaskOffers().size());
    }

    @Test(expected = NotMemberOfException.class)
    @Transactional
    public void offerTaskSharingGroup_OffererNotParticipant() throws WrongParameterException, TaskPermissionException, NotMemberOfException, AlreadyExistsException {
        User user1 = userAPI.createUser("User1", "pwd", "Some name", "a@b.c");
        User user2 = userAPI.createUser("User2", "pwd", "Some name", "a@b.c");
        Group group = groupAPI.createGroup("Group", user1, null);

        Task task1 = taskAPI.createTask(user1, "task1", null, Priority.NORMAL, 10, 10);

        sharingAPI.offerTaskSharing(user2, task1, group);
    }

    @Test(expected = AlreadyExistsException.class)
    @Transactional
    public void offerTaskSharingGroup_UserAlreadyParticipant() throws WrongParameterException, TaskPermissionException, NotMemberOfException, AlreadyExistsException, GroupPermissionException {
        User user1 = userAPI.createUser("User1", "pwd", "Some name", "a@b.c");
        Group group = groupAPI.createGroup("Group", user1, null);

        Task task1 = taskAPI.createTask(user1, "task1", null, Priority.NORMAL, 10, 10);
        groupAPI.addTask(user1, group, task1);

        sharingAPI.offerTaskSharing(user1, task1, group);
    }

    @Test(expected = AlreadyExistsException.class)
    @Transactional
    public void offerTaskSharingGroup_OfferAlreadyExists() throws WrongParameterException, TaskPermissionException, NotMemberOfException, AlreadyExistsException, GroupPermissionException {
        User user1 = userAPI.createUser("User1", "pwd", "Some name", "a@b.c");
        Group group = groupAPI.createGroup("Group", user1, null);

        Task task1 = taskAPI.createTask(user1, "task1", null, Priority.NORMAL, 10, 10);

        sharingAPI.offerTaskSharing(user1, task1, group);
        sharingAPI.offerTaskSharing(user1, task1, group);
    }

    @Test
    @Transactional
    public void offerMembership() throws WrongParameterException, GroupPermissionException, NotMemberOfException, AlreadyExistsException {
        User user1 = userAPI.createUser("User1", "pwd", "Some name", "a@b.c");
        User user2 = userAPI.createUser("User2", "pwd", "Some name", "a@b.c");
        Group group = groupAPI.createGroup("Group", user1, null);

        Assert.assertEquals(0, user2.getMembershipOffers().size());

        sharingAPI.offerMembership(user1, group, user2);

        Assert.assertEquals(1, user2.getMembershipOffers().size());
    }

    @Test(expected = NotMemberOfException.class)
    @Transactional
    public void offerMembership_OffererNotMember() throws WrongParameterException, GroupPermissionException, NotMemberOfException, AlreadyExistsException {
        User user1 = userAPI.createUser("User1", "pwd", "Some name", "a@b.c");
        User user2 = userAPI.createUser("User2", "pwd", "Some name", "a@b.c");
        User user3 = userAPI.createUser("User3", "pwd", "Some name", "a@b.c");
        Group group = groupAPI.createGroup("Group", user1, null);

        sharingAPI.offerMembership(user2, group, user3);
    }

    @Test(expected = GroupPermissionException.class)
    @Transactional
    public void offerMembership_OffererNotManager() throws WrongParameterException, GroupPermissionException, NotMemberOfException, AlreadyExistsException {
        User user1 = userAPI.createUser("User1", "pwd", "Some name", "a@b.c");
        User user2 = userAPI.createUser("User2", "pwd", "Some name", "a@b.c");
        User user3 = userAPI.createUser("User3", "pwd", "Some name", "a@b.c");
        Group group = groupAPI.createGroup("Group", user1, null);
        groupAPI.addMember(user1, group, user2);


        sharingAPI.offerMembership(user2, group, user3);
    }

    @Test(expected = AlreadyExistsException.class)
    @Transactional
    public void offerMembership_OfferAlreadyExists() throws WrongParameterException, GroupPermissionException, NotMemberOfException, AlreadyExistsException {
        User user1 = userAPI.createUser("User1", "pwd", "Some name", "a@b.c");
        User user2 = userAPI.createUser("User2", "pwd", "Some name", "a@b.c");
        Group group = groupAPI.createGroup("Group", user1, null);

        sharingAPI.offerMembership(user1, group, user2);
        sharingAPI.offerMembership(user1, group, user2);
    }

    @Test(expected = AlreadyExistsException.class)
    @Transactional
    public void offerMembership_UserAlreadyMember() throws WrongParameterException, GroupPermissionException, NotMemberOfException, AlreadyExistsException {
        User user1 = userAPI.createUser("User1", "pwd", "Some name", "a@b.c");
        User user2 = userAPI.createUser("User2", "pwd", "Some name", "a@b.c");
        Group group = groupAPI.createGroup("Group", user1, null);
        groupAPI.addMember(user1, group, user2);

        sharingAPI.offerMembership(user1, group, user2);
    }

    @Test
    @Transactional
    public void listTaskOffersOfUser() throws WrongParameterException, AlreadyExistsException, NotMemberOfException {
        User user1 = userAPI.createUser("User1", "pwd", "Some name", "a@b.c");
        User user2 = userAPI.createUser("User2", "pwd", "Some name", "a@b.c");
        User user3 = userAPI.createUser("User3", "pwd", "Some name", "a@b.c");

        Task task1 = taskAPI.createTask(user1, "task1", null, Priority.NORMAL, 10, 10);
        Task task2 = taskAPI.createTask(user1, "task1", null, Priority.NORMAL, 10, 10);
        Task task3 = taskAPI.createTask(user2, "task1", null, Priority.NORMAL, 10, 10);
        Task task4 = taskAPI.createTask(user3, "task1", null, Priority.NORMAL, 10, 10);

        Assert.assertEquals(0, sharingAPI.listTaskOffersOfUser(user1).size());
        Assert.assertEquals(0, sharingAPI.listTaskOffersOfUser(user2).size());
        Assert.assertEquals(0, sharingAPI.listTaskOffersOfUser(user3).size());

        sharingAPI.offerTaskSharing(user1, task1, user3);

        Assert.assertEquals(0, sharingAPI.listTaskOffersOfUser(user1).size());
        Assert.assertEquals(0, sharingAPI.listTaskOffersOfUser(user2).size());
        Assert.assertEquals(1, sharingAPI.listTaskOffersOfUser(user3).size());

        sharingAPI.offerTaskSharing(user1, task2, user3);

        Assert.assertEquals(0, sharingAPI.listTaskOffersOfUser(user1).size());
        Assert.assertEquals(0, sharingAPI.listTaskOffersOfUser(user2).size());
        Assert.assertEquals(2, sharingAPI.listTaskOffersOfUser(user3).size());

        sharingAPI.offerTaskSharing(user2, task3, user1);
        sharingAPI.offerTaskSharing(user2, task3, user3);

        Assert.assertEquals(1, sharingAPI.listTaskOffersOfUser(user1).size());
        Assert.assertEquals(0, sharingAPI.listTaskOffersOfUser(user2).size());
        Assert.assertEquals(3, sharingAPI.listTaskOffersOfUser(user3).size());

        sharingAPI.offerTaskSharing(user3, task4, user1);
        sharingAPI.offerTaskSharing(user3, task4, user2);

        Assert.assertEquals(2, sharingAPI.listTaskOffersOfUser(user1).size());
        Assert.assertEquals(1, sharingAPI.listTaskOffersOfUser(user2).size());
        Assert.assertEquals(3, sharingAPI.listTaskOffersOfUser(user3).size());
    }

    @Test
    @Transactional
    public void listMembershipOffersOfUser() throws WrongParameterException, AlreadyExistsException, NotMemberOfException, GroupPermissionException {
        User user1 = userAPI.createUser("User1", "pwd", "Some name", "a@b.c");
        User user2 = userAPI.createUser("User2", "pwd", "Some name", "a@b.c");
        User user3 = userAPI.createUser("User3", "pwd", "Some name", "a@b.c");

        Group group1 = groupAPI.createGroup("group1", user1, null);
        Group group2 = groupAPI.createGroup("group2", user1, null);

        Assert.assertEquals(0, sharingAPI.listMembershipOffersOfUser(user1).size());
        Assert.assertEquals(0, sharingAPI.listMembershipOffersOfUser(user2).size());
        Assert.assertEquals(0, sharingAPI.listMembershipOffersOfUser(user3).size());

        sharingAPI.offerMembership(user1, group1, user2);

        Assert.assertEquals(0, sharingAPI.listMembershipOffersOfUser(user1).size());
        Assert.assertEquals(1, sharingAPI.listMembershipOffersOfUser(user2).size());
        Assert.assertEquals(0, sharingAPI.listMembershipOffersOfUser(user3).size());

        sharingAPI.offerMembership(user1, group1, user3);

        Assert.assertEquals(0, sharingAPI.listMembershipOffersOfUser(user1).size());
        Assert.assertEquals(1, sharingAPI.listMembershipOffersOfUser(user2).size());
        Assert.assertEquals(1, sharingAPI.listMembershipOffersOfUser(user3).size());

        sharingAPI.offerMembership(user1, group2, user2);
        sharingAPI.offerMembership(user1, group2, user3);

        Assert.assertEquals(0, sharingAPI.listMembershipOffersOfUser(user1).size());
        Assert.assertEquals(2, sharingAPI.listMembershipOffersOfUser(user2).size());
        Assert.assertEquals(2, sharingAPI.listMembershipOffersOfUser(user3).size());
    }

    @Test
    @Transactional
    public void listTaskOffersOfGroup() throws WrongParameterException, GroupPermissionException, NotMemberOfException, AlreadyExistsException {
        User user1 = userAPI.createUser("User1", "pwd", "Some name", "a@b.c");
        User user2 = userAPI.createUser("User2", "pwd", "Some name", "a@b.c");
        Group group1 = groupAPI.createGroup("Group1", user1, null);

        Task task1 = taskAPI.createTask(user2, "task1", null, Priority.NORMAL, 10, 10);
        Task task2 = taskAPI.createTask(user2, "task1", null, Priority.NORMAL, 10, 10);

        Assert.assertEquals(0, sharingAPI.listTaskOffersOfGroup(user1, group1).size());
        sharingAPI.offerTaskSharing(user2, task1, group1);
        Assert.assertEquals(1, sharingAPI.listTaskOffersOfGroup(user1, group1).size());
        sharingAPI.offerTaskSharing(user2, task2, group1);
        Assert.assertEquals(2, sharingAPI.listTaskOffersOfGroup(user1, group1).size());
    }

    @Test(expected = NotMemberOfException.class)
    @Transactional
    public void listTaskOffersOfGroup_UserNotMember() throws WrongParameterException, GroupPermissionException, NotMemberOfException, AlreadyExistsException {
        User user1 = userAPI.createUser("User1", "pwd", "Some name", "a@b.c");
        User user2 = userAPI.createUser("User2", "pwd", "Some name", "a@b.c");
        Group group1 = groupAPI.createGroup("Group1", user1, null);

        sharingAPI.listTaskOffersOfGroup(user2, group1).size();
    }

    @Test(expected = GroupPermissionException.class)
    @Transactional
    public void listTaskOffersOfGroup_UserNotManager() throws WrongParameterException, GroupPermissionException, NotMemberOfException, AlreadyExistsException {
        User user1 = userAPI.createUser("User1", "pwd", "Some name", "a@b.c");
        User user2 = userAPI.createUser("User2", "pwd", "Some name", "a@b.c");
        User user3 = userAPI.createUser("User3", "pwd", "Some name", "a@b.c");
        Group group1 = groupAPI.createGroup("Group1", user1, null);

        groupAPI.addMember(user1, group1, user3);

        sharingAPI.listTaskOffersOfGroup(user3, group1).size();
    }

    @Test
    @Transactional
    public void resolveTaskSharingOffer() throws WrongParameterException, AlreadyExistsException, NotMemberOfException {
        User user1 = userAPI.createUser("User1", "pwd", "Some name", "a@b.c");
        User user2 = userAPI.createUser("User2", "pwd", "Some name", "a@b.c");
        User user3 = userAPI.createUser("User3", "pwd", "Some name", "a@b.c");

        Task task1 = taskAPI.createTask(user1, "task1", null, Priority.NORMAL, 10, 10);
        Task task2 = taskAPI.createTask(user1, "task1", null, Priority.NORMAL, 10, 10);
        Task task3 = taskAPI.createTask(user2, "task1", null, Priority.NORMAL, 10, 10);
        Task task4 = taskAPI.createTask(user3, "task1", null, Priority.NORMAL, 10, 10);


        UserTaskSharingOffer offer1 = sharingAPI.offerTaskSharing(user1, task1, user3);
        UserTaskSharingOffer offer2 = sharingAPI.offerTaskSharing(user1, task2, user3);
        UserTaskSharingOffer offer3 = sharingAPI.offerTaskSharing(user2, task3, user1);
        UserTaskSharingOffer offer4 = sharingAPI.offerTaskSharing(user2, task3, user3);
        UserTaskSharingOffer offer5 = sharingAPI.offerTaskSharing(user3, task4, user1);
        UserTaskSharingOffer offer6 = sharingAPI.offerTaskSharing(user3, task4, user2);

        Assert.assertEquals(1, task1.getParticipants().size());
        Assert.assertEquals(1, task2.getParticipants().size());
        Assert.assertEquals(1, task3.getParticipants().size());
        Assert.assertEquals(1, task4.getParticipants().size());

        Assert.assertEquals(2, user1.getParticipants().size());
        Assert.assertEquals(1, user2.getParticipants().size());
        Assert.assertEquals(1, user3.getParticipants().size());

        Assert.assertNotNull(userTaskSharingDao.findByOfferedToAndTaskOffered(user3, task1));
        sharingAPI.resolveTaskSharingOffer(user3, offer1, true);
        Assert.assertNull(userTaskSharingDao.findByOfferedToAndTaskOffered(user3, task1));

        Assert.assertEquals(2, task1.getParticipants().size());
        Assert.assertEquals(1, task2.getParticipants().size());
        Assert.assertEquals(1, task3.getParticipants().size());
        Assert.assertEquals(1, task4.getParticipants().size());

        Assert.assertEquals(2, user1.getParticipants().size());
        Assert.assertEquals(1, user2.getParticipants().size());
        Assert.assertEquals(2, user3.getParticipants().size());

        Assert.assertNotNull(userTaskSharingDao.findByOfferedToAndTaskOffered(user3, task3));
        sharingAPI.resolveTaskSharingOffer(user3, offer4, true);
        Assert.assertNull(userTaskSharingDao.findByOfferedToAndTaskOffered(user3, task3));

        Assert.assertEquals(2, task1.getParticipants().size());
        Assert.assertEquals(1, task2.getParticipants().size());
        Assert.assertEquals(2, task3.getParticipants().size());
        Assert.assertEquals(1, task4.getParticipants().size());

        Assert.assertEquals(2, user1.getParticipants().size());
        Assert.assertEquals(1, user2.getParticipants().size());
        Assert.assertEquals(3, user3.getParticipants().size());


        Assert.assertNotNull(userTaskSharingDao.findByOfferedToAndTaskOffered(user1, task3));
        sharingAPI.resolveTaskSharingOffer(user1, offer3, false);
        Assert.assertNull(userTaskSharingDao.findByOfferedToAndTaskOffered(user1, task3));

        Assert.assertEquals(2, task1.getParticipants().size());
        Assert.assertEquals(1, task2.getParticipants().size());
        Assert.assertEquals(2, task3.getParticipants().size());
        Assert.assertEquals(1, task4.getParticipants().size());

        Assert.assertEquals(2, user1.getParticipants().size());
        Assert.assertEquals(1, user2.getParticipants().size());
        Assert.assertEquals(3, user3.getParticipants().size());
    }

}