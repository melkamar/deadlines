package com.melkamar.deadlines.dao.group;

import com.melkamar.deadlines.model.Group;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 26.03.2016 12:10
 */
public interface GroupDAO {
    long count();
    Group save(Group group);
    void delete(Group group);
    Group findByName(String name);
    Group findById(Long id);
}
