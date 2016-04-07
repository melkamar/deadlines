package com.melkamar.deadlines.dao.task;

import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.task.Task;
import com.melkamar.deadlines.model.task.TaskStatus;
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
    List<Task> findByStatus(TaskStatus status);

    List<Task> findByParticipants_User(User user);
    List<Task> findByParticipants_UserOrderByNameAsc(User user);
    List<Task> findByParticipants_UserOrderByNameDesc(User user);
    List<Task> findByParticipants_UserOrderByDateCreatedAsc(User user);
    List<Task> findByParticipants_UserOrderByDateCreatedDesc(User user);
    List<Task> findByParticipants_UserOrderByPriorityAsc(User user);
    List<Task> findByParticipants_UserOrderByPriorityDesc(User user);
    List<Task> findByParticipants_UserOrderByUrgency_ValueAsc(User user);
    List<Task> findByParticipants_UserOrderByUrgency_ValueDesc(User user);

    // GROUPS
    List<Task> findBySharedGroups(Group group);
    List<Task> findBySharedGroupsOrderByNameAsc(Group group);
    List<Task> findBySharedGroupsOrderByNameDesc(Group group);
    List<Task> findBySharedGroupsOrderByDateCreatedAsc(Group group);
    List<Task> findBySharedGroupsOrderByDateCreatedDesc(Group group);
    List<Task> findBySharedGroupsOrderByPriorityAsc(Group group);
    List<Task> findBySharedGroupsOrderByPriorityDesc(Group group);
    List<Task> findBySharedGroupsOrderByUrgency_ValueAsc(Group group);
    List<Task> findBySharedGroupsOrderByUrgency_ValueDesc(Group group);
}
