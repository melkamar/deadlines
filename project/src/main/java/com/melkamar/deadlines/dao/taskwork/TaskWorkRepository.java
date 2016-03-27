package com.melkamar.deadlines.dao.taskwork;

import com.melkamar.deadlines.model.task.TaskWork;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 27.03.2016 19:14
 */
@Repository
public interface TaskWorkRepository extends CrudRepository<TaskWork, Long> {
}
