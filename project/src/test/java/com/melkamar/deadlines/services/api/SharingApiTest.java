package com.melkamar.deadlines.services.api;

import com.melkamar.deadlines.DeadlinesApplication;
import com.melkamar.deadlines.dao.offer.OfferDAOHibernate;
import com.melkamar.deadlines.dao.offer.usertask.UserTaskSharingDAOHibernate;
import com.melkamar.deadlines.exceptions.*;
import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.offer.GroupTaskSharingOffer;
import com.melkamar.deadlines.model.offer.MembershipOffer;
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
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 01.04.2016 18:22
 */
//@Rollback(value = false)
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DeadlinesApplication.class)
@WebAppConfiguration
public class SharingApiTest {
    @Autowired
    private UserApi userApi;
    @Autowired
    private TaskApi taskApi;
    @Autowired
    private SharingApi sharingApi;
    @Autowired
    private TaskParticipantHelper taskParticipantHelper;
    @Autowired
    private GroupApi groupApi;
    @Autowired
    private OfferDAOHibernate offerDao;
    @Autowired
    private UserTaskSharingDAOHibernate userTaskSharingDao;

    @Test
    @Transactional
    public void offerTaskSharingUser() throws WrongParameterException, TaskPermissionException, NotMemberOfException, AlreadyExistsException, AlreadyExistsException {
        User user1 = userApi.createUser("TestUser", "pwd", "Some name", "a@b.c");
        User user2 = userApi.createUser("TestUserNonMember", "pwd", "Some name", "a@b.c");

        Task task1 = taskApi.createTask(user1, "task1", null, Priority.NORMAL, 10, 10);
        Task task2 = taskApi.createTask(user1, "task2", null, Priority.NORMAL, 10, 10);

        Assert.assertEquals(0, user1.getTaskOffers().size());
        Assert.assertEquals(0, user2.getTaskOffers().size());

        sharingApi.offerTaskSharing(user1, task1, user2);
        Assert.assertEquals(0, user1.getTaskOffers().size());
        Assert.assertEquals(1, user2.getTaskOffers().size());

        sharingApi.offerTaskSharing(user1, task2, user2);
        Assert.assertEquals(0, user1.getTaskOffers().size());
        Assert.assertEquals(2, user2.getTaskOffers().size());
    }

    @Test(expected = NotMemberOfException.class)
    @Transactional
    public void offerTaskSharingUser_OffererNotParticipant() throws WrongParameterException, TaskPermissionException, NotMemberOfException, AlreadyExistsException, AlreadyExistsException {
        User user1 = userApi.createUser("User1", "pwd", "Some name", "a@b.c");
        User user2 = userApi.createUser("User2", "pwd", "Some name", "a@b.c");
        User user3 = userApi.createUser("User3", "pwd", "Some name", "a@b.c");

        Task task1 = taskApi.createTask(user1, "task1", null, Priority.NORMAL, 10, 10);

        sharingApi.offerTaskSharing(user2, task1, user3);
    }

    @Test(expected = AlreadyExistsException.class)
    @Transactional
    public void offerTaskSharingUser_UserAlreadyParticipant() throws WrongParameterException, TaskPermissionException, NotMemberOfException, AlreadyExistsException, AlreadyExistsException {
        User user1 = userApi.createUser("User1", "pwd", "Some name", "a@b.c");
        User user2 = userApi.createUser("User2", "pwd", "Some name", "a@b.c");
        User user3 = userApi.createUser("User3", "pwd", "Some name", "a@b.c");

        Task task1 = taskApi.createTask(user1, "task1", null, Priority.NORMAL, 10, 10);
        taskParticipantHelper.editOrCreateTaskParticipant(user2, task1, TaskRole.WATCHER, null, true);

        sharingApi.offerTaskSharing(user2, task1, user1);
    }

    @Test(expected = AlreadyExistsException.class)
    @Transactional
    public void offerTaskSharingUser_OfferAlreadyExists() throws WrongParameterException, TaskPermissionException, NotMemberOfException, AlreadyExistsException, AlreadyExistsException {
        User user1 = userApi.createUser("User1", "pwd", "Some name", "a@b.c");
        User user2 = userApi.createUser("User2", "pwd", "Some name", "a@b.c");

        Task task1 = taskApi.createTask(user1, "task1", null, Priority.NORMAL, 10, 10);
        taskParticipantHelper.editOrCreateTaskParticipant(user2, task1, TaskRole.WATCHER, null, true);

        sharingApi.offerTaskSharing(user2, task1, user1);
        sharingApi.offerTaskSharing(user2, task1, user1);
    }

