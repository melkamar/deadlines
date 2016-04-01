package com.melkamar.deadlines.dao.offer.grouptask;

import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.offer.GroupTaskSharingOffer;
import com.melkamar.deadlines.model.offer.Offer;
import com.melkamar.deadlines.model.task.Task;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 01.04.2016 20:14
 */
public interface GroupTaskSharingDAO {
    public GroupTaskSharingOffer findByOfferedToAndTaskOffered(Group group, Task task);
}
