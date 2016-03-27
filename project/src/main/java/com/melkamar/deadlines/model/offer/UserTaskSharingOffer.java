package com.melkamar.deadlines.model.offer;

import com.melkamar.deadlines.model.User;
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
@Table(name = "OFFER_TASK_USER")
public class UserTaskSharingOffer extends TaskSharingOffer {

    public static final String COL_OFFERED_TO_ID = "OFFERED_TO_ID";

    @ManyToOne
    @JoinColumn(name = COL_OFFERED_TO_ID, referencedColumnName = User.COL_USER_ID)
    private User offeredTo;

    @Override
    public int accept() {
        throw new NotImplementedException();
    }

    @Override
    public int decline() {
        throw new NotImplementedException();
    }
}
