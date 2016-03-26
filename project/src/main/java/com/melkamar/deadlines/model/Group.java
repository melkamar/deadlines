package com.melkamar.deadlines.model;

import com.melkamar.deadlines.model.task.Task;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 25.03.2016 17:38
 */
@Entity
@Table(name = "GROUP_TABLE")
public class Group {
    @Id
    @Column(name = "GROUP_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "DESCRIPTION")
    String description;

    @Column(name = "NAME", unique = true, nullable = false)
    final String name;

    @ManyToMany(mappedBy = "groups")
    private Set<TaskParticipant> participants = new HashSet<>();

    public Group(){
        this.name = null;
    }

    public Group(String name, String description){
        this.name = name;
        this.description = description;
    }


    public int removeMember(User user){
        return 0;
    }

    public int removeTask(Task task){
        return 0;
    }

    /**
     * Removes the current Admin's role and sets it to the given User.
     */
    public int setAdmin(User user){
        return 0;
    }

    public int setManager(User user, boolean manager){
        return 0;
    }
}
