package com.melkamar.deadlines.model;

import com.melkamar.deadlines.model.offer.MembershipOffer;
import com.melkamar.deadlines.model.offer.Offer;
import com.melkamar.deadlines.model.offer.UserTaskSharingOffer;
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
    @Column(name = COL_USER_ID)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = COL_USERNAME, nullable = false)
    private String username;

    @Column(name = COL_EMAIL)
    private String email;

    @Column(name = COL_PASSWORD_HASH, nullable = false)
    private String passwordHash;

    @Column(name = COL_PASSWORD_SALT, nullable = false)
    private String passwordSalt;

    @Column(name = COL_NAME)
    private String name;


    public User() {

    }

    public User(String username, String passwordHash, String passwordSalt) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.passwordSalt = passwordSalt;
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

    @ManyToMany()
    @JoinTable(name = "MANAGEROF_GROUP",
            joinColumns = {@JoinColumn(name = "USER_ID")},
            inverseJoinColumns = {@JoinColumn(name = "GROUP_ID")}
    )
    private Set<Group> managerOf = new HashSet<>();

    @OneToMany(mappedBy = "admin", fetch = FetchType.EAGER)
    private Set<Group> adminOf = new HashSet<>();

    @OneToMany(mappedBy = "offeredTo")
    private Set<MembershipOffer> membershipOffers = new HashSet<>();

    @OneToMany(mappedBy = "offeredTo")
    private Set<UserTaskSharingOffer> taskOffers = new HashSet<>();

    public boolean addParticipant(TaskParticipant participant) {
        if (participants.contains(participant))
            return false;

        participants.add(participant);
        return true;
    }

    public final static String COL_USER_ID = "USER_ID";
    public final static String COL_USERNAME = "USERNAME";
    public final static String COL_EMAIL = "EMAIL";
    public final static String COL_PASSWORD_HASH = "PWDHASH";
    public final static String COL_PASSWORD_SALT = "PWDSALT";
    public final static String COL_NAME = "NAME";


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
