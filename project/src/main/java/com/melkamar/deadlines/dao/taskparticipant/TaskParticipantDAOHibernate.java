package com.melkamar.deadlines.dao.taskparticipant;

import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.TaskParticipant;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 27.03.2016 15:46
 */
@Service("taskparticipantDAO")
public class TaskParticipantDAOHibernate implements TaskParticipantDAO {
    @Autowired
    private TaskParticipantRepository taskParticipantRepository;

    @Override
    public long count() {
        return taskParticipantRepository.count();
    }

    @Override
    public TaskParticipant save(TaskParticipant taskParticipant) {
        return taskParticipantRepository.save(taskParticipant);
    }

    @Override
    public void delete(TaskParticipant taskParticipant) {
        taskParticipantRepository.delete(taskParticipant);
    }

    @Override
    public TaskParticipant findByUserAndTask(User user, Task task) {
        return taskParticipantRepository.findByUserAndTask(user, task);
    }

    @Override
    public Set<TaskParticipant> findByUserAndGroups(User user, Group group) {
        return taskParticipantRepository.findByUserAndGroups(user, group);
    }

    @Override
    public Set<TaskParticipant> findByTaskAndGroups(Task task, Group group) {
        return taskParticipantRepository.findByTaskAndGroups(task, group);
    }

    @Override
    public Set<TaskParticipant> findByUser(User user) {
        return taskParticipantRepository.findByUser(user);
    }
}
