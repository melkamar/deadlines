package com.melkamar.deadlines.model.task;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    public static final String COL_OWNING_TASK_ID = "OWNING_TASK_ID";

    @Id
    @Column(name = COL_TASKWORK_ID, nullable = false)
    @GeneratedValue(strategy = GenerationType.TABLE)
    Long id;

    @Column(name = COL_MANHOURS, nullable = false)
    private final Double manhours;

    @ManyToOne
    @JoinColumn(name = User.COL_USER_ID)
    @JsonIgnore
    private final User workedBy;

    @Column(name = COL_OWNING_TASK_ID)
    private Long ownerTaskId;

    public TaskWork(Double manhours, User workedBy) {
        this.manhours = manhours;
        this.workedBy = workedBy;
    }

    public TaskWork() {
        this.manhours = 0d;
        this.workedBy = null;
    }

    public Long getId() {
        return id;
    }

    public Double getManhours() {
        return manhours;
    }

    public User getWorkedBy() {
        return workedBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaskWork)) return false;

        TaskWork taskWork = (TaskWork) o;

        return id != null ? id.equals(taskWork.id) : taskWork.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @JsonProperty(value = "userId")
    public Long getUserId(){
        return this.workedBy.getId();
    }
}
