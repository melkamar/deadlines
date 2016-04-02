package com.melkamar.deadlines.dao.offer.grouptask;

import com.google.common.collect.Sets;
import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.offer.GroupTaskSharingOffer;
import com.melkamar.deadlines.model.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 01.04.2016 20:14
 */
@Service("groupTaskSharingDao")
public class GroupTaskSharingDAOHibernate implements GroupTaskSharingDAO {

    @Autowired
    private GroupTaskSharingRepository groupTaskSharingRepository;

    @Override
    public Set<GroupTaskSharingOffer> findAll() {
        return Sets.newHashSet(groupTaskSharingRepository.findAll());
    }

    @Override
    public GroupTaskSharingOffer findByOfferedToAndTaskOffered(Group group, Task task) {
        return groupTaskSharingRepository.findByOfferedToAndTaskOffered(group, task);
    }

    @Override
    public Set<GroupTaskSharingOffer> findByOfferedTo(Group group) {
        return groupTaskSharingRepository.findByOfferedTo(group);
    }
}
