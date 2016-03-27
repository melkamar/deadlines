package com.melkamar.deadlines.model;

import com.melkamar.deadlines.model.offer.GroupTaskSharingOffer;
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
    Long id;

    @Column(name = COL_GROUP_DESCRIPTION)
    String description;

    @Column(name = COL_GROUP_NAME, unique = true, nullable = false)
    final String name;

    @ManyToMany(mappedBy = "groups")
    private Set<TaskParticipant> participants = new HashSet<>();

    @ManyToMany(mappedBy = "memberOf")
    private Set<User> members = new HashSet<>();

    @ManyToMany(mappedBy = "managerOf")
    private Set<User> managers = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = COL_GROUP_JCOL_ADMIN, referencedColumnName = User.COL_USER_ID)
    private User admin;

    @OneToMany(mappedBy = "offeredTo")
    private Set<GroupTaskSharingOffer> taskOffers = new HashSet<>();

    public Group(){
        this.name = null;
    }

    public Group(String name){
        this.name = name;
    }


    public int removeMember(User user){
        return 0;
    }

    public int removeTask(Task task){
        return 0;
    }

    @Deprecated
    public int addMember(User user){
        this.members.add(user);
        user.getMemberOf().add(this);
        return 0;
    }

    /**
     * Removes the current Admin's role and sets it to the given User.
     */
    public int setAdmin(User user){
        // TODO
        members.remove(user);
        managers.remove(user);
        admin = user;

        user.getMemberOf().remove(this);
        user.getManagerOf().remove(this);
        user.getAdminOf().add(this);
        return 0;
    }

    public int setManager(User user, boolean manager){
        // TODO
        members.remove(user);
        managers.add(user);

        user.getMemberOf().remove(this);
        user.getManagerOf().add(this);
        return 0;
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

    public Set<User> getMembers() {
        return members;
    }

    public Set<User> getManagers() {
        return managers;
    }

    public User getAdmin() {
        return admin;
    }

    public void setDescription(String description) {
        this.description = description;
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

        if (description != null ? !description.equals(group.description) : group.description != null) return false;
        if (name != null ? !name.equals(group.name) : group.name != null) return false;
        if (participants != null ? !participants.equals(group.participants) : group.participants != null) return false;
        if (members != null ? !members.equals(group.members) : group.members != null) return false;
        if (managers != null ? !managers.equals(group.managers) : group.managers != null) return false;
        if (admin != null ? !admin.equals(group.admin) : group.admin != null) return false;
        return taskOffers != null ? taskOffers.equals(group.taskOffers) : group.taskOffers == null;

    }

    @Override
    public int hashCode() {
        int result = description != null ? description.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (participants != null ? participants.hashCode() : 0);
        result = 31 * result + (members != null ? members.hashCode() : 0);
        result = 31 * result + (managers != null ? managers.hashCode() : 0);
        result = 31 * result + (admin != null ? admin.hashCode() : 0);
        result = 31 * result + (taskOffers != null ? taskOffers.hashCode() : 0);
        return result;
    }
}
