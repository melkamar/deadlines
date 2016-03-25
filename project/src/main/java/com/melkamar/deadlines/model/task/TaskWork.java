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
    @Id
    @Column(name = "TASKWORK_ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "MANHOURS", nullable = false)
    private Double manhours;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User workedBy;
}
