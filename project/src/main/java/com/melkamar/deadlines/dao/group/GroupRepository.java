package com.melkamar.deadlines.dao.group;

import com.melkamar.deadlines.model.Group;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 26.03.2016 11:16
 */
@Repository
public interface GroupRepository extends CrudRepository<Group, Long> {
    Group findByName(String name);
    Group findById(Long id);
}
