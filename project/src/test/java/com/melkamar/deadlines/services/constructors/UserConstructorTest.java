package com.melkamar.deadlines.services.constructors;

import com.melkamar.deadlines.DeadlinesApplication;
import com.melkamar.deadlines.dao.user.UserDAO;
import com.melkamar.deadlines.dao.user.UserDAOHibernate;
import com.melkamar.deadlines.exceptions.NullParameterException;
import com.melkamar.deadlines.model.User;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 26.03.2016 15:21
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DeadlinesApplication.class)
public class UserConstructorTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @Autowired
    private UserConstructor userConstructor;
    @Autowired
    private UserDAO userDAO;


    @Test(expected = NullParameterException.class)
    @Transactional
    public void nullParameters() throws NullParameterException {
        userConstructor.createUser(null, null, null, null);
    }

    @Test(expected = NullParameterException.class)
    @Transactional
    public void emptyParameters() throws NullParameterException {
        userConstructor.createUser("", "", "", "");
    }

    @Test
    @Transactional
    public void plainPersistence() throws NullParameterException {
        User user = userConstructor.createUser("User1", "password", null, null);
        userDAO.save(user);

        Assert.assertNotNull(userDAO.findByUsername("User1"));
    }

    @Test
    @Transactional
    public void fieldsPersistence() throws NullParameterException {
        User user = userConstructor.createUser("User2", "password", "somename", "someemail");
        userDAO.save(user);

        User retrieved = userDAO.findByUsername("User2");
        Assert.assertNotNull(retrieved);
        Assert.assertEquals("User2", retrieved.getUsername());
        Assert.assertEquals("somename", retrieved.getName());
        Assert.assertEquals("someemail", retrieved.getEmail());
    }
}