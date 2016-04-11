package com.melkamar.deadlines.services.api;

import com.melkamar.deadlines.dao.processing.TaskFilter;
import com.melkamar.deadlines.dao.processing.TaskOrdering;
import com.melkamar.deadlines.exceptions.*;
import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.TaskParticipant;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.task.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 11.04.2016 14:25
 */
public interface TaskAPI {
    public Task createTask(User creator, String name, String description, Priority priority, double workEstimate, LocalDateTime deadline) throws WrongParameterException;
    public Task createTask(User creator, String name, String description, Priority priority, double workEstimate, double hoursToPeak) throws WrongParameterException;
    public Task createTask(User creator, String name, String description, Priority priority, double workEstimate, Set<Group> groups, LocalDateTime deadline) throws WrongParameterException, GroupPermissionException, NotMemberOfException, AlreadyExistsException;
    public Task createTask(User creator, String name, String description, Priority priority, double workEstimate, Set<Group> groups, double hoursToPeak) throws WrongParameterException, GroupPermissionException, NotMemberOfException, AlreadyExistsException;
    public TaskWork reportWork(User user, Task task, Double workDone) throws WrongParameterException, NotMemberOfException, TaskPermissionException;
    public List<Task> listTasks(User tasksOfUser, TaskOrdering ordering, TaskFilter... filters);
    public List<Task> listTasks(Group tasksOfGroup, TaskOrdering ordering, TaskFilter... filters);
    public Task getTask(Long taskId) throws DoesNotExistException;
    public Task getTask(User executor, Long taskId) throws DoesNotExistException, NotMemberOfException;
    public void setTaskRole(User user, Task task, TaskRole newRole) throws NotMemberOfException;
    public void setTaskRole(User user, Task task, TaskRole newRole, User manager, Group group) throws WrongParameterException, NotMemberOfException, GroupPermissionException, NotAllowedException;
    public void setTaskStatus(User user, Task task, TaskStatus newStatus) throws NotMemberOfException, NotAllowedException;
    public void editTask(User user, Task task, String newDescription, LocalDateTime newDeadline, Double newWorkEstimate, Priority newPriority) throws TaskPermissionException, NotAllowedException, NotMemberOfException;
    public Task resetUrgency(User user, Task task) throws NotAllowedException, NotMemberOfException, TaskPermissionException;
    public TaskParticipant getTaskParticipant(User user, Task task);
}
