package com.melkamar.deadlines.model;

import com.melkamar.deadlines.DeadlinesApplication;
import com.melkamar.deadlines.dao.group.GroupDAO;
import com.melkamar.deadlines.dao.user.UserDAO;
import com.melkamar.deadlines.dao.user.UserDAOHibernate;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 26.03.2016 10:45
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DeadlinesApplication.class)
@WebAppConfiguration
public class GroupTest {
    @Autowired
    private GroupDAO groupDAO;

    @Autowired
    private UserDAO userDAO;

    @Rule
    public ExpectedException thrown = ExpectedException.none();


    @Test
    public void testPersistence() {
        Assert.assertEquals(0, groupDAO.count());

        Group group = new Group("PersistName");
        groupDAO.save(group);

        Assert.assertEquals(1, groupDAO.count());
    }

    @Test
    public void testUniqueName() {
        Group group1 = new Group("GroupName");
        groupDAO.save(group1);

        Group group2 = new Group("GroupNameDifferent");
        groupDAO.save(group2);

        Group group3 = new Group("GroupName");
        thrown.expect(DataIntegrityViolationException.class);
        groupDAO.save(group3);
    }

    @Test
    @Transactional
    public void testMemberRoles() {
        Group group = new Group("RoleGroup");

        User user1 = new User("Member1", "abcd", "abcd");
        User user2 = new User("Member2", "abcd", "abcd");
        User user3 = new User("Member3", "abcd", "abcd");
        User user4 = new User("Manager4", "abcd", "abcd");
        User user5 = new User("Manager5", "abcd", "abcd");
        User user6 = new User("Admin6", "abcd", "abcd");

        group.addMember(user1);
        group.addMember(user2);
        group.addMember(user3);
        group.addMember(user4);
        group.addMember(user5);
        group.addMember(user6);

        group.setManager(user4, true);
        group.setManager(user5, true);

        group.setAdmin(user6);

        groupDAO.save(group);

        userDAO.save(user1);
        userDAO.save(user2);
        userDAO.save(user3);
        userDAO.save(user4);
        userDAO.save(user5);
        userDAO.save(user6);


        Group groupLoaded = groupDAO.findByName("RoleGroup");

        System.out.println("Members -----");
        for (User member : groupLoaded.getMembers()) System.out.println(member.getUsername());
        System.out.println("Managers ----");
        for (User manager : groupLoaded.getManagers()) System.out.println(manager.getUsername());
        System.out.println("Admin -------");
        System.out.println(groupLoaded.getAdmin().getUsername());
        System.out.println("-------------");



        Assert.assertEquals(3, groupLoaded.getMembers().size());
        Assert.assertEquals(2, groupLoaded.getManagers().size());
        Assert.assertNotNull(groupLoaded.getAdmin());
    }
}
