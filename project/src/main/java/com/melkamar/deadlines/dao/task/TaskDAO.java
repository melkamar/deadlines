package com.melkamar.deadlines.dao.task;

import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.task.Task;

import java.util.List;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 27.03.2016 15:14
 */
public interface TaskDAO {
    long count();
    Task save(Task task);
    Task findById(Long id);
    List<Task> findByUser(User user);

    List<Task> findByUserOrderByNameAsc(User user);
    List<Task> findByUserOrderByNameDesc(User user);

    List<Task> findByUserOrderByDateCreatedAsc(User user);
    List<Task> findByUserOrderByDateCreatedDesc(User user);

    List<Task> findByUserOrderByPriorityAsc(User user);
    List<Task> findByUserOrderByPriorityDesc(User user);

    List<Task> findByUserOrderByUrgency_ValueAsc(User user);
    List<Task> findByUserOrderByUrgency_ValueDesc(User user);

    List<Task> findByUserOrderByDeadlineAsc(User user);
    List<Task> findByUserOrderByDeadlineDesc(User user);

    List<Task> findByUserOrderByWorkedAsc(User user);
    List<Task> findByUserOrderByWorkedDesc(User user);
}


