package com.melkamar.deadlines.model;

import com.melkamar.deadlines.DeadlinesApplication;
import com.melkamar.deadlines.dao.GroupDAO;
import com.melkamar.deadlines.dao.GroupRepository;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

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


    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testPersistence() {
        Assert.assertEquals(0, groupDAO.count());

        Group group = new Group("PersistName", "Description");
        groupDAO.save(group);

        Assert.assertEquals(1, groupDAO.count());
    }

    @Test
    public void testUniqueName() {
        Group group1 = new Group("GroupName", "Description lorem ipsum");
        groupDAO.save(group1);

        Group group2 = new Group("GroupNameDifferent", "Description lorem ipsum");
        groupDAO.save(group2);

        Group group3 = new Group("GroupName", "Description lorem ipsum");
        thrown.expect(JpaSystemException.class);
        groupDAO.save(group3);
    }
}
