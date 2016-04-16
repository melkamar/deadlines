package com.melkamar.deadlines.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.melkamar.deadlines.controllers.JsonViews;

import javax.persistence.*;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 28.03.2016 11:11
 */
@Entity
@Table(name = "GROUP_MEMBER")
public class GroupMember {
    public final static String COL_GROUP_MEMBER_ID = "GROUP_MEMBER_ID";
    public final static String COL_ROLE = "ROLE";

    @Id
    @Column(name = COL_GROUP_MEMBER_ID)
    @GeneratedValue(strategy = GenerationType.TABLE)
    @JsonIgnore
    private Long id;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = User.COL_USER_ID)
    @JsonView(JsonViews.GroupMember.Basic.class)
    private final User user;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = Group.COL_GROUP_ID)
    @JsonBackReference
    private final Group group;

    @Column(name = COL_ROLE)
    @Enumerated(EnumType.STRING)
    @JsonView(JsonViews.GroupMember.Basic.class)
    private MemberRole role;

    public GroupMember(User user, Group group, MemberRole role) {
        this.user = user;
        this.group = group;
        this.role = role;
    }

    public GroupMember() {
        this.user = null;
        this.group = null;
        this.role = MemberRole.MEMBER;
    }

    public MemberRole getRole() {
        return role;
    }

    public Group getGroup() {
        return group;
    }

    public User getUser() {
        return user;
    }

    public Long getId() {
        return id;
    }

    public void setRole(MemberRole role) {
        this.role = role;
    }
}
