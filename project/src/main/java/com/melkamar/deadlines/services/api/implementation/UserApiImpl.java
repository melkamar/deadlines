/*
 * Copyright (c) 2016 Martin Melka
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.melkamar.deadlines.services.api.implementation;

import com.melkamar.deadlines.config.StringConstants;
import com.melkamar.deadlines.dao.groupmember.GroupMemberDAO;
import com.melkamar.deadlines.dao.user.UserDAO;
import com.melkamar.deadlines.exceptions.*;
import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.GroupMember;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.task.Task;
import com.melkamar.deadlines.services.api.GroupApi;
import com.melkamar.deadlines.services.api.UserApi;
import com.melkamar.deadlines.services.helpers.TaskParticipantHelper;
import com.melkamar.deadlines.services.security.HashAndSaltGenerator;
import org.apache.log4j.Logger;
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
 * @author Martin Melka
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

    @Override
    public List<User> listUsers() {
        return userDAO.findAll();
    }

    @Override
    public void leaveGroup(User user, Group group) throws NotAllowedException, WrongParameterException, NotMemberOfException {
        try {
            groupApi.removeMember(user, group, user);
        } catch (GroupPermissionException e){
            // this will not happen
            Logger.getLogger(this.getClass()).error(e);
        }
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
