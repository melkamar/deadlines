package com.melkamar.deadlines.dao.user;

import com.melkamar.deadlines.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 26.03.2016 12:20
 */

@Component("userDAO")
@Transactional
public class UserDAOHibernate implements UserDAO {
    @Autowired
    private UserRepository userRepository;

    @Override
    public long count() {
        return userRepository.count();
    }

    @Override
    public User save(User user) {
        userRepository.save(user);
        return user;
    }
}
