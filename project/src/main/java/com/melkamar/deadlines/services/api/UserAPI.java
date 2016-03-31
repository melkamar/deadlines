package com.melkamar.deadlines.services.api;

import com.melkamar.deadlines.config.StringConstants;
import com.melkamar.deadlines.dao.user.UserDAO;
import com.melkamar.deadlines.exceptions.WrongParameterException;
import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.services.PasswordHashGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 31.03.2016 10:27
 */
@Service
public class UserAPI {
    @Autowired
    private StringConstants stringConstants;
    @Autowired
    private PasswordHashGenerator passwordHashGenerator;
    @Autowired
    private UserDAO userDAO;

    public User createUser(String username, String password, String name, String email) throws WrongParameterException {
        if (username == null || username.isEmpty()) {
            throw new WrongParameterException(stringConstants.EXC_PARAM_USERNAME_EMPTY);
        }

        if (password == null || password.isEmpty()){
            throw new NullPointerException(stringConstants.EXC_PARAM_PASSWORD_EMPTY);
        }

        String[] hashsalt = passwordHashGenerator.generatePasswordHash(password);

        User newUser = new User(username, hashsalt[0], hashsalt[1]);
        newUser.setName(name);
        newUser.setEmail(email);

        userDAO.save(newUser);

        return newUser;
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
