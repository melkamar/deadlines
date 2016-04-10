package com.melkamar.deadlines.dao.processing;

import com.melkamar.deadlines.dao.group.GroupDAO;
import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.MemberRole;
import com.melkamar.deadlines.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 01.04.2016 9:23
 */
@Component(value = "groupFilterGroupsOfUser")
public class GroupFilterUserWithRole implements GroupFilter {

    @Autowired
    private GroupDAO groupDAO;

    /**
     * Returns Groups a User is a member of.
     * @param parameters Takes one parameter of type {@link User}.
     */
    @Override
    public List<Group> getGroups(Object... parameters) {
        User user = (User) parameters[0];
        if (parameters.length==1){
            return groupDAO.findByMembers_User(user);
        } else {
            MemberRole role = (MemberRole) parameters[1];
            return groupDAO.findByMembers_UserAndRole(user, role);
        }

    }
}
