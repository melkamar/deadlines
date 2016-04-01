package com.melkamar.deadlines.dao.offer.usertask;

import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.offer.Offer;
import com.melkamar.deadlines.model.offer.UserTaskSharingOffer;
import com.melkamar.deadlines.model.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 01.04.2016 20:14
 */
@Service("userTaskSharingDao")
public class UserTaskSharingDAOHibernate implements UserTaskSharingDAO {

    @Autowired
    private UserTaskSharingRepository userTaskSharingRepository;

    @Override
    public UserTaskSharingOffer findByOfferedToAndTaskOffered(User user, Task task) {
        return userTaskSharingRepository.findByOfferedToAndTaskOffered(user, task);
    }

    @Override
    public Set<UserTaskSharingOffer> findByOfferedTo(User user) {
        return userTaskSharingRepository.findByOfferedTo(user);
    }
}
