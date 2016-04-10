package com.melkamar.deadlines.controllers.stubs;

import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.User;

import java.util.List;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 09.04.2016 17:12
 */
public class TaskSharingRequestBody {
    private List<Long> userIds;
    private List<Long> groupIds;

    public List<Long> getGroups() {
        return groupIds;
    }

    public List<Long> getUsers() {
        return userIds;
    }
}
