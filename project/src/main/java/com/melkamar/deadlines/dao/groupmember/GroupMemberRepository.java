package com.melkamar.deadlines.dao.groupmember;

import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.GroupMember;
import com.melkamar.deadlines.model.User;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 28.03.2016 11:32
 */
public interface GroupMemberRepository extends CrudRepository<GroupMember, Long> {
    public GroupMember findByUserAndGroup(User user, Group group);
}
