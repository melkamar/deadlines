package com.melkamar.deadlines.factory;

import com.melkamar.deadlines.exceptions.AlreadyExistsException;
import com.melkamar.deadlines.exceptions.WrongParameterException;
import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.User;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 15.04.2016 12:49
 */
public interface GroupFactory {
    Group createGroup(String name, User founder, String description) throws WrongParameterException;
}
