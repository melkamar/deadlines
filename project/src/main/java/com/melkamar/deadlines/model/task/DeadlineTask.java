package com.melkamar.deadlines.model.task;

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
    protected Date deadline;

    public DeadlineTask() {
        super();
    }

    public DeadlineTask(Date dateCreated, Date deadline) {
        super(dateCreated);
        this.deadline = deadline;
    }

    /***************************************************************/

    @Override
    public String toString() {
        return super.toString() + "DeadlineTask{" +
                "deadline=" + deadline +
                '}';
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline){
        this.deadline = deadline;
        // TODO: 01.04.2016 Urgency needs to be updated now
    }
}
