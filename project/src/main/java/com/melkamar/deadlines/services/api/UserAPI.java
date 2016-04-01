package com.melkamar.deadlines.services.api;

import com.melkamar.deadlines.config.StringConstants;
import com.melkamar.deadlines.dao.groupmember.GroupMemberDAO;
import com.melkamar.deadlines.dao.user.UserDAO;
import com.melkamar.deadlines.exceptions.GroupPermissionException;
import com.melkamar.deadlines.exceptions.NotAllowedException;
import com.melkamar.deadlines.exceptions.NotMemberOfException;
import com.melkamar.deadlines.exceptions.WrongParameterException;
import com.melkamar.deadlines.model.*;
import com.melkamar.deadlines.model.task.Task;
import com.melkamar.deadlines.services.PasswordHashGenerator;
import com.melkamar.deadlines.services.helpers.TaskParticipantHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    @Autowired
    private GroupMemberDAO groupMemberDAO;
    @Autowired
    private GroupAPI groupAPI;
    @Autowired
    private TaskParticipantHelper taskParticipantHelper;


    @Transactional
    public User createUser(String username, String password, String name, String email) throws WrongParameterException {
        if (username == null || username.isEmpty()) {
            throw new WrongParameterException(stringConstants.EXC_PARAM_USERNAME_EMPTY);
        }

        if (password == null || password.isEmpty()) {
            throw new NullPointerException(stringConstants.EXC_PARAM_PASSWORD_EMPTY);
        }

        PasswordHashGenerator.HashAndSalt hashAndSalt = passwordHashGenerator.generatePasswordHash(password);

        User newUser = new User(username, hashAndSalt);
        newUser.setName(name);
        newUser.setEmail(email);

        userDAO.save(newUser);

        return newUser;
    }

    @Transactional
    public User editUserDetails(User user, String name, String email, String password) {
        if (user == null) {
            return null;
        }

        if (name != null && !name.isEmpty()) {
            user.setName(name);
        }

        if (email != null && !email.isEmpty()) {
            user.setEmail(email);
        }

        if (password != null && !password.isEmpty()) {
            PasswordHashGenerator.HashAndSalt hashAndSalt = passwordHashGenerator.generatePasswordHash(password);
            user.setNewPassword(hashAndSalt);
        }

        return user;
    }

    /**
     * Lists all users in the system.
     *
     * @return
     */
    public List<User> listUsers() {
        return userDAO.findAll();
    }

    public void leaveGroup(User user, Group group) throws NotMemberOfException, NotAllowedException, GroupPermissionException, WrongParameterException {
        // TODO: 31.03.2016
        throw new NotImplementedException();
    }

    public void leaveTask(User user, Task task) throws NotMemberOfException {
        taskParticipantHelper.removeSoloConnection(user, task);
    }

    @Transactional
    public Set<Group> getGroupsOfUser(User executor) {
        return groupMemberDAO.findByUser(executor).stream().map(GroupMember::getGroup).collect(Collectors.toSet());
    }
}
