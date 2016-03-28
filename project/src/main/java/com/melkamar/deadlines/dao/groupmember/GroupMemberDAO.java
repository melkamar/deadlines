package com.melkamar.deadlines.dao.groupmember;

import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.GroupMember;
import com.melkamar.deadlines.model.User;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 28.03.2016 11:31
 */
public interface GroupMemberDAO {
    public long count();
    public GroupMember save(GroupMember groupMember);
    public GroupMember findByUserAndGroup(User user, Group group);
}
