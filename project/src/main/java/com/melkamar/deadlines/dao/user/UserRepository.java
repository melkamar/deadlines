package com.melkamar.deadlines.dao.user;

import com.melkamar.deadlines.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 26.03.2016 11:16
 */
@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    public User findById(Long id);
    public User findByUsername(String username);
    User saveAndFlush(User user);
}
