package com.melkamar.deadlines.model.offer;

import com.fasterxml.jackson.annotation.JsonView;
import com.melkamar.deadlines.controllers.JsonViews;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.task.Task;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 26.03.2016 13:55
 */
@Entity
public abstract class TaskSharingOffer extends Offer {
    @ManyToOne
    @JoinColumn(name = Task.COL_TASK_ID)
    @JsonView(JsonViews.Offer.Basic.class)
    protected final Task taskOffered;

    public TaskSharingOffer(User offerer, Task taskOffered) {
        super(offerer);
        this.taskOffered = taskOffered;
    }

    public TaskSharingOffer() {
        taskOffered = null;
    }

    public Task getTaskOffered() {
        return taskOffered;
    }
}
