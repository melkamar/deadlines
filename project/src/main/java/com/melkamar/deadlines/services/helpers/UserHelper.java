package com.melkamar.deadlines.services.helpers;

import com.melkamar.deadlines.config.StringConstants;
import com.melkamar.deadlines.dao.user.UserDAO;
import com.melkamar.deadlines.exceptions.NullParameterException;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.services.PasswordHashGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 26.03.2016 14:49
 */
@Component
public class UserHelper {
    @Autowired
    private StringConstants stringConstants;
    @Autowired
    private PasswordHashGenerator passwordHashGenerator;
    @Autowired
    private UserDAO userDAO;


    public User createUser(String username, String password, String name, String email) throws NullParameterException {
        if (username == null || username.isEmpty()) {
            throw new NullParameterException(stringConstants.EXC_PARAM_USERNAME_EMPTY);
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
}
