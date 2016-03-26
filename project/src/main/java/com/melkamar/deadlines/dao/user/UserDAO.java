package com.melkamar.deadlines.dao.user;

import com.melkamar.deadlines.model.User;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 26.03.2016 12:10
 */
public interface UserDAO {
    public long count();
    public User save(User user);
}
