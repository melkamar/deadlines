package com.melkamar.deadlines.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 25.03.2016 12:49
 */

@Entity
@Table(name = "USER")
public class User {
    @Id
    @Column(name = "USER_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "USERNAME", nullable = false)
    private String username;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "PWDHASH", nullable = false)
    private String passwordHash;

    @Column(name = "PWDSALT", nullable = false)
    private String passwordSalt;

    @Column(name = "NAME")
    private String name;



    public User() {

    }

    /* RELATIONS */
    @OneToMany(mappedBy = "user")
    private Set<TaskParticipant> participants = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "MEMBEROF_GROUP",
            joinColumns = {@JoinColumn(name = "USER_ID")},
            inverseJoinColumns = {@JoinColumn(name = "GROUP_ID")}
    )
    private Set<Group> memberOf = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "MANAGEROF_GROUP",
            joinColumns = {@JoinColumn(name = "USER_ID")},
            inverseJoinColumns = {@JoinColumn(name = "GROUP_ID")}
    )
    private Set<Group> managerOf = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "MEMBEROF_GROUP",
            joinColumns = {@JoinColumn(name = "USER_ID")},
            inverseJoinColumns = {@JoinColumn(name = "GROUP_ID")}
    )
    private Set<Group> adminOf = new HashSet<>();


    public boolean addParticipant(TaskParticipant participant) {
        if (participants.contains(participant))
            return false;

        participants.add(participant);
        return true;
    }


    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getPasswordSalt() {
        return passwordSalt;
    }

    public String getName() {
        return name;
    }

    public Set<TaskParticipant> getParticipants() {
        return participants;
    }

    public Set<Group> getMemberOf() {
        return memberOf;
    }

    public Set<Group> getManagerOf() {
        return managerOf;
    }

    public Set<Group> getAdminOf() {
        return adminOf;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }
}
