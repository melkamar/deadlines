package com.melkamar.deadlines.model;

import com.melkamar.deadlines.model.task.Task;
import com.melkamar.deadlines.model.task.TaskRole;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 25.03.2016 18:07
 */
@Entity
@Table(name = "TASK_PARTICIPANT")
public class TaskParticipant {
    @Id
    @Column(name = "TASKPARTICIPANT_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "SOLO")
    private Boolean solo;

    @Enumerated(EnumType.STRING)
    private TaskRole role;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @ManyToOne
    @JoinColumn(name = "TASK_ID")
    private Task task;

    @ManyToMany
    @JoinTable(name = "taskparticipant_group",
            joinColumns = {@JoinColumn(name = "TASKPARTICIPANT_ID")},
            inverseJoinColumns = {@JoinColumn(name = "GROUP_ID")})
    private Set<Group> groups = new HashSet<>();

    public TaskParticipant(){
        this.user = null;
        this.task = null;
    }

    private TaskParticipant(User user, Task task){
        this.user = user;
        this.task = task;
        this.role = TaskRole.WATCHER;
    }

    public static TaskParticipant createTaskParticipant(User user, Task task){
        TaskParticipant participant = new TaskParticipant(user, task);
        task.addParticipant(participant);
        user.addParticipant(participant);

        return participant;
    }


    public Long getId() {
        return id;
    }

    public Boolean getSolo() {
        return solo;
    }

    public User getUser() {
        return user;
    }

    public TaskRole getRole() {
        return role;
    }

    public Set<Group> getGroups() {
        return groups;
    }

    public int leaveTask() {
        return 0;
    }

    public int setRole(TaskRole newRole) {
        return 0;
    }
}
