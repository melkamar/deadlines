package com.melkamar.deadlines.model.offer;

import com.melkamar.deadlines.model.Group;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 26.03.2016 13:55
 */
@Entity
@Table(name = "OFFER_TASK_GROUP")
public class GroupTaskSharingOffer extends TaskSharingOffer {
    @ManyToOne
    @JoinColumn(name = Group.COL_GROUP_ID)
    private Group offeredTo;

    @Override
    public int accept() {
        // TODO: 26.03.2016
        return 0;
    }

    @Override
    public int decline() {
        // TODO: 26.03.2016
        return 0;
    }
}
