package com.melkamar.deadlines.services.api;

import com.melkamar.deadlines.exceptions.*;
import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.task.Task;
import com.melkamar.deadlines.services.api.implementation.UserApiImpl;

import java.util.List;
import java.util.Set;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 11.04.2016 14:12
 *
 * {@link UserApiImpl}
 */
public interface UserApi {
    User createUser(String username, String password, String name, String email) throws WrongParameterException, AlreadyExistsException;
    User editUserDetails(User user, String name, String email, String password);
    //
    List<User> listUsers();
    Set<Group> getGroupsOfUser(User executor);
    User getUser(Long id) throws DoesNotExistException;
    User getUser(String username) throws DoesNotExistException;
    //
    void leaveGroup(User user, Group group) throws NotAllowedException, WrongParameterException, GroupPermissionException, NotMemberOfException;
    void leaveTask(User user, Task task) throws NotMemberOfException;
}
