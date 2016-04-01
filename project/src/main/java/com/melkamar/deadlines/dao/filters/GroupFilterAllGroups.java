package com.melkamar.deadlines.dao.filters;

import com.melkamar.deadlines.dao.group.GroupDAO;
import com.melkamar.deadlines.dao.group.GroupDAOHibernate;
import com.melkamar.deadlines.model.Group;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 01.04.2016 9:30
 */
@Component
public class GroupFilterAllGroups implements GroupFilter {

    @Autowired
    private GroupDAO groupDAO;

    @Override
    public List<Group> getGroups(Object... parameters) {
        return groupDAO.findAll();
    }
}
