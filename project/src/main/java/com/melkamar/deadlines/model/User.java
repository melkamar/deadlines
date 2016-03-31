package com.melkamar.deadlines.model;

import com.melkamar.deadlines.model.offer.MembershipOffer;
import com.melkamar.deadlines.model.offer.UserTaskSharingOffer;
import com.melkamar.deadlines.model.task.Task;
import com.melkamar.deadlines.services.PasswordHashGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Id
    @Column(name = COL_USER_ID)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = COL_USERNAME, nullable = false, unique = true)
    private final String username;

    @Column(name = COL_EMAIL)
    private String email;

    @Column(name = COL_PASSWORD_HASH, nullable = false)
    private String passwordHash;

    @Column(name = COL_PASSWORD_SALT, nullable = false)
    private String passwordSalt;

    @Column(name = COL_NAME)
    private String name;


    public User() {
        this.username = null;
        this.passwordHash = null;
        this.passwordSalt = null;
    }

    public User(String username, PasswordHashGenerator.HashAndSalt hashAndSalt) {
        this.username = username;
        this.passwordHash = hashAndSalt.hash;
        this.passwordSalt = hashAndSalt.salt;
    }

    /* RELATIONS */
    @OneToMany(mappedBy = "user", cascade = CascadeType.MERGE)
    private Set<TaskParticipant> participants = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<GroupMember> memberAs = new HashSet<>();

    @OneToMany(mappedBy = "offeredTo", cascade = CascadeType.MERGE)
    private Set<MembershipOffer> membershipOffers = new HashSet<>();

    @OneToMany(mappedBy = "offeredTo", cascade = CascadeType.MERGE)
    private Set<UserTaskSharingOffer> taskOffers = new HashSet<>();

    /*************************************************************/
    public boolean addParticipant(TaskParticipant participant) {
        return participants.add(participant);
    }

    public boolean removeParticipant(TaskParticipant participant){
        return participants.remove(participant);
    }

    public boolean addGroupMember(GroupMember groupMember){
        return memberAs.add(groupMember);
    }

    public boolean removeGroupMember(GroupMember groupMember){
        return memberAs.remove(groupMember);
    }

    /**
     * Lists all Tasks the User participates in.
     * Serves as a shortcut so that it is not necessary
     * to navigate through the TaskParticipant.
     *
     * @return Set of Tasks
     */
    public Set<Task> tasksOfUser() {
        Set<Task> tasks = new HashSet<Task>(participants.size());
        tasks.addAll(participants.stream().map(TaskParticipant::getTask).collect(Collectors.toList()));

        return tasks;
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

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNewPassword(PasswordHashGenerator.HashAndSalt hashAndSalt){
        this.passwordHash = hashAndSalt.hash;
        this.passwordSalt = hashAndSalt.salt;
    }


    public Set<GroupMember> getMemberAs() {
        return memberAs;
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

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
