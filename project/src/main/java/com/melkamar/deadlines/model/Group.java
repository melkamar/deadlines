package com.melkamar.deadlines.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.melkamar.deadlines.controllers.views.JsonViews;
import com.melkamar.deadlines.model.offer.GroupTaskSharingOffer;
import com.melkamar.deadlines.model.offer.MembershipOffer;
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
    public final static String COL_GROUP_ID = "GROUP_ID";
    public final static String COL_GROUP_NAME = "DESCRIPTION";
    public final static String COL_GROUP_DESCRIPTION = "NAME";
    public final static String COL_GROUP_JCOL_ADMIN = "ADMIN_USER_ID";

    @Id
    @Column(name = COL_GROUP_ID)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(JsonViews.Always.class)
    Long id;

    @Column(name = COL_GROUP_DESCRIPTION)
    @JsonView(JsonViews.Group.Details.class)
    String description;

    @Column(name = COL_GROUP_NAME, unique = true, nullable = false)
    @JsonView(JsonViews.Always.class)
    final String name;

    @ManyToMany(mappedBy = "groups")
    @JsonView(JsonViews.Group.Details.class)
    private Set<TaskParticipant> participants = new HashSet<>();

    @ManyToMany(mappedBy = "sharedGroups")
    @JsonView(JsonViews.Group.Details.class)
    private Set<Task> sharedTasks = new HashSet<>();

    @OneToMany(mappedBy = "group")
    @JsonView(JsonViews.Group.Details.class)
    private Set<GroupMember> members = new HashSet<>();

    // TODO: 02.04.2016 Added cascade-remove - if working, ok
    @OneToMany(mappedBy = "offeredTo", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private Set<GroupTaskSharingOffer> taskOffers = new HashSet<>();

    // TODO: 02.04.2016 Added cascade-remove - if working, ok
    @OneToMany(mappedBy = "group", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private Set<MembershipOffer> membershipOffers = new HashSet<>();

    public Group(){
        this.name = null;
    }

    public Group(String name){
        this.name = name;
    }


    public boolean removeMember(GroupMember groupMember){
        return members.remove(groupMember);
    }

    public boolean removeTask(Task task){
        return sharedTasks.remove(task);
    }

    public boolean removeTaskParticipant(TaskParticipant taskParticipant){
        return participants.remove(taskParticipant);
    }

    public boolean addTaskSharingOffer(GroupTaskSharingOffer offer){
        return taskOffers.add(offer);
    }

    public boolean removeTaskSharingOffer(GroupTaskSharingOffer offer){
        return taskOffers.remove(offer);
    }

    public Set<GroupMember> getMembers() {
        return members;
    }

    public Set<GroupTaskSharingOffer> getTaskOffers() {
        return taskOffers;
    }

    public Set<MembershipOffer> getMembershipOffers() {
        return membershipOffers;
    }

    public boolean addMembershipOffer(MembershipOffer offer) {
        return membershipOffers.add(offer);
    }

    public boolean removeMembershipOffer(MembershipOffer offer) {
        return membershipOffers.remove(offer);
    }



    public boolean addParticipant(TaskParticipant participant) {
        return participants.add(participant);
    }

    public boolean addGroupMember(GroupMember groupMember){
        return members.add(groupMember);
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public Set<TaskParticipant> getParticipants() {
        return participants;
    }

    public Set<GroupMember> getGroupMembers() {
        return members;
    }

    /**
     * Returns a set of GroupMembers with the given role.
     */
    public Set<GroupMember> getGroupMembers(MemberRole role){
        Set<GroupMember> membersOfRole = new HashSet<>();
        for (GroupMember groupMember: members) if (groupMember.getRole() == role) membersOfRole.add(groupMember);

        return membersOfRole;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public Set<Task> getSharedTasks() {
        return sharedTasks;
    }

    public boolean addSharedTask(Task task){
        return sharedTasks.add(task);
    }

    public boolean removeSharedTask(Task task){
        return sharedTasks.remove(task);
    }

    @Override
    public String toString() {
        return "Group{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", name='" + name + '\'' +
                '}' + super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Group)) return false;

        Group group = (Group) o;

        return name.equals(group.name);

    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @JsonView(JsonViews.Group.AdminInfo.class)
    public AdminInfo adminInfo(){
        User admin = this.getGroupMembers(MemberRole.ADMIN).iterator().next().getUser();
        return new AdminInfo(admin.getId(), admin.getUsername(), admin.getName(), admin.getEmail());
    }

    class AdminInfo{
        @JsonView(JsonViews.Group.AdminInfo.class)
        @JsonProperty
        Long id;
        @JsonView(JsonViews.Group.AdminInfo.class)
        @JsonProperty
        String username;
        @JsonView(JsonViews.Group.AdminInfo.class)
        @JsonProperty
        String name;
        @JsonView(JsonViews.Group.AdminInfo.class)
        @JsonProperty
        String email;

        public AdminInfo(Long id, String username, String name, String email) {
            this.id = id;
            this.username = username;
            this.name = name;
            this.email = email;
        }
    }
}
