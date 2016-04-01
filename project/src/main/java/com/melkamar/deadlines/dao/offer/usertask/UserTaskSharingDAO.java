package com.melkamar.deadlines.dao.offer.usertask;

import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.offer.Offer;
import com.melkamar.deadlines.model.offer.UserTaskSharingOffer;
import com.melkamar.deadlines.model.task.Task;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 01.04.2016 20:14
 */
public interface UserTaskSharingDAO {
    public UserTaskSharingOffer findByOfferedToAndTaskOffered(User user, Task task);
}