    @Test
    @Transactional
    public void offerTaskSharingGroup() throws WrongParameterException, TaskPermissionException, NotMemberOfException, AlreadyExistsException, AlreadyExistsException {
        User user1 = userApi.createUser("TestUser2", "pwd", "Some name", "a@b.c");
        Group group = groupApi.createGroup("Group2", user1, null);

        Task task1 = taskApi.createTask(user1, "task11", null, Priority.NORMAL, 10, 10);
        Task task2 = taskApi.createTask(user1, "task12", null, Priority.NORMAL, 10, 10);

        Assert.assertEquals(0, group.getTaskOffers().size());

        sharingApi.offerTaskSharing(user1, task1, group);
        Assert.assertEquals(1, group.getTaskOffers().size());

        sharingApi.offerTaskSharing(user1, task2, group);
        Assert.assertEquals(2, group.getTaskOffers().size());
    }

    @Test(expected = NotMemberOfException.class)
    @Transactional
    public void offerTaskSharingGroup_OffererNotParticipant() throws WrongParameterException, TaskPermissionException, NotMemberOfException, AlreadyExistsException, AlreadyExistsException {
        User user1 = userApi.createUser("User1", "pwd", "Some name", "a@b.c");
        User user2 = userApi.createUser("User2", "pwd", "Some name", "a@b.c");
        Group group = groupApi.createGroup("Group", user1, null);

        Task task1 = taskApi.createTask(user1, "task1", null, Priority.NORMAL, 10, 10);

        sharingApi.offerTaskSharing(user2, task1, group);
    }

    @Test(expected = AlreadyExistsException.class)
    @Transactional
    public void offerTaskSharingGroup_UserAlreadyParticipant() throws WrongParameterException, TaskPermissionException, NotMemberOfException, AlreadyExistsException, GroupPermissionException, AlreadyExistsException {
        User user1 = userApi.createUser("User1", "pwd", "Some name", "a@b.c");
        Group group = groupApi.createGroup("Group", user1, null);

        Task task1 = taskApi.createTask(user1, "task1", null, Priority.NORMAL, 10, 10);
        groupApi.addTask(user1, group, task1);

        sharingApi.offerTaskSharing(user1, task1, group);
    }

    @Test(expected = AlreadyExistsException.class)
    @Transactional
    public void offerTaskSharingGroup_OfferAlreadyExists() throws WrongParameterException, TaskPermissionException, NotMemberOfException, AlreadyExistsException, GroupPermissionException, AlreadyExistsException {
        User user1 = userApi.createUser("User1", "pwd", "Some name", "a@b.c");
        Group group = groupApi.createGroup("Group", user1, null);

        Task task1 = taskApi.createTask(user1, "task1", null, Priority.NORMAL, 10, 10);

        sharingApi.offerTaskSharing(user1, task1, group);
        sharingApi.offerTaskSharing(user1, task1, group);
    }

    @Test
    @Transactional
    public void offerMembership() throws WrongParameterException, GroupPermissionException, NotMemberOfException, AlreadyExistsException, AlreadyExistsException {
        User user1 = userApi.createUser("User1", "pwd", "Some name", "a@b.c");
        User user2 = userApi.createUser("User2", "pwd", "Some name", "a@b.c");
        Group group = groupApi.createGroup("Group", user1, null);

        Assert.assertEquals(0, user2.getMembershipOffers().size());

        sharingApi.offerMembership(user1, group, user2);

        Assert.assertEquals(1, user2.getMembershipOffers().size());
    }

    @Test(expected = NotMemberOfException.class)
    @Transactional
    public void offerMembership_OffererNotMember() throws WrongParameterException, GroupPermissionException, NotMemberOfException, AlreadyExistsException, AlreadyExistsException {
        User user1 = userApi.createUser("User1", "pwd", "Some name", "a@b.c");
        User user2 = userApi.createUser("User2", "pwd", "Some name", "a@b.c");
        User user3 = userApi.createUser("User3", "pwd", "Some name", "a@b.c");
        Group group = groupApi.createGroup("Group", user1, null);

        sharingApi.offerMembership(user2, group, user3);
    }

