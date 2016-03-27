package com.melkamar.deadlines.services.helpers;

import com.melkamar.deadlines.DeadlinesApplication;
import com.melkamar.deadlines.dao.user.UserDAO;
import com.melkamar.deadlines.exceptions.NullParameterException;
import com.melkamar.deadlines.model.User;
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
public class UserHelperTest {
    @Autowired
    private UserHelper userHelper;
    @Autowired
    private UserDAO userDAO;


    @Test(expected = NullParameterException.class)
    @Transactional
    public void nullParameters() throws NullParameterException {
        userHelper.createUser(null, null, null, null);
    }

    @Test(expected = NullParameterException.class)
    @Transactional
    public void emptyParameters() throws NullParameterException {
        userHelper.createUser("", "", "", "");
    }

    @Test
    @Transactional
    public void plainPersistence() throws NullParameterException {
        User user = userHelper.createUser("User1", "password", null, null);

        Assert.assertNotNull(userDAO.findByUsername("User1"));
    }

    @Test(expected = DataIntegrityViolationException.class)
    @Transactional
    public void uniqueUsername() throws NullParameterException {
        User user = userHelper.createUser("uniq1", "password", null, null);
        User user2 = userHelper.createUser("uniq2", "password", null, null);
        User user3 = userHelper.createUser("uniq2", "password", null, null);
    }

    @Test
//    @Transactional
    public void fieldsPersistence() throws NullParameterException {
        User user = userHelper.createUser("User2", "password", "somename", "someemail");
        User retrieved = userDAO.findByUsername("User2");

        System.out.println(user);
        System.out.println(retrieved);

        Assert.assertNotNull(retrieved);
        Assert.assertEquals(user, retrieved);
    }
}