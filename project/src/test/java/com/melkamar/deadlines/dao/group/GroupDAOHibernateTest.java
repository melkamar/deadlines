package com.melkamar.deadlines.dao.group;

import com.melkamar.deadlines.DeadlinesApplication;
import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.MemberRole;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.services.api.GroupAPI;
import com.melkamar.deadlines.services.api.UserAPI;
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
 * 10.04.2016 12:44
 */
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DeadlinesApplication.class)
public class GroupDAOHibernateTest {


    @Autowired
    private UserAPI userAPI;
    @Autowired
    private GroupAPI groupAPI;
    @Autowired
    private GroupDAOHibernate groupDAO;


    @Test
    @Transactional
    public void findByMembers_UserAndRole() throws Exception {
        User user1 = userAPI.createUser("User1", "pwd", null, null);
        User user2 = userAPI.createUser("User2", "pwd", null, null);
        User user3 = userAPI.createUser("User3", "pwd", null, null);
        User user4 = userAPI.createUser("User4", "pwd", null, null);
        User user5 = userAPI.createUser("User5", "pwd", null, null);

        Group group1 = groupAPI.createGroup("AGroup", user1, null);
        Group group2 = groupAPI.createGroup("BGroup", user1, null);
        Group group3 = groupAPI.createGroup("CGroup", user2, null);
        Group group4 = groupAPI.createGroup("DGroup", user3, null);
        Group group5 = groupAPI.createGroup("EGroup", user3, null);

        /**
         *            ADMIN     |      MANAGER       |     MEMBER
         * user1:     1, 2      |        4           |      3, 5
         * user2:     3         |                    |      1, 2, 4
         * user3:     4, 5      |       1, 2, 3      |
         * user4:               |       1            |      3, 4
         * user5:               |                    |      2
         */
        groupAPI.addMember(user1, group1, user2);
        groupAPI.addMember(user1, group1, user3);
        groupAPI.addMember(user1, group1, user4);
        groupAPI.setManager(user1, group1, user3, true);
        groupAPI.setManager(user1, group1, user4, true);

        groupAPI.addMember(user1, group2, user5);
        groupAPI.addMember(user1, group2, user3);
        groupAPI.addMember(user1, group2, user2);
        groupAPI.addMember(user1, group2, user4);
        groupAPI.setManager(user1, group2, user3, true);

        groupAPI.addMember(user2, group3, user1);
        groupAPI.addMember(user2, group3, user3);
        groupAPI.addMember(user2, group3, user4);
        groupAPI.setManager(user2, group3, user3, true);

        groupAPI.addMember(user3, group4, user1);
        groupAPI.addMember(user3, group4, user2);
        groupAPI.setManager(user3, group4, user1, true);

        groupAPI.addMember(user3, group5, user1);

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


