    @Test(expected = GroupPermissionException.class)
    @Transactional
    public void offerMembership_OffererNotManager() throws WrongParameterException, GroupPermissionException, NotMemberOfException, AlreadyExistsException, AlreadyExistsException {
        User user1 = userApi.createUser("User1", "pwd", "Some name", "a@b.c");
        User user2 = userApi.createUser("User2", "pwd", "Some name", "a@b.c");
        User user3 = userApi.createUser("User3", "pwd", "Some name", "a@b.c");
        Group group = groupApi.createGroup("Group", user1, null);
        groupApi.addMember(user1, group, user2);


        sharingApi.offerMembership(user2, group, user3);
    }

    @Test(expected = AlreadyExistsException.class)
    @Transactional
    public void offerMembership_OfferAlreadyExists() throws WrongParameterException, GroupPermissionException, NotMemberOfException, AlreadyExistsException, AlreadyExistsException {
        User user1 = userApi.createUser("User1", "pwd", "Some name", "a@b.c");
        User user2 = userApi.createUser("User2", "pwd", "Some name", "a@b.c");
        Group group = groupApi.createGroup("Group", user1, null);

        sharingApi.offerMembership(user1, group, user2);
        sharingApi.offerMembership(user1, group, user2);
    }

    @Test(expected = AlreadyExistsException.class)
    @Transactional
    public void offerMembership_UserAlreadyMember() throws WrongParameterException, GroupPermissionException, NotMemberOfException, AlreadyExistsException, AlreadyExistsException {
        User user1 = userApi.createUser("User1", "pwd", "Some name", "a@b.c");
        User user2 = userApi.createUser("User2", "pwd", "Some name", "a@b.c");
        Group group = groupApi.createGroup("Group", user1, null);
        groupApi.addMember(user1, group, user2);

        sharingApi.offerMembership(user1, group, user2);
    }

    @Test
    @Transactional
    public void listTaskOffersOfUser() throws WrongParameterException, AlreadyExistsException, NotMemberOfException, AlreadyExistsException {
        User user1 = userApi.createUser("User1", "pwd", "Some name", "a@b.c");
        User user2 = userApi.createUser("User2", "pwd", "Some name", "a@b.c");
        User user3 = userApi.createUser("User3", "pwd", "Some name", "a@b.c");

        Task task1 = taskApi.createTask(user1, "task1", null, Priority.NORMAL, 10, 10);
        Task task2 = taskApi.createTask(user1, "task1", null, Priority.NORMAL, 10, 10);
        Task task3 = taskApi.createTask(user2, "task1", null, Priority.NORMAL, 10, 10);
        Task task4 = taskApi.createTask(user3, "task1", null, Priority.NORMAL, 10, 10);

        Assert.assertEquals(0, sharingApi.listTaskOffersOfUser(user1).size());
        Assert.assertEquals(0, sharingApi.listTaskOffersOfUser(user2).size());
        Assert.assertEquals(0, sharingApi.listTaskOffersOfUser(user3).size());

        sharingApi.offerTaskSharing(user1, task1, user3);

        Assert.assertEquals(0, sharingApi.listTaskOffersOfUser(user1).size());
        Assert.assertEquals(0, sharingApi.listTaskOffersOfUser(user2).size());
        Assert.assertEquals(1, sharingApi.listTaskOffersOfUser(user3).size());

        sharingApi.offerTaskSharing(user1, task2, user3);

        Assert.assertEquals(0, sharingApi.listTaskOffersOfUser(user1).size());
        Assert.assertEquals(0, sharingApi.listTaskOffersOfUser(user2).size());
        Assert.assertEquals(2, sharingApi.listTaskOffersOfUser(user3).size());

        sharingApi.offerTaskSharing(user2, task3, user1);
        sharingApi.offerTaskSharing(user2, task3, user3);

        Assert.assertEquals(1, sharingApi.listTaskOffersOfUser(user1).size());
        Assert.assertEquals(0, sharingApi.listTaskOffersOfUser(user2).size());
        Assert.assertEquals(3, sharingApi.listTaskOffersOfUser(user3).size());

        sharingApi.offerTaskSharing(user3, task4, user1);
        sharingApi.offerTaskSharing(user3, task4, user2);

        Assert.assertEquals(2, sharingApi.listTaskOffersOfUser(user1).size());
        Assert.assertEquals(1, sharingApi.listTaskOffersOfUser(user2).size());
        Assert.assertEquals(3, sharingApi.listTaskOffersOfUser(user3).size());
    }

