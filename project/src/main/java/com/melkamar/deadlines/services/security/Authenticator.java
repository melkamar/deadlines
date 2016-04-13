package com.melkamar.deadlines.services.security;

import com.melkamar.deadlines.dao.user.UserDAO;
import com.melkamar.deadlines.dao.user.UserDAOHibernate;
import com.melkamar.deadlines.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 31.03.2016 11:45
 */
@Service
public class Authenticator {
    @Autowired
    private ShaPasswordEncoder passwordEncoder;

    /**
     * Authenticates the given user by with his password.
     * If the authentication failed, returns null.
     * If it succeeded, returns the user object.
     */
    public User authenticate(User user, String password){
        if (user == null) return null;

        String encoded = passwordEncoder.encodePassword(password, user.getPasswordSalt());
        if (encoded.equals(user.getPasswordHash())){
            return user;
        } else {
            return null;
        }
    }
}
