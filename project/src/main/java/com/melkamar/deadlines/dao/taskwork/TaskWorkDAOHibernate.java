package com.melkamar.deadlines.dao.taskwork;

import com.melkamar.deadlines.model.task.TaskWork;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 27.03.2016 19:14
 */
@Service("taskWorkDAO")
public class TaskWorkDAOHibernate implements TaskWorkDAO {


    @Qualifier("taskWorkRepository")
    @Autowired
    private TaskWorkRepository taskWorkRepository;

    @Override
    public long count() {
        return taskWorkRepository.count();
    }

    @Override
    public TaskWork save(TaskWork taskWork) {
        return taskWorkRepository.save(taskWork);
    }
}
