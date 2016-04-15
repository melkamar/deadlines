package com.melkamar.deadlines.factory;

import com.melkamar.deadlines.exceptions.WrongParameterException;
import com.melkamar.deadlines.model.User;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 15.04.2016 12:43
 */
public interface UserFactory {
    User createUser(String username, String password, String name, String email) throws WrongParameterException;
}
