package com.melkamar.deadlines.model;

import com.melkamar.deadlines.DeadlinesApplication;
import com.melkamar.deadlines.dao.group.GroupDAO;
import com.melkamar.deadlines.dao.user.UserDAO;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Martin Melka
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


    @Transactional
    @Test
    public void testPersistence() {
        Assert.assertEquals(0, groupDAO.count());

        Group group = new Group("PersistName");
        groupDAO.save(group);

        Assert.assertEquals(1, groupDAO.count());
    }

    @Transactional
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
}
