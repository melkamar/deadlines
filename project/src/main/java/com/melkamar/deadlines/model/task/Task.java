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

    /*************************************************************/

    public boolean addParticipant(TaskParticipant participant) {
        // TODO: 27.03.2016 Check if correct
        if (participants.contains(participant))
            return false;

        participants.add(participant);
        return true;
    }

    public Long getId() {
        return id;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Double getWorkEstimate() {
        return workEstimate;
    }

    public Priority getPriority() {
        return priority;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public Urgency getUrgency() {
        return urgency;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setWorkEstimate(Double workEstimate) {
        this.workEstimate = workEstimate;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public void setUrgency(Urgency urgency) {
        this.urgency = urgency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;

        return id != null ? id.equals(task.id) : task.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}

