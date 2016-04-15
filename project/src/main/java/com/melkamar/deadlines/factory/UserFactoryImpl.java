package com.melkamar.deadlines.factory;

import com.melkamar.deadlines.config.StringConstants;
import com.melkamar.deadlines.exceptions.WrongParameterException;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.services.security.HashAndSaltGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 15.04.2016 12:44
 */
@Component("userFactory")
public class UserFactoryImpl implements UserFactory {


    @Autowired
    private StringConstants stringConstants;
    @Autowired
    private HashAndSaltGenerator hashAndSaltGenerator;


    @Override
    public User createUser(String username, String password, String name, String email) throws WrongParameterException {
        if (username == null || username.isEmpty()) {
            throw new WrongParameterException(stringConstants.EXC_PARAM_USERNAME_EMPTY);
        }

        if (password == null || password.isEmpty()) {
            throw new WrongParameterException(stringConstants.EXC_PARAM_PASSWORD_EMPTY);
        }

        HashAndSaltGenerator.HashAndSalt hashAndSalt = hashAndSaltGenerator.generatePasswordHash(password);

        User newUser = new User(username, hashAndSalt);
        newUser.setName(name);
        newUser.setEmail(email);

        return newUser;
    }
}
