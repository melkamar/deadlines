package com.melkamar.deadlines.model.task;

import com.melkamar.deadlines.model.TaskParticipant;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 25.03.2016 17:42
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TASK_TYPE", discriminatorType = DiscriminatorType.STRING)
public abstract class Task {
    public final static String COL_TASK_ID = "TASK_ID";
    public final static String COL_TASK_DATE_CREATED = "DATE_CREATED";
    public final static String COL_TASK_NAME = "NAME";
    public final static String COL_TASK_DESCRIPTION = "DESCRIPTION";
    public final static String COL_TASK_WORK_ESTIMATE = "WORK_ESTIMATE";
    public final static String COL_TASK_PRIORITY = "PRIORITY";
    public final static String COL_TASK_STATUS = "STATUS";

    @Id
    @Column(name = COL_TASK_ID, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = COL_TASK_DATE_CREATED, nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated;

    @Column(name = COL_TASK_NAME, nullable = false)
    private String name;

    @Column(name = COL_TASK_DESCRIPTION)
    private String description;

    @Column(name = COL_TASK_WORK_ESTIMATE)
    private Double workEstimate; // In manhours

    @Column(name = COL_TASK_PRIORITY)
    @Enumerated(EnumType.STRING)
    private Priority priority;

    @Column(name = COL_TASK_STATUS)
    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = Urgency.COL_URGENCY_ID)
    private Urgency urgency;

    @OneToMany
    @JoinColumn(name = TaskWork.COL_TASKWORK_ID)
    private Set<TaskWork> workReports = new HashSet<>();

    @OneToMany(mappedBy = "task")
    private Set<TaskParticipant> participants = new HashSet<>();


    int reportWork(double workDone) {
        // TODO
        return 0;
    }

    public boolean addParticipant(TaskParticipant participant) {
        if (participants.contains(participant))
            return false;

        participants.add(participant);
        return true;
    }

}

