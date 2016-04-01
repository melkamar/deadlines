package com.melkamar.deadlines.dao.filters;

import com.melkamar.deadlines.model.Group;

import java.util.List;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 01.04.2016 9:23
 */
public interface GroupFilter {
    public List<Group> getGroups(Object... parameters);
}
