package com.melkamar.deadlines.dao.taskparticipant;

import com.melkamar.deadlines.model.TaskParticipant;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.task.Task;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 27.03.2016 15:46
 */
public interface TaskParticipantDAO {
    public long count();
    public TaskParticipant save(TaskParticipant taskParticipant);
    public TaskParticipant findByUserAndTask(User user, Task task);
}
