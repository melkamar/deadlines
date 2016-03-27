package com.melkamar.deadlines.services.helpers;

import com.melkamar.deadlines.DeadlinesApplication;
import com.melkamar.deadlines.dao.group.GroupDAO;
import com.melkamar.deadlines.dao.group.GroupDAOHibernate;
import com.melkamar.deadlines.dao.user.UserDAO;
import com.melkamar.deadlines.exceptions.NullParameterException;
import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.User;
import com.sun.media.sound.SoftTuning;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 26.03.2016 16:07
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DeadlinesApplication.class)
public class GroupHelperTest {
    @Autowired
    private GroupDAO groupDAO;
    @Autowired
    private GroupHelper groupHelper;

    @Autowired
    private UserDAO userDAO;
    @Autowired
    private UserHelper userHelper;

    @Test(expected = NullParameterException.class)
    @Transactional
    public void nullParameters() throws NullParameterException {
        groupHelper.createGroup(null, null, null);
    }

    @Test(expected = NullParameterException.class)
    @Transactional
    public void nullFounder() throws NullParameterException {
        groupHelper.createGroup("SomeName", null, null);
    }

    @Test
    @Transactional
    public void founderAdmin() throws NullParameterException {
        User user = userHelper.createUser("GroupAdmin", "pwd", null, null);
        Group group = groupHelper.createGroup("AGroup", user, null);

        User retrievedUser = userDAO.findByUsername("GroupAdmin");
        Group retrievedGroup = groupDAO.findByName("AGroup");

        Assert.assertEquals(retrievedUser, retrievedGroup.getAdmin());
        Assert.assertTrue(retrievedUser.isAdminOf(retrievedGroup));

    }



}