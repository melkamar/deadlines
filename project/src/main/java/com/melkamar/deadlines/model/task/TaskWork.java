package com.melkamar.deadlines.model.task;

import com.melkamar.deadlines.model.User;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 25.03.2016 19:15
 */
@Entity
public class TaskWork {
    public static final String COL_TASKWORK_ID = "TASKWORK_ID";
    public static final String COL_MANHOURS = "MANHOURS";

    @Id
    @Column(name = COL_TASKWORK_ID, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = COL_MANHOURS, nullable = false)
    private Double manhours;

    @ManyToOne
    @JoinColumn(name = User.COL_USER_ID)
    private User workedBy;
}
