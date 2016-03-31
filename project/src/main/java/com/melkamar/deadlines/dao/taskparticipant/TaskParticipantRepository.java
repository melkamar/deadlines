package com.melkamar.deadlines.dao.taskparticipant;

import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.TaskParticipant;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.task.Task;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 27.03.2016 15:46
 */
@Repository
public interface TaskParticipantRepository extends CrudRepository<TaskParticipant, Long> {
    TaskParticipant findByUserAndTask(User user, Task task);
    Set<TaskParticipant> findByUserAndGroups(User user, Group group);
    Set<TaskParticipant> findByTaskAndGroups(Task task, Group group);
}
