package com.melkamar.deadlines.model.offer;

import com.melkamar.deadlines.model.task.Task;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 26.03.2016 13:55
 */
@Entity
public abstract class TaskSharingOffer extends Offer {
    // TODO: 26.03.2016 Should @Inheritance be annotated here as well as in the superclass?

    @ManyToOne
    @JoinColumn(name = Task.COL_TASK_ID)
    private Task taskOffered;
}
