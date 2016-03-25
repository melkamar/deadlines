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
    @Id
    @Column(name = "TASK_ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "DATE_CREATED", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "WORK_ESTIMATE")
    private Double workEstimate; // In manhours

    @Column(name = "PRIORITY")
    @Enumerated(EnumType.STRING)
    private Priority priority;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "URGENCY_ID")
    private Urgency urgency;

    @OneToMany
    @JoinColumn(name = "TASKWORK_ID")
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

