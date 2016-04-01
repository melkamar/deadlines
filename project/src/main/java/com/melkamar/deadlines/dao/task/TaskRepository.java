package com.melkamar.deadlines.dao.task;

import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.task.Task;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 27.03.2016 15:14
 */
@Repository
public interface TaskRepository extends CrudRepository<Task, Long> {
    Task findById(Long id);
    List<Task> findByParticipants_User(User user);
    List<Task> findByParticipants_UserOrderByNameAsc(User user);
    List<Task> findByParticipants_UserOrderByNameDesc(User user);
    List<Task> findByParticipants_UserOrderByDateCreatedAsc(User user);
    List<Task> findByParticipants_UserOrderByDateCreatedDesc(User user);
    List<Task> findByParticipants_UserOrderByPriorityAsc(User user);
    List<Task> findByParticipants_UserOrderByPriorityDesc(User user);
    List<Task> findByParticipants_UserOrderByUrgency_ValueAsc(User user);
    List<Task> findByParticipants_UserOrderByUrgency_ValueDesc(User user);
}
