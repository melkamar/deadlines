package com.melkamar.deadlines.dao.group;

import com.melkamar.deadlines.model.Group;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 26.03.2016 12:20
 */

@Service("groupDAO")
@Transactional
public class GroupDAOHibernate implements GroupDAO {
    @Autowired
    private GroupRepository groupRepository;

    @Override
    public long count() {
        return groupRepository.count();
    }

    @Override
    public Group save(Group group) {
        groupRepository.save(group);
        return group;
    }

    @Override
    public Group getGroupByName(String name) {
        return groupRepository.findByName(name);
    }
}
