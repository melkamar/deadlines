package com.melkamar.deadlines.services.api;

import com.melkamar.deadlines.exceptions.*;
import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.task.Task;

import java.util.List;
import java.util.Set;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 11.04.2016 14:12
 *
 * {@link com.melkamar.deadlines.services.api.implementation.UserAPIImpl}
 */
public interface UserAPI {
    public User createUser(String username, String password, String name, String email) throws WrongParameterException, UserAlreadyExistsException;
    public User editUserDetails(User user, String name, String email, String password);
    public List<User> listUsers();
    public void leaveGroup(User user, Group group) throws NotAllowedException, WrongParameterException, GroupPermissionException, NotMemberOfException;
    public void leaveTask(User user, Task task) throws NotMemberOfException;
    public Set<Group> getGroupsOfUser(User executor);
    public User getUser(Long id) throws DoesNotExistException;
    public User getUser(String username) throws DoesNotExistException;
}
