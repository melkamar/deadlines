package com.melkamar.deadlines.services.api;

import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.User;
import org.springframework.stereotype.Service;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 31.03.2016 10:27
 */
@Service
public class UserAPI {
    public User registerUser(String username, String password, String name, String email){
        // TODO: 31.03.2016 Implement
        throw new NotImplementedException();
    }

    public User editUserDetails(User user, String name, String email){
        // TODO: 31.03.2016 Implement
        throw new NotImplementedException();
    }

    /**
     * Lists all users in the system.
     * @param executor
     * @return
     */
    public List<User> listUsers(User executor){
        // TODO: 31.03.2016 Implement
        throw new NotImplementedException();
    }

    public void leaveGroup(User executor, Group group){
        // TODO: 31.03.2016 Implement
        throw new NotImplementedException();
    }

    public List<Group> listGroups(User executor){
        // TODO: 31.03.2016 Implement
        throw new NotImplementedException();
    }
}
