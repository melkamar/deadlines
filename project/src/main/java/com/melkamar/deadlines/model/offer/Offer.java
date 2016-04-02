package com.melkamar.deadlines.model.offer;

import com.melkamar.deadlines.model.User;

import javax.persistence.*;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 26.03.2016 13:54
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Offer {
    public static final String COL_OFFERER_ID = "OFFERER_ID";

    @Id
    @Column(name = "OFFER_ID")
    @GeneratedValue(strategy = GenerationType.TABLE)
    protected Long id;

    @ManyToOne
    @JoinColumn(name = COL_OFFERER_ID, referencedColumnName = User.COL_USER_ID)
    final protected User offerer;

    public Offer(User offerer) {
        this.offerer = offerer;
    }

    public Offer() {
        this.offerer = null;
    }

    public User getOfferer() {
        return offerer;
    }
}
