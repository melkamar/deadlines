package com.melkamar.deadlines.model.task;

import com.melkamar.deadlines.services.helpers.urgency.UrgencyComputer;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.Date;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 25.03.2016 17:42
 */
@Entity
@DiscriminatorValue("Growing")
public class GrowingTask extends Task {
    public static final String COL_GROW_SPEED = "GROW_SPEED";

    @Column(name = COL_GROW_SPEED)
    protected Double growspeed;

    public GrowingTask() {
        super();
    }

    public GrowingTask(Date dateCreated, Urgency urgency) {
        super(dateCreated, urgency);
    }

    @Override
    public String toString() {
        return super.toString() + "GrowingTask{" +
                "growspeed=" + growspeed +
                '}';
    }

    @Override
    public void updateUrgency(UrgencyComputer computer) {
        this.urgency.update(computer.computeGrowingTaskUrgency(this));
    }

    public Double getGrowspeed() {
        return growspeed;
    }
}
