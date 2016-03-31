package com.melkamar.deadlines.dao.groupmember;

import com.melkamar.deadlines.dao.group.GroupDAO;
import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.GroupMember;
import com.melkamar.deadlines.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 28.03.2016 11:33
 */
@Service("groupMemberDAO")
public class GroupMemberDAOHibernate implements GroupMemberDAO {
    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @Override
    public long count() {
        return groupMemberRepository.count();
    }

    @Override
    public GroupMember save(GroupMember groupMember) {
        return groupMemberRepository.save(groupMember);
    }

    @Override
    public GroupMember findByUserAndGroup(User user, Group group) {
        return groupMemberRepository.findByUserAndGroup(user, group);
    }

    @Override
    public Set<GroupMember> findByUser(User user) {
        return groupMemberRepository.findByUser(user);
    }

}