    @Test
    @Transactional
    public void listMembershipOffersOfUser() throws WrongParameterException, AlreadyExistsException, NotMemberOfException, GroupPermissionException, AlreadyExistsException {
        User user1 = userApi.createUser("User1", "pwd", "Some name", "a@b.c");
        User user2 = userApi.createUser("User2", "pwd", "Some name", "a@b.c");
        User user3 = userApi.createUser("User3", "pwd", "Some name", "a@b.c");

        Group group1 = groupApi.createGroup("group1", user1, null);
        Group group2 = groupApi.createGroup("group2", user1, null);

        Assert.assertEquals(0, sharingApi.listMembershipOffersOfUser(user1).size());
        Assert.assertEquals(0, sharingApi.listMembershipOffersOfUser(user2).size());
        Assert.assertEquals(0, sharingApi.listMembershipOffersOfUser(user3).size());

        sharingApi.offerMembership(user1, group1, user2);

        Assert.assertEquals(0, sharingApi.listMembershipOffersOfUser(user1).size());
        Assert.assertEquals(1, sharingApi.listMembershipOffersOfUser(user2).size());
        Assert.assertEquals(0, sharingApi.listMembershipOffersOfUser(user3).size());

        sharingApi.offerMembership(user1, group1, user3);

        Assert.assertEquals(0, sharingApi.listMembershipOffersOfUser(user1).size());
        Assert.assertEquals(1, sharingApi.listMembershipOffersOfUser(user2).size());
        Assert.assertEquals(1, sharingApi.listMembershipOffersOfUser(user3).size());

        sharingApi.offerMembership(user1, group2, user2);
        sharingApi.offerMembership(user1, group2, user3);

        Assert.assertEquals(0, sharingApi.listMembershipOffersOfUser(user1).size());
        Assert.assertEquals(2, sharingApi.listMembershipOffersOfUser(user2).size());
        Assert.assertEquals(2, sharingApi.listMembershipOffersOfUser(user3).size());
    }

    @Test
    @Transactional
    public void listTaskOffersOfGroup() throws WrongParameterException, GroupPermissionException, NotMemberOfException, AlreadyExistsException, AlreadyExistsException {
        User user1 = userApi.createUser("User1", "pwd", "Some name", "a@b.c");
        User user2 = userApi.createUser("User2", "pwd", "Some name", "a@b.c");
        Group group1 = groupApi.createGroup("Group1", user1, null);

        Task task1 = taskApi.createTask(user2, "task1", null, Priority.NORMAL, 10, 10);
        Task task2 = taskApi.createTask(user2, "task1", null, Priority.NORMAL, 10, 10);

        Assert.assertEquals(0, sharingApi.listTaskOffersOfGroup(user1, group1).size());
        sharingApi.offerTaskSharing(user2, task1, group1);
        Assert.assertEquals(1, sharingApi.listTaskOffersOfGroup(user1, group1).size());
        sharingApi.offerTaskSharing(user2, task2, group1);
        Assert.assertEquals(2, sharingApi.listTaskOffersOfGroup(user1, group1).size());
    }

    @Test(expected = NotMemberOfException.class)
    @Transactional
    public void listTaskOffersOfGroup_UserNotMember() throws WrongParameterException, GroupPermissionException, NotMemberOfException, AlreadyExistsException, AlreadyExistsException {
        User user1 = userApi.createUser("User1", "pwd", "Some name", "a@b.c");
        User user2 = userApi.createUser("User2", "pwd", "Some name", "a@b.c");
        Group group1 = groupApi.createGroup("Group1", user1, null);

        sharingApi.listTaskOffersOfGroup(user2, group1).size();
    }

    @Test(expected = GroupPermissionException.class)
    @Transactional
    public void listTaskOffersOfGroup_UserNotManager() throws WrongParameterException, GroupPermissionException, NotMemberOfException, AlreadyExistsException, AlreadyExistsException {
        User user1 = userApi.createUser("User1", "pwd", "Some name", "a@b.c");
        User user2 = userApi.createUser("User2", "pwd", "Some name", "a@b.c");
        User user3 = userApi.createUser("User3", "pwd", "Some name", "a@b.c");
        Group group1 = groupApi.createGroup("Group1", user1, null);

        groupApi.addMember(user1, group1, user3);

        sharingApi.listTaskOffersOfGroup(user3, group1).size();
    }

