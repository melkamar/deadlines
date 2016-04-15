package com.melkamar.deadlines.factory;

import com.melkamar.deadlines.config.StringConstants;
import com.melkamar.deadlines.exceptions.WrongParameterException;
import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 15.04.2016 12:49
 */
@Component("groupFactory")
public class GroupFactoryImpl implements GroupFactory {
    @Autowired
    private StringConstants stringConstants;

    @Override
    public Group createGroup(String name, User founder, String description) throws WrongParameterException {
        if (name == null || name.isEmpty()) throw new WrongParameterException(stringConstants.EXC_PARAM_NAME_EMPTY);
        if (founder == null) throw new WrongParameterException(stringConstants.EXC_PARAM_FOUNDER_NULL);

        Group group = new Group(name);
        group.setDescription(description);

        return group;
    }
}
