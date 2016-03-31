package com.melkamar.deadlines.model;

import com.melkamar.deadlines.model.offer.GroupTaskSharingOffer;
import com.melkamar.deadlines.model.task.Task;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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
    Long id;

    @Column(name = COL_GROUP_DESCRIPTION)
    String description;

    @Column(name = COL_GROUP_NAME, unique = true, nullable = false)
    final String name;

    @ManyToMany(mappedBy = "groups")
    private Set<TaskParticipant> participants = new HashSet<>();

    @ManyToMany(mappedBy = "sharedGroups")
    private Set<Task> sharedTasks = new HashSet<>();

    @OneToMany(mappedBy = "group")
    private Set<GroupMember> members = new HashSet<>();

    @OneToMany(mappedBy = "offeredTo")
    private Set<GroupTaskSharingOffer> taskOffers = new HashSet<>();

    public Group(){
        this.name = null;
    }

    public Group(String name){
        this.name = name;
    }


    public int removeMember(User user){
        // TODO: 31.03.2016
        throw new NotImplementedException();
    }

    public int removeTask(Task task){
        // TODO: 31.03.2016
        throw new NotImplementedException();
    }

    public boolean removeTaskParticipant(TaskParticipant taskParticipant){
        return participants.remove(taskParticipant);
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
}
