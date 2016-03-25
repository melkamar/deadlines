package com.melkamar.deadlines.model.task;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 25.03.2016 17:45
 */
@Entity
public class Urgency {
    @Id
    @Column(name = "URGENCY_ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "LAST_UPDATE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdate;

    @Column(name = "VALUE", nullable = false)
    private Integer value;

    public boolean needsUpdate(){
        return false;
    }
}
