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

    public DeadlineTask(){
        super();
    }

    public DeadlineTask(Date dateCreated) {
        super(dateCreated);
    }

    /***************************************************************/
}
