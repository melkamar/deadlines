package com.melkamar.deadlines.dao.task;

import com.melkamar.deadlines.model.task.Task;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 27.03.2016 15:14
 */
@Repository
public interface TaskRepository extends CrudRepository<Task, Long> {
    Task findById(Long id);
}
