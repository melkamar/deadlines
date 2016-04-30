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

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.melkamar.deadlines.controllers.JsonViews;
import com.melkamar.deadlines.model.offer.MembershipOffer;
import com.melkamar.deadlines.model.offer.UserTaskSharingOffer;
import com.melkamar.deadlines.model.task.Task;
import com.melkamar.deadlines.services.security.HashAndSaltGenerator;
import org.hibernate.validator.constraints.Email;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 25.03.2016 12:49
 */

@Entity
@Table(name = "USER_TABLE")
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class User {
    public final static String COL_USER_ID = "USER_ID";
    public final static String COL_USERNAME = "USERNAME";
    public final static String COL_EMAIL = "EMAIL";
    public final static String COL_PASSWORD_HASH = "PWDHASH";
    public final static String COL_PASSWORD_SALT = "PWDSALT";
    public final static String COL_NAME = "NAME";

    @Id
    @Column(name = COL_USER_ID)
    @GeneratedValue(strategy = GenerationType.TABLE)
    @JsonProperty
    @JsonView(JsonViews.User.Minimal.class)
    private Long id;

    @JsonProperty
    @Column(name = COL_USERNAME, nullable = false, unique = true)
    @JsonView(JsonViews.User.Minimal.class)
    private final String username;

    @JsonProperty
    @Column(name = COL_EMAIL)
    @JsonView(JsonViews.User.Basic.class)
    @Email
    private String email;

    @JsonIgnore
    @Column(name = COL_PASSWORD_HASH, nullable = false)
    private String passwordHash;

    @JsonIgnore
    @Column(name = COL_PASSWORD_SALT, nullable = false)
    private String passwordSalt;

    @JsonProperty
    @Column(name = COL_NAME)
    @JsonView(JsonViews.User.Basic.class)
    private String name;

    /* RELATIONS */
    @OneToMany(mappedBy = "user", cascade = CascadeType.MERGE)
    @JsonIgnore
    private Set<TaskParticipant> participants = new HashSet<>();

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private Set<GroupMember> memberAs = new HashSet<>();

    @OneToMany(mappedBy = "offeredTo", cascade = CascadeType.MERGE)
    @JsonIgnore
    private Set<MembershipOffer> membershipOffers = new HashSet<>();

    @OneToMany(mappedBy = "offeredTo", cascade = CascadeType.MERGE)
    @JsonIgnore
    private Set<UserTaskSharingOffer> taskOffers = new HashSet<>();


    /*************************************************************/
    public User() {
        this.username = null;
        this.passwordHash = null;
        this.passwordSalt = null;
    }

    public User(String username, HashAndSaltGenerator.HashAndSalt hashAndSalt) {
        this.username = username;
        this.passwordHash = hashAndSalt.hash;
        this.passwordSalt = hashAndSalt.salt;
    }

    /*************************************************************/
    public boolean addParticipant(TaskParticipant participant) {
        return participants.add(participant);
    }

    public boolean removeParticipant(TaskParticipant participant) {
        return participants.remove(participant);
    }

    public boolean addGroupMember(GroupMember groupMember) {
        return memberAs.add(groupMember);
    }

    public boolean removeGroupMember(GroupMember groupMember) {
        return memberAs.remove(groupMember);
    }

    public boolean addTaskSharingOffer(UserTaskSharingOffer offer) {
        return taskOffers.add(offer);
    }

    public boolean removeTaskSharingOffer(UserTaskSharingOffer offer) {
        return taskOffers.remove(offer);
    }


    public boolean addMembershipOffer(MembershipOffer offer) {
        return membershipOffers.add(offer);
    }

    public boolean removeMembershipOffer(MembershipOffer offer) {
        return membershipOffers.remove(offer);
    }

    /**
     * Lists all Tasks the User participates in.
     * Serves as a shortcut so that it is not necessary
     * to navigate through the TaskParticipant.
     *
     * @return Set of Tasks
     */
    public Set<Task> getTasksOfUser() {
        return participants.stream().map(TaskParticipant::getTask).collect(Collectors.toSet());
    }

    /**
     * Lists all Grups the User belongs to.
     * Serves as a shortcut so that it is not necessary
     * to navigate through the GroupMember.
     *
     * @return List of Gruops
     */
    @JsonIgnore
    public List<Group> getGroupsOfUser() {
        return memberAs.stream().map(GroupMember::getGroup).collect(Collectors.toList());
    }

    /*************************************************************/

    public Long getId() {
        return id;
    }


    public String getUsername() {
        return username;
    }

    /*************************************************************/

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

    public void setNewPassword(HashAndSaltGenerator.HashAndSalt hashAndSalt) {
        this.passwordHash = hashAndSalt.hash;
        this.passwordSalt = hashAndSalt.salt;
    }

    public Set<MembershipOffer> getMembershipOffers() {
        return membershipOffers;
    }

    public Set<UserTaskSharingOffer> getTaskOffers() {
        return taskOffers;
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
