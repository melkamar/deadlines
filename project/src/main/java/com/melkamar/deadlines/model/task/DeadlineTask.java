package com.melkamar.deadlines.model.task;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 25.03.2016 17:42
 */
@Entity
@DiscriminatorValue("Deadline")
public class DeadlineTask extends Task{
    @Column(name = "DATE_DEADLINE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated;
}
