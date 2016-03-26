package com.melkamar.deadlines.dao.group;

import com.melkamar.deadlines.model.Group;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 26.03.2016 12:10
 */
public interface GroupDAO {
    public long count();
    public Group save(Group group);
    public Group getGroupByName(String name);
}
