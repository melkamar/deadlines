package com.melkamar.deadlines.dao.taskparticipant;

import com.melkamar.deadlines.model.TaskParticipant;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 27.03.2016 15:46
 */
@Repository
public interface TaskParticipantRepository extends CrudRepository<TaskParticipant, Long> {

}
