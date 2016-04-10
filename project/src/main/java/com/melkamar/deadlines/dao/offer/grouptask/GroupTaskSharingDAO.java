package com.melkamar.deadlines.dao.offer.grouptask;

import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.offer.GroupTaskSharingOffer;
import com.melkamar.deadlines.model.offer.Offer;
import com.melkamar.deadlines.model.task.Task;

import java.util.Set;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 01.04.2016 20:14
 */
public interface GroupTaskSharingDAO {
    public Set<GroupTaskSharingOffer> findAll();
    public GroupTaskSharingOffer findByOfferedToAndTaskOffered(Group group, Task task);
    public Set<GroupTaskSharingOffer> findByOfferedTo(Group group);
    public GroupTaskSharingOffer findById(Long id);
}
