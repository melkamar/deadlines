package com.melkamar.deadlines.dao.offer.grouptask;

import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.offer.GroupTaskSharingOffer;
import com.melkamar.deadlines.model.offer.Offer;
import com.melkamar.deadlines.model.offer.UserTaskSharingOffer;
import com.melkamar.deadlines.model.task.Task;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 01.04.2016 20:14
 */
public interface GroupTaskSharingRepository extends CrudRepository<GroupTaskSharingOffer, Long> {
    public GroupTaskSharingOffer findByOfferedToAndTaskOffered(Group group, Task task);
}
