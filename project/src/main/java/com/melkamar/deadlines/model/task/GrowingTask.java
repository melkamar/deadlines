package com.melkamar.deadlines.model.task;

import com.fasterxml.jackson.annotation.JsonView;
import com.melkamar.deadlines.controllers.views.JsonViews;
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
    @JsonView(JsonViews.Always.class)
    protected Double growspeed;

    public GrowingTask() {
        super();
    }

    /**
     *
     * @param dateCreated
     * @param growspeed Approximately how many hours should pass until the task should be completed.
     *                  E.g. number 20 would mean that 20 hours after the task has been created its
     *                  urgency will be equal to urgency of a Task that should be worked on at that moment.
     * @param urgency
     */
    public GrowingTask(Date dateCreated, Double growspeed, Urgency urgency) {
        super(dateCreated, urgency);
        this.growspeed = growspeed;
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

    @Override
    public String taskTypeString() {
        return "GROWING";
    }

    public Double getGrowspeed() {
        return growspeed;
    }
}