    @Test
    @Transactional
    public void resolveTaskSharingOfferUser() throws WrongParameterException, AlreadyExistsException, NotMemberOfException, AlreadyExistsException {
        User user1 = userApi.createUser("User1", "pwd", "Some name", "a@b.c");
        User user2 = userApi.createUser("User2", "pwd", "Some name", "a@b.c");
        User user3 = userApi.createUser("User3", "pwd", "Some name", "a@b.c");

        Task task1 = taskApi.createTask(user1, "task1", null, Priority.NORMAL, 10, 10);
        Task task2 = taskApi.createTask(user1, "task1", null, Priority.NORMAL, 10, 10);
        Task task3 = taskApi.createTask(user2, "task1", null, Priority.NORMAL, 10, 10);
        Task task4 = taskApi.createTask(user3, "task1", null, Priority.NORMAL, 10, 10);


        UserTaskSharingOffer offer1 = sharingApi.offerTaskSharing(user1, task1, user3);
        UserTaskSharingOffer offer2 = sharingApi.offerTaskSharing(user1, task2, user3);
        UserTaskSharingOffer offer3 = sharingApi.offerTaskSharing(user2, task3, user1);
        UserTaskSharingOffer offer4 = sharingApi.offerTaskSharing(user2, task3, user3);
        UserTaskSharingOffer offer5 = sharingApi.offerTaskSharing(user3, task4, user1);
        UserTaskSharingOffer offer6 = sharingApi.offerTaskSharing(user3, task4, user2);

        Assert.assertEquals(1, task1.getParticipants().size());
        Assert.assertEquals(1, task2.getParticipants().size());
        Assert.assertEquals(1, task3.getParticipants().size());
        Assert.assertEquals(1, task4.getParticipants().size());

        Assert.assertEquals(2, user1.getParticipants().size());
        Assert.assertEquals(1, user2.getParticipants().size());
        Assert.assertEquals(1, user3.getParticipants().size());

        Assert.assertNotNull(userTaskSharingDao.findByOfferedToAndTaskOffered(user3, task1));
        sharingApi.resolveTaskSharingOffer(user3, offer1, true);
        Assert.assertNull(userTaskSharingDao.findByOfferedToAndTaskOffered(user3, task1));

        Assert.assertEquals(2, task1.getParticipants().size());
        Assert.assertEquals(1, task2.getParticipants().size());
        Assert.assertEquals(1, task3.getParticipants().size());
        Assert.assertEquals(1, task4.getParticipants().size());

        Assert.assertEquals(2, user1.getParticipants().size());
        Assert.assertEquals(1, user2.getParticipants().size());
        Assert.assertEquals(2, user3.getParticipants().size());

        Assert.assertNotNull(userTaskSharingDao.findByOfferedToAndTaskOffered(user3, task3));
        sharingApi.resolveTaskSharingOffer(user3, offer4, true);
        Assert.assertNull(userTaskSharingDao.findByOfferedToAndTaskOffered(user3, task3));

        Assert.assertEquals(2, task1.getParticipants().size());
        Assert.assertEquals(1, task2.getParticipants().size());
        Assert.assertEquals(2, task3.getParticipants().size());
        Assert.assertEquals(1, task4.getParticipants().size());

        Assert.assertEquals(2, user1.getParticipants().size());
        Assert.assertEquals(1, user2.getParticipants().size());
        Assert.assertEquals(3, user3.getParticipants().size());


        Assert.assertNotNull(userTaskSharingDao.findByOfferedToAndTaskOffered(user1, task3));
        sharingApi.resolveTaskSharingOffer(user1, offer3, false);
        Assert.assertNull(userTaskSharingDao.findByOfferedToAndTaskOffered(user1, task3));

        Assert.assertEquals(2, task1.getParticipants().size());
        Assert.assertEquals(1, task2.getParticipants().size());
        Assert.assertEquals(2, task3.getParticipants().size());
        Assert.assertEquals(1, task4.getParticipants().size());

        Assert.assertEquals(2, user1.getParticipants().size());
        Assert.assertEquals(1, user2.getParticipants().size());
        Assert.assertEquals(3, user3.getParticipants().size());
    }

