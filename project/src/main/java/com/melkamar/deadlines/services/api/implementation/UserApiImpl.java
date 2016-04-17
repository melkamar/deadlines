package com.melkamar.deadlines.services.api.implementation;

import com.melkamar.deadlines.config.StringConstants;
import com.melkamar.deadlines.dao.groupmember.GroupMemberDAO;
import com.melkamar.deadlines.dao.user.UserDAO;
import com.melkamar.deadlines.exceptions.*;
import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.GroupMember;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.task.Task;
import com.melkamar.deadlines.services.security.HashAndSaltGenerator;
import com.melkamar.deadlines.services.api.GroupApi;
import com.melkamar.deadlines.services.api.UserApi;
import com.melkamar.deadlines.services.helpers.TaskParticipantHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolation;
import java.text.MessageFormat;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 31.03.2016 10:27
 */
@Service("userApi")
@Transactional
public class UserApiImpl implements UserApi {
    @Autowired
    private StringConstants stringConstants;
    @Autowired
    private HashAndSaltGenerator hashAndSaltGenerator;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private GroupMemberDAO groupMemberDAO;
    @Autowired
    private GroupApi groupApi;
    @Autowired
    private TaskParticipantHelper taskParticipantHelper;


    @Override
    @Transactional(rollbackFor = AlreadyExistsException.class)
    public User createUser(String username, String password, String name, String email) throws WrongParameterException, AlreadyExistsException {
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

        try {
            userDAO.save(newUser);
        } catch (DataIntegrityViolationException e){
            throw new AlreadyExistsException(MessageFormat.format(stringConstants.EXC_ALREADY_EXISTS_USER_NAME, username));
        } catch (javax.validation.ConstraintViolationException e){
            StringBuilder err = new StringBuilder();
            for (ConstraintViolation violation: e.getConstraintViolations()){
                err.append(violation.getMessage());
            }
            throw new WrongParameterException(err.toString());
        }

        return newUser;
    }

    @Override
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
            HashAndSaltGenerator.HashAndSalt hashAndSalt = hashAndSaltGenerator.generatePasswordHash(password);
            user.setNewPassword(hashAndSalt);
        }

        return user;
    }

    /**
     * Lists all users in the system.
     *
     * @return
     */
    @Override
    public List<User> listUsers() {
        return userDAO.findAll();
    }

    /**
     * Removes the user from a group, including all his group-related jobs.
     *
     * @param user
     * @param group
     * @throws NotAllowedException
     * @throws WrongParameterException
     * @throws GroupPermissionException
     * @throws NotMemberOfException
     */
    @Override
    public void leaveGroup(User user, Group group) throws NotAllowedException, WrongParameterException, GroupPermissionException, NotMemberOfException {
        groupApi.removeMember(user, group, user);
    }

    @Override
    public void leaveTask(User user, Task task) throws NotMemberOfException {
        taskParticipantHelper.removeSoloConnection(user, task);
    }

    @Override
    public Set<Group> getGroupsOfUser(User executor) {
        return groupMemberDAO.findByUser(executor).stream().map(GroupMember::getGroup).collect(Collectors.toSet());
    }

    @Override
    public User getUser(Long id) throws DoesNotExistException {
        User user = userDAO.findById(id);
        if (user == null){
            throw new DoesNotExistException(MessageFormat.format(stringConstants.EXC_DOES_NOT_EXIST_USER_ID, id));
        } else {
            return user;
        }
    }

    @Override
    public User getUser(String username) throws DoesNotExistException {
        User user = userDAO.findByUsername(username);
        if (user == null){
            throw new DoesNotExistException(MessageFormat.format(stringConstants.EXC_DOES_NOT_EXIST_USER_ID, username));
        } else {
            return user;
        }
    }
}