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
    public static final String COL_TASKPARTICIPANT_ID = "TASKPARTICIPANT_ID";
    public static final String COL_SOLO = "SOLO";
    public static final String COL_ROLE = "ROLE";
    public static final String COL_JTABLE_TASKPARTICIPANT_GROUP = "TASK_PARTICIPANT_GROUP";

    @Id
    @Column(name = COL_TASKPARTICIPANT_ID)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = COL_SOLO)
    private Boolean solo;

    @Column(name = COL_ROLE)
    @Enumerated(EnumType.STRING)
    private TaskRole role;

    @ManyToOne
    @JoinColumn(name = User.COL_USER_ID)
    private User user;

    @ManyToOne
    @JoinColumn(name = Task.COL_TASK_ID)
    private Task task;

    @ManyToMany
    @JoinTable(name = COL_JTABLE_TASKPARTICIPANT_GROUP,
            joinColumns = {@JoinColumn(name = COL_TASKPARTICIPANT_ID)},
            inverseJoinColumns = {@JoinColumn(name = Group.COL_GROUP_ID)})
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
