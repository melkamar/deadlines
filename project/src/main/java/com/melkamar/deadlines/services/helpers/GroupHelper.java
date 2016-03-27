package com.melkamar.deadlines.services.helpers;

import com.melkamar.deadlines.config.StringConstants;
import com.melkamar.deadlines.dao.group.GroupDAO;
import com.melkamar.deadlines.dao.user.UserDAO;
import com.melkamar.deadlines.exceptions.WrongParameterException;
import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 26.03.2016 15:52
 */
@Service
public class GroupHelper {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private StringConstants stringConstants;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private GroupDAO groupDAO;

    public Group createGroup(String name, User founder, String description) throws WrongParameterException {
        if (name == null || name.isEmpty()) throw new WrongParameterException(stringConstants.EXC_PARAM_NAME_EMPTY);
        if (founder == null) throw new WrongParameterException(stringConstants.EXC_PARAM_FOUNDER_NULL);

        Group group = new Group(name);
        group.setDescription(description);

        group.setAdmin(founder);
        if (!founder.addAdminOf(group)) {
            log.warn("User [" + founder.getUsername() + "] has already been Admin of [" + group.getName() + "]");
        }

        groupDAO.save(group);


        return group;
    }
}
