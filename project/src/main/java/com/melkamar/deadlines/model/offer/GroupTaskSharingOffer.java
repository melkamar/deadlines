package com.melkamar.deadlines.model.offer;

import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.task.Task;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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
    protected final Group offeredTo;

    public GroupTaskSharingOffer(User offerer, Task taskOffered, Group offeredTo) {
        super(offerer, taskOffered);
        this.offeredTo = offeredTo;
    }

    public GroupTaskSharingOffer() {
        this.offeredTo = null;
    }

    public Group getOfferedTo() {
        return offeredTo;
    }

    @Override
    public int accept() {
        // TODO: 26.03.2016
        throw new NotImplementedException();
    }

    @Override
    public int decline() {
        // TODO: 26.03.2016
        throw new NotImplementedException();
    }
}
