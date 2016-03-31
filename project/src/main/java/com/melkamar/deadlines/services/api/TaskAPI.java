package com.melkamar.deadlines.services.api;

import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.task.Priority;
import com.melkamar.deadlines.model.task.Task;
import com.melkamar.deadlines.model.task.TaskRole;
import com.melkamar.deadlines.model.task.TaskStatus;
import org.springframework.stereotype.Service;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 31.03.2016 10:27
 */
@Service
public class TaskAPI {
    /**
     * Creates a DeadlineTask. If groups is not null, then it will be immediately shared with the given groups.
     * Creator must be manager of all of them.
     */
    public Task createTask(User creator, String name, String description, Priority priority, double workEstimate, Set<Group> groups, LocalDateTime deadline) {
        // TODO: 31.03.2016 Implement
        throw new NotImplementedException();
    }

    /**
     * Creates a GrowingTask. If groups is not null, then it will be immediately shared with the given groups.
     * Creator must be manager of all of them.
     */
    public Task createTask(User creator, String name, String description, Priority priority, double workEstimate, Set<Group> groups, double growSpeed) {
        // TODO: 31.03.2016 Implement
        throw new NotImplementedException();
    }

    public List<Task> listTasks(User tasksOfUser, TaskFilter filter, SortCriteria sortCriteria) {
        // TODO: 31.03.2016 Implement
        throw new NotImplementedException();
    }

    public Task getTask(User executor, Long taskId) {
        // TODO: 31.03.2016 Implement
        throw new NotImplementedException();
    }

    /**
     * Changes a toChangeUser's role on a task. Will succeed if executor is toChangeUser, or if executor is a manager
     * of a group the toChangeUser belongs to and which is participating on the task.
     * @return
     */
    public Task setTaskRole(User executor, Task task, User toChangeUser, TaskRole newRole){
        // TODO: 31.03.2016 Implement
        throw new NotImplementedException();
    }

    public Task setTaskStatus(User executor, Task task, TaskStatus newStatus){
        // TODO: 31.03.2016 Implement
        throw new NotImplementedException();
    }

    public Task editTask(User executor, Task task, String newDescription, LocalDateTime newDeadline, Double newWorkEstimate, Priority newPriority){
        // TODO: 31.03.2016 Implement
        throw new NotImplementedException();
    }

    public Task reportWork(User reporter, Task task, double workDone){
        // TODO: 31.03.2016 Implement
        throw new NotImplementedException();
    }

    /**
     * Resets urgency of a GrowingTask. Does not affect DeadlineTasks.
     * @param executor
     * @param task
     * @return
     */
    public Task resetUrgency(User executor, Task task){
        // TODO: 31.03.2016 Implement
        throw new NotImplementedException();
    }

    public void leaveTask(User user, Task task){
        // TODO: 31.03.2016 Implement
        throw new NotImplementedException();
    }

    public List<Task> listTasks(Group tasksOfGroup, TaskFilter filter, SortCriteria sortCriteria){
        // TODO: 31.03.2016 Implement
        throw new NotImplementedException();
    }



    public class TaskFilter {
        public List<Task> filter(List<Task> tasks) {
            // TODO: 31.03.2016 Implement
            throw new NotImplementedException();
        }
    }

    public enum SortCriteria {
        NONE, NAME, DATE_START, DATE_DEADLINE, WORKED_PERCENT, PRIORITY, URGENCY
    }
}