    @Test
    @Transactional
    public void resolveTaskSharingOfferGroup() throws WrongParameterException, AlreadyExistsException, NotMemberOfException, GroupPermissionException, AlreadyExistsException {
        User user1 = userApi.createUser("User1", "pwd", "Some name", "a@b.c");
        User user2 = userApi.createUser("User2", "pwd", "Some name", "a@b.c");
        User user3 = userApi.createUser("User3", "pwd", "Some name", "a@b.c");
        User user4 = userApi.createUser("User4", "pwd", "Some name", "a@b.c");

        Task task1 = taskApi.createTask(user1, "task1", null, Priority.NORMAL, 10, 10);
        Task task2 = taskApi.createTask(user1, "task1", null, Priority.NORMAL, 10, 10);
        Task task3 = taskApi.createTask(user2, "task1", null, Priority.NORMAL, 10, 10);
        Task task4 = taskApi.createTask(user3, "task1", null, Priority.NORMAL, 10, 10);

        Group group1 = groupApi.createGroup("Group1", user1, null);
        groupApi.addMember(user1, group1, user4);

        GroupTaskSharingOffer offer1 = sharingApi.offerTaskSharing(user2, task3, group1);
        GroupTaskSharingOffer offer2 = sharingApi.offerTaskSharing(user3, task4, group1);
        GroupTaskSharingOffer offer3 = sharingApi.offerTaskSharing(user1, task1, group1);

        Assert.assertEquals(2, user1.getParticipants().size());
        Assert.assertEquals(0, user4.getParticipants().size());

        sharingApi.resolveTaskSharingOffer(group1, user1, offer1, true);
        Assert.assertEquals(3, user1.getParticipants().size());
        Assert.assertEquals(1, user4.getParticipants().size());

        sharingApi.resolveTaskSharingOffer(group1, user1, offer2, false);
        Assert.assertEquals(3, user1.getParticipants().size());
        Assert.assertEquals(1, user4.getParticipants().size());

        sharingApi.resolveTaskSharingOffer(group1, user1, offer3, true);
        Assert.assertEquals(3, user1.getParticipants().size());
        Assert.assertEquals(2, user4.getParticipants().size());
    }

    @Test
    @Transactional
    public void resolveMembershipOffer() throws WrongParameterException, AlreadyExistsException, NotMemberOfException, GroupPermissionException, AlreadyExistsException {
        User user1 = userApi.createUser("User1", "pwd", "Some name", "a@b.c");
        User user2 = userApi.createUser("User2", "pwd", "Some name", "a@b.c");
        User user3 = userApi.createUser("User3", "pwd", "Some name", "a@b.c");

        Task task1 = taskApi.createTask(user1, "task1", null, Priority.NORMAL, 10, 10);
        Task task2 = taskApi.createTask(user1, "task1", null, Priority.NORMAL, 10, 10);
        Task task3 = taskApi.createTask(user1, "task1", null, Priority.NORMAL, 10, 10);

        Group group1 = groupApi.createGroup("Group1", user1, null);
        groupApi.addTask(user1, group1, task1);
        groupApi.addTask(user1, group1, task2);

        MembershipOffer offer1 = sharingApi.offerMembership(user1, group1, user2);
        MembershipOffer offer2 = sharingApi.offerMembership(user1, group1, user3);

        Assert.assertEquals(3, user1.getTasksOfUser().size());
        Assert.assertEquals(0, user2.getTasksOfUser().size());
        Assert.assertEquals(0, user3.getTasksOfUser().size());
        Assert.assertEquals(1, group1.getMembers().size());

        sharingApi.resolveMembershipOffer(user2, offer1, true);
        Assert.assertEquals(3, user1.getTasksOfUser().size());
        Assert.assertEquals(2, user2.getTasksOfUser().size());
        Assert.assertEquals(0, user3.getTasksOfUser().size());
        Assert.assertEquals(2, group1.getMembers().size());

        sharingApi.resolveMembershipOffer(user3, offer2, false);
        Assert.assertEquals(3, user1.getTasksOfUser().size());
        Assert.assertEquals(2, user2.getTasksOfUser().size());
        Assert.assertEquals(0, user3.getTasksOfUser().size());
        Assert.assertEquals(2, group1.getMembers().size());
    }

    @Test(expected = GroupPermissionException.class)
    @Transactional
    public void resolveMembershipOffer_OffererNoLongerManager() throws WrongParameterException, AlreadyExistsException, NotMemberOfException, GroupPermissionException, NotAllowedException, AlreadyExistsException {
        User userAdmin = userApi.createUser("User1", "pwd", "Some name", "a@b.c");
        User userManager = userApi.createUser("User2", "pwd", "Some name", "a@b.c");
        User userNewMember = userApi.createUser("User3", "pwd", "Some name", "a@b.c");

        Group group1 = groupApi.createGroup("Group1", userAdmin, null);
        groupApi.addMember(userAdmin, group1, userManager);
        groupApi.setManager(userAdmin, group1, userManager, true);

        MembershipOffer offer1 = sharingApi.offerMembership(userManager, group1, userNewMember);
        groupApi.setManager(userAdmin, group1, userManager, false);
        sharingApi.resolveMembershipOffer(userNewMember, offer1, true);
    }

}