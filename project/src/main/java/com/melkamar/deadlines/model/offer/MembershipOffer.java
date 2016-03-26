package com.melkamar.deadlines.model.offer;

import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.User;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 26.03.2016 13:56
 */
@Entity
@Table(name = "OFFER_MEMBERSHIP")
public class MembershipOffer extends Offer {
    @ManyToOne
    @JoinColumn(name = User.COL_USER_ID)
    private User offeredTo;

    @ManyToOne
    @JoinColumn(name = Group.COL_GROUP_ID)
    private Group group;

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
