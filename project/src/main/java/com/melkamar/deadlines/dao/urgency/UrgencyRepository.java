package com.melkamar.deadlines.dao.urgency;

import com.melkamar.deadlines.model.task.Urgency;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 06.04.2016 21:43
 */
public interface UrgencyRepository extends CrudRepository<Urgency, Long> {
}
