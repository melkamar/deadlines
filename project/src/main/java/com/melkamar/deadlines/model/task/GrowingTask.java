package com.melkamar.deadlines.model.task;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 25.03.2016 17:42
 */
@Entity
@DiscriminatorValue("Growing")
public class GrowingTask extends Task {
    @Column(name = "GROW_SPEED", nullable = false)
    private Double growspeed;
}
