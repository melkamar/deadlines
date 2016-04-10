package com.melkamar.deadlines.model.task;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.TaskParticipant;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.services.helpers.urgency.UrgencyComputer;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    public final static String COL_JTABLE_TASK_GROUP = "TASK_GROUP";
    public final static TaskStatus[] activeStates = new TaskStatus[]{TaskStatus.OPEN, TaskStatus.IN_PROGRESS};

    @Id
    @Column(name = COL_TASK_ID, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(name = COL_TASK_DATE_CREATED, nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    protected final Date dateCreated;

    @Column(name = COL_TASK_NAME, nullable = false)
    protected String name;

    @Column(name = COL_TASK_DESCRIPTION)
    protected String description;

    @Column(name = COL_TASK_WORK_ESTIMATE)
    protected Double workEstimate; // In manhours

    @Column(name = COL_TASK_PRIORITY)
    @Enumerated(EnumType.ORDINAL)
    protected Priority priority;

    @Column(name = COL_TASK_STATUS)
    @Enumerated(EnumType.STRING)
    protected TaskStatus status;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = Urgency.COL_URGENCY_ID)
    protected Urgency urgency;

    @OneToMany(cascade = CascadeType.MERGE)
    @JoinColumn(name = TaskWork.COL_OWNING_TASK_ID, referencedColumnName = COL_TASK_ID)
    protected Set<TaskWork> workReports = new HashSet<>();

    @OneToMany(mappedBy = "task", cascade = CascadeType.MERGE)
    @JsonManagedReference
    protected Set<TaskParticipant> participants = new HashSet<>();

    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(name = COL_JTABLE_TASK_GROUP,
            joinColumns = {@JoinColumn(name = COL_TASK_ID)},
            inverseJoinColumns = {@JoinColumn(name = Group.COL_GROUP_ID)})
    private Set<Group> sharedGroups = new HashSet<>();

    public Task() {
        this.dateCreated = null;
    }

    public Task(Date dateCreated, Urgency urgency) {
        this.dateCreated = dateCreated;
        this.urgency = urgency;
    }

    /*************************************************************/

    public boolean addParticipant(TaskParticipant participant) {
        return participants.add(participant);
    }

    public boolean removeParticipant(TaskParticipant participant) {
        return participants.remove(participant);
    }

    public void addWorkReport(TaskWork taskWork) {
        this.workReports.add(taskWork);
    }

    /**
     * Calculates the total amount of work done on this task in manhours.
     *
     * @return The number of manhours worked.
     */
    public double getManhoursWorked() {
        double total = 0;
        for (TaskWork work : workReports) {
            total += work.getManhours();
        }
        return total;
    }

    /**
     * Lists all users that are associated with the Task.
     * This serves as a shortcut, so that it is not necessary to
     * get all task participants and then create a Set of users from them.
     *
     * @return Set of Users.
     */
    @JsonIgnore
    public Set<User> getUsersOnTask() {
        Set<User> users = new HashSet<User>(participants.size());
        users.addAll(participants.stream().map(TaskParticipant::getUser).collect(Collectors.toList()));

        return users;
    }

    public boolean addSharedGroup(Group group) {
        return sharedGroups.add(group);
    }

    public boolean removeSharedGroup(Group group) {
        return sharedGroups.remove(group);
    }

    public Set<TaskParticipant> getParticipants() {
        return participants;
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

    public Task setName(String name) {
        this.name = name;
        return this;
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

    public Task setStatus(TaskStatus status) {
        this.status = status;
        return this;
    }

    public void setUrgency(Urgency urgency) {
        this.urgency = urgency;
    }

    public Set<TaskWork> getWorkReports() {
        return workReports;
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

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", dateCreated=" + dateCreated +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", workEstimate=" + workEstimate +
                ", priority=" + priority +
                ", status=" + status +
                ", urgency=" + urgency +
                '}';
    }

    /**
     * Calculates the percentage worked on this task. If there is no estimate set, then value -1 is returned.
     *
     * @return Real number between 0 and 1 indicating the percentage worked, or -1 indicating no estimate was set.
     */
    public double getWorkedPercentage() {
        if (workEstimate == 0) return -1;

        double hoursWorked = 0;
        for (TaskWork taskWork : getWorkReports()) {
            hoursWorked += taskWork.getManhours();
        }
        return hoursWorked / workEstimate;
    }

    public void setUrgencyValue(double newValue) {
        this.urgency.update(newValue);
    }

    public abstract void updateUrgency(UrgencyComputer computer);
}

