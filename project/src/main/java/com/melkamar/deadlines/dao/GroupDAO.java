package com.melkamar.deadlines.dao;

import com.melkamar.deadlines.model.Group;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 26.03.2016 11:16
 */

public interface GroupDAO extends CrudRepository<Group, Long> {

}
