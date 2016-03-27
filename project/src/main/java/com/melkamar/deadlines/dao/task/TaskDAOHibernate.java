package com.melkamar.deadlines.dao.task;

import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 27.03.2016 15:14
 */
@Service("taskDAO")
public class TaskDAOHibernate implements TaskDAO {
    @Autowired
    private TaskRepository taskRepository;

    @Override
    public long count() {
        return taskRepository.count();
    }

    @Override
    public Task save(Task task) {
        taskRepository.save(task);
        return task;
    }

    @Override
    public Task findById(Long id) {
        return taskRepository.findById(id);
    }
}
