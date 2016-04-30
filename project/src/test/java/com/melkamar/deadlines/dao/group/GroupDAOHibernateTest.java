package com.melkamar.deadlines.dao.group;

import com.melkamar.deadlines.DeadlinesApplication;
import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.MemberRole;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.services.api.GroupApi;
import com.melkamar.deadlines.services.api.UserApi;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Martin Melka
 */
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DeadlinesApplication.class)
public class GroupDAOHibernateTest {


    @Autowired
    private UserApi userApi;
    @Autowired
    private GroupApi groupApi;
    @Autowired
    private GroupDAOHibernate groupDAO;


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

        Assert.assertEquals(2, groupDAO.findByMembers_UserAndRole(user1, MemberRole.ADMIN).size());
        Assert.assertEquals(1, groupDAO.findByMembers_UserAndRole(user1, MemberRole.MANAGER).size());
        Assert.assertEquals(2, groupDAO.findByMembers_UserAndRole(user1, MemberRole.MEMBER).size());

        Assert.assertEquals(1, groupDAO.findByMembers_UserAndRole(user2, MemberRole.ADMIN).size());
        Assert.assertEquals(0, groupDAO.findByMembers_UserAndRole(user2, MemberRole.MANAGER).size());
        Assert.assertEquals(3, groupDAO.findByMembers_UserAndRole(user2, MemberRole.MEMBER).size());

        Assert.assertEquals(2, groupDAO.findByMembers_UserAndRole(user3, MemberRole.ADMIN).size());
        Assert.assertEquals(3, groupDAO.findByMembers_UserAndRole(user3, MemberRole.MANAGER).size());
        Assert.assertEquals(0, groupDAO.findByMembers_UserAndRole(user3, MemberRole.MEMBER).size());

        Assert.assertEquals(0, groupDAO.findByMembers_UserAndRole(user4, MemberRole.ADMIN).size());
        Assert.assertEquals(1, groupDAO.findByMembers_UserAndRole(user4, MemberRole.MANAGER).size());
        Assert.assertEquals(2, groupDAO.findByMembers_UserAndRole(user4, MemberRole.MEMBER).size());

        Assert.assertEquals(0, groupDAO.findByMembers_UserAndRole(user5, MemberRole.ADMIN).size());
        Assert.assertEquals(0, groupDAO.findByMembers_UserAndRole(user5, MemberRole.MANAGER).size());
        Assert.assertEquals(1, groupDAO.findByMembers_UserAndRole(user5, MemberRole.MEMBER).size());
    }
}


















