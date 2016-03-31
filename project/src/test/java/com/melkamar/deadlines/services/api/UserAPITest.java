package com.melkamar.deadlines.services.api;

import com.melkamar.deadlines.DeadlinesApplication;
import com.melkamar.deadlines.dao.user.UserDAO;
import com.melkamar.deadlines.exceptions.WrongParameterException;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.services.security.Authenticator;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 26.03.2016 15:21
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DeadlinesApplication.class)
public class UserAPITest {
    @Autowired
    private UserAPI userAPI;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private Authenticator authenticator;


    @Test(expected = WrongParameterException.class)
    @Transactional
    public void nullParameters() throws WrongParameterException {
        userAPI.createUser(null, null, null, null);
    }

    @Test(expected = WrongParameterException.class)
    @Transactional
    public void emptyParameters() throws WrongParameterException {
        userAPI.createUser("", "", "", "");
    }

    @Test
    @Transactional
    public void plainPersistence() throws WrongParameterException {
        User user = userAPI.createUser("User1", "password", null, null);

        Assert.assertNotNull(userDAO.findByUsername("User1"));
    }

    @Test(expected = DataIntegrityViolationException.class)
    @Transactional
    public void uniqueUsername() throws WrongParameterException {
        User user = userAPI.createUser("uniq1", "password", null, null);
        User user2 = userAPI.createUser("uniq2", "password", null, null);
        User user3 = userAPI.createUser("uniq2", "password", null, null);
    }

    @Test
    @Transactional
    public void fieldsPersistence() throws WrongParameterException {
        User user = userAPI.createUser("User2", "password", "somename", "someemail");
        User retrieved = userDAO.findByUsername("User2");

        System.out.println(user);
        System.out.println(retrieved);

        Assert.assertNotNull(retrieved);
        Assert.assertEquals(user, retrieved);
    }

    @Test
    @Transactional
    public void editUserDetails() throws WrongParameterException {
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
}