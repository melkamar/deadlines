/*
 * Copyright (c) 2016 Martin Melka
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.melkamar.deadlines.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.melkamar.deadlines.controllers.JsonViews;
import com.melkamar.deadlines.model.task.Task;
import com.melkamar.deadlines.model.task.TaskRole;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Martin Melka
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
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    @Column(name = COL_SOLO)
    @JsonView(JsonViews.TaskParticipant.Basic.class)
    private Boolean solo = false;

    @Column(name = COL_ROLE)
    @Enumerated(EnumType.STRING)
    @JsonView(JsonViews.TaskParticipant.Basic.class)
    private TaskRole role;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = User.COL_USER_ID)
    @JsonView(JsonViews.TaskParticipant.Basic.class)
    private final User user;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = Task.COL_TASK_ID)
    private final Task task;

    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(name = COL_JTABLE_TASKPARTICIPANT_GROUP,
            joinColumns = {@JoinColumn(name = COL_TASKPARTICIPANT_ID)},
            inverseJoinColumns = {@JoinColumn(name = Group.COL_GROUP_ID)})
//    @JsonView(JsonViews.TaskParticipant.Basic.class)   -- No need to show group, it will be shown in sharedGroups of Task
    private Set<Group> groups = new HashSet<>();

    /*************************************************************/

    /**
     * Returns true if the group was not already present.
     * False if the group was already in the set.
     */
    public boolean addGroup(Group group) {
        return groups.add(group);
    }

    /**
     * Returns true if an existing group was removed from the set.
     * False if the group was not in the set.
     */
    public boolean removeGroup(Group group) {
        return groups.remove(group);
    }

    /*************************************************************/


    public TaskParticipant() {
        this.user = null;
        this.task = null;
    }

    public TaskParticipant(User user, Task task) {
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

    /******************************************************************************************************************/
    // Getters used by Jackson for serializing
//    @JsonProperty(value = "userId")
//    @JsonView(JsonViews.Always.class)
//    public Long getUserId() {
//        return this.getUser().getId();
//    }
//
//    @JsonProperty(value = "username")
//    @JsonView(JsonViews.Always.class)
//    public String getUserUsername() {
//        return this.getUser().getUsername();
//    }
//
//    @JsonProperty(value = "groupIds")
//    @JsonView(JsonViews.Always.class)
//    public List<Long> getGroupsIds() {
//        return this.getGroups().stream().map(Group::getId).collect(Collectors.toList());
//    }
    @JsonProperty("taskId")
    @JsonView(JsonViews.TaskParticipant.ShowTaskId.class)
    public Long getTaskId() {
        return this.task.getId();
    }

    @JsonProperty("taskName")
    @JsonView(JsonViews.TaskParticipant.ShowTaskName.class)
    public String getTaskName() {
        return this.task.getName();
    }
}
