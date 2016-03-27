package com.melkamar.deadlines.dao.taskparticipant;

import com.melkamar.deadlines.model.TaskParticipant;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

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
        taskParticipantRepository.save(taskParticipant);
        return taskParticipant;
    }

    @Override
    public TaskParticipant findByUserAndTask(User user, Task task) {
        return taskParticipantRepository.findByUserAndTask(user, task);
    }
}