package com.melkamar.deadlines.model.offer;

import com.fasterxml.jackson.annotation.JsonView;
import com.melkamar.deadlines.controllers.views.JsonViews;
import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.User;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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
    private final User offeredTo;

    @ManyToOne
    @JoinColumn(name = Group.COL_GROUP_ID)
    @JsonView(JsonViews.Offer.Basic.class)
    private final Group group;

    public User getOfferedTo() {
        return offeredTo;
    }

    public Group getGroup() {
        return group;
    }

    public MembershipOffer(User offerer, Group group, User offeredTo) {
        super(offerer);
        this.offeredTo = offeredTo;
        this.group = group;
    }

    public MembershipOffer() {
        this.offeredTo = null;
        this.group = null;
    }
}
