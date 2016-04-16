package com.melkamar.deadlines.dao.group;

import com.google.common.collect.Lists;
import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.MemberRole;
import com.melkamar.deadlines.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 26.03.2016 12:20
 */

@Service("groupDAO")
public class GroupDAOHibernate implements GroupDAO {
    @Autowired
    private GroupRepository groupRepository;

    @Override
    public long count() {
        return groupRepository.count();
    }

    @Override
    public Group save(Group group) {
//        groupRepository.save(group);
        groupRepository.saveAndFlush(group);
        return group;
    }

    @Override
    public void delete(Group group) {
        groupRepository.delete(group);
    }

    @Override
    public Group findByName(String name) {
        return groupRepository.findByName(name);
    }

    @Override
    public Group findById(Long id) {
        return groupRepository.findById(id);
    }

    @Override
    public List<Group> findAll() {
        return Lists.newArrayList(groupRepository.findAll());
    }

    @Override
    public List<Group> findByMembers_User(User user) {
        return groupRepository.findByMembers_User(user);
    }

    @Override
    public List<Group> findByMembers_UserAndRole(User user, MemberRole role) {
        return groupRepository.findByMembers_UserAndMembers_Role(user, role);
    }
}
