package com.melkamar.deadlines.model.task;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 25.03.2016 17:45
 */
@Entity
public class Urgency {
    public static final String COL_URGENCY_ID = "URGENCY_ID";
    public static final String COL_LAST_UPDATE = "LAST_UPDATE";
    public static final String COL_VALUE = "VALUE";

    @Id
    @Column(name = COL_URGENCY_ID, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = COL_LAST_UPDATE, nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdate;

    @Column(name = COL_VALUE, nullable = false)
    private double value;

    public Urgency() {
        this.value = 0;
        lastUpdate = new Date();
    }

    public void update(double newValue) {
        this.value = newValue;
        lastUpdate = new Date();
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }
}
