package com.melkamar.deadlines.model.task;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import com.melkamar.deadlines.controllers.JsonViews;
import com.melkamar.deadlines.services.helpers.urgency.UrgencyComputer;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 25.03.2016 17:42
 */
@Entity
@DiscriminatorValue("Deadline")
public class DeadlineTask extends Task {
    public static final String COL_DATE_DEADLINE = "DATE_DEADLINE";

    @Column(name = COL_DATE_DEADLINE)
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    @JsonView(JsonViews.Task.Basic.class)
    protected Date deadline;

    public DeadlineTask() {
        super();
    }

    public DeadlineTask(Date dateCreated, Date deadline, Urgency urgency) {
        super(dateCreated, urgency);
        this.deadline = deadline;
    }

    /***************************************************************/

    @Override
    public String toString() {
        return super.toString() + "DeadlineTask{" +
                "deadline=" + deadline +
                '}';
    }

    @Override
    public void updateUrgency(UrgencyComputer computer) {
        this.urgency.update(computer.computeDeadlineTaskUrgency(this));
    }

    @Override
    public String taskTypeString() {
        return "DEADLINE";
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }
}
