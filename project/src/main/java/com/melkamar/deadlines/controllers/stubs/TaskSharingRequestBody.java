package com.melkamar.deadlines.controllers.stubs;

import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.User;

import java.util.List;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 09.04.2016 17:12
 */
public class TaskSharingRequestBody {
    private List<User> users;
    private List<Group> groups;

    public List<Group> getGroups() {
        return groups;
    }

    public List<User> getUsers() {
        return users;
    }
}
