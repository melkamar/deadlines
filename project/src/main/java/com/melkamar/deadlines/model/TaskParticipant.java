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
    private Boolean solo = false;

    @Column(name = COL_ROLE)
    @Enumerated(EnumType.STRING)
    private TaskRole role;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = User.COL_USER_ID)
    private final User user;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = Task.COL_TASK_ID)
    private final Task task;

    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(name = COL_JTABLE_TASKPARTICIPANT_GROUP,
            joinColumns = {@JoinColumn(name = COL_TASKPARTICIPANT_ID)},
            inverseJoinColumns = {@JoinColumn(name = Group.COL_GROUP_ID)})
    private Set<Group> groups = new HashSet<>();

    /*************************************************************/

    /**
     * Returns true if the group was not already present.
     * False if the group was already in the set.
     */
    public boolean addGroup(Group group){
        return groups.add(group);
    }

    /**
     * Returns true if an existing group was removed from the set.
     * False if the group was not in the set.
     */
    public boolean removeGroup(Group group){
        return groups.remove(group);
    }

    /*************************************************************/


    public TaskParticipant(){
        this.user = null;
        this.task = null;
    }

    public TaskParticipant(User user, Task task){
        this.user = user;
        this.task = task;
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

    public void setRole(TaskRole newRole) {
        this.role = newRole;
    }

    public Task getTask() {
        return task;
    }

    public void setSolo(Boolean solo) {
        this.solo = solo;
    }
}
