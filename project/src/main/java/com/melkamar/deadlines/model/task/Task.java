package com.melkamar.deadlines.model.task;

import com.melkamar.deadlines.model.TaskParticipant;
import com.melkamar.deadlines.model.User;

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
    protected Long id;

    @Column(name = COL_TASK_DATE_CREATED, nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    protected final Date dateCreated;

    @Column(name = COL_TASK_NAME, nullable = false)
    protected String name;

    @Column(name = COL_TASK_DESCRIPTION)
    protected String description;

    @Column(name = COL_TASK_WORK_ESTIMATE)
    protected Double workEstimate; // In manhours

    @Column(name = COL_TASK_PRIORITY)
    @Enumerated(EnumType.STRING)
    protected Priority priority;

    @Column(name = COL_TASK_STATUS)
    @Enumerated(EnumType.STRING)
    protected TaskStatus status;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = Urgency.COL_URGENCY_ID)
    protected Urgency urgency;

    @OneToMany
    @JoinColumn(name = TaskWork.COL_TASKWORK_ID)
    protected Set<TaskWork> workReports = new HashSet<>();

    @OneToMany(mappedBy = "task")
    protected Set<TaskParticipant> participants = new HashSet<>();

    public Task(){
        this.dateCreated = null;
    }

    public Task(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    /*************************************************************/

    public boolean addParticipant(TaskParticipant participant) {
        // TODO: 27.03.2016 Check if correct
        if (participants.contains(participant))
            return false;

        participants.add(participant);
        return true;
    }

    /**
     * Lists all users that are associated with the Task.
     * This serves as a shortcut, so that it is not necessary to
     * get all task participants and then create a Set of users from them.
     * @return Set of Users.
     */
    public Set<User> usersOnTask(){
        // TODO: 27.03.2016 Implement
        return new HashSet<User>();
    }

    /*************************************************************/

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

