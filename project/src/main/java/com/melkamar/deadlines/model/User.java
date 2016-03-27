package com.melkamar.deadlines.model;

import com.melkamar.deadlines.model.offer.MembershipOffer;
import com.melkamar.deadlines.model.offer.Offer;
import com.melkamar.deadlines.model.offer.UserTaskSharingOffer;
import com.melkamar.deadlines.model.task.Task;
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
    public final static String COL_USER_ID = "USER_ID";
    public final static String COL_USERNAME = "USERNAME";
    public final static String COL_EMAIL = "EMAIL";
    public final static String COL_PASSWORD_HASH = "PWDHASH";
    public final static String COL_PASSWORD_SALT = "PWDSALT";
    public final static String COL_NAME = "NAME";
    public final static String COL_JTABLE_MEMBEROF_GROUP = "MEMBEROF_GROUP";
    public final static String COL_JTABLE_MANAGEROF_GROUP = "MANAGEROF_GROUP";

    @Id
    @Column(name = COL_USER_ID)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = COL_USERNAME, nullable = false, unique = true)
    private final String username;

    @Column(name = COL_EMAIL)
    private String email;

    @Column(name = COL_PASSWORD_HASH, nullable = false)
    private final String passwordHash;

    @Column(name = COL_PASSWORD_SALT, nullable = false)
    private final String passwordSalt;

    @Column(name = COL_NAME)
    private String name;


    public User() {
        this.username = null;
        this.passwordHash = null;
        this.passwordSalt = null;
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
    @JoinTable(name = COL_JTABLE_MEMBEROF_GROUP,
            joinColumns = {@JoinColumn(name = COL_USER_ID)},
            inverseJoinColumns = {@JoinColumn(name = Group.COL_GROUP_ID )}
    )
    private Set<Group> memberOf = new HashSet<>();

    @ManyToMany()
    @JoinTable(name = COL_JTABLE_MANAGEROF_GROUP,
            joinColumns = {@JoinColumn(name = COL_USER_ID)},
            inverseJoinColumns = {@JoinColumn(name = Group.COL_GROUP_ID)}
    )
    private Set<Group> managerOf = new HashSet<>();

    @OneToMany(mappedBy = "admin", fetch = FetchType.EAGER)
    private Set<Group> adminOf = new HashSet<>();

    @OneToMany(mappedBy = "offeredTo")
    private Set<MembershipOffer> membershipOffers = new HashSet<>();

    @OneToMany(mappedBy = "offeredTo")
    private Set<UserTaskSharingOffer> taskOffers = new HashSet<>();

    /*************************************************************/
    public boolean addParticipant(TaskParticipant participant) {
        if (participants.contains(participant))
            return false;

        participants.add(participant);
        return true;
    }

    public boolean addAdminOf(Group group){
        System.out.println("User#addAdminOf: "+group);
        return adminOf.add(group);
    }

    public boolean isAdminOf(Group group){
        System.out.println("CURRENT admin groups -----");
        for (Group iter: adminOf) System.out.println("    " + iter + " ||| "+iter.getId());

        System.out.println("Comparing with: "+group+" (id = "+group.getId());
        return adminOf.contains(group);
    }

    /**
     * Lists all Tasks the User participates in.
     * Serves as a shortcut so that it is not necessary
     * to navigate through the TaskParticipant.
     * @return Set of Tasks
     */
    public Set<Task> tasksOfUser(){
        // TODO: 27.03.2016 Implement
        return new HashSet<Task>();
    }

    /*************************************************************/

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;

        User user = (User) o;

        if (!username.equals(user.username)) return false;
        if (email != null ? !email.equals(user.email) : user.email != null) return false;
        if (!passwordHash.equals(user.passwordHash)) return false;
        if (!passwordSalt.equals(user.passwordSalt)) return false;
        return name != null ? name.equals(user.name) : user.name == null;

    }

    @Override
    public int hashCode() {
        int result = username.hashCode();
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + passwordHash.hashCode();
        result = 31 * result + passwordSalt.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
