package com.melkamar.deadlines.dao.taskparticipant;

import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.TaskParticipant;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.task.Task;

import java.util.Set;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 27.03.2016 15:46
 */
public interface TaskParticipantDAO {
    long count();
    TaskParticipant save(TaskParticipant taskParticipant);
    void delete(TaskParticipant taskParticipant);
    TaskParticipant findByUserAndTask(User user, Task task);
    Set<TaskParticipant> findByUserAndGroups(User user, Group group);
}
