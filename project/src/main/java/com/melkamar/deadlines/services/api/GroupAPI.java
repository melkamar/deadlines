package com.melkamar.deadlines.services.api;

import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.task.Task;
import org.springframework.stereotype.Service;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 31.03.2016 10:27
 */
@Service
public class GroupAPI {
    public Group createGroup(User founder, String name, String description){
        // TODO: 31.03.2016 Implement
        throw new NotImplementedException();
    }

    public List<Group> listGroups(){
        // TODO: 31.03.2016 Implement
        throw new NotImplementedException();
    }

    public Group getGroup(User executor, Long groupId){
        // TODO: 31.03.2016 Implement
        throw new NotImplementedException();
    }

    public void removeMember(User manager, Group group, User toRemove){
        // TODO: 31.03.2016 Implement
        throw new NotImplementedException();
    }

    public void leaveTask(User manager, Group group, Task task){
        // TODO: 31.03.2016 Implement
        throw new NotImplementedException();
    }

    public Group editDetails(User manager, Group group, String newDescription){
        // TODO: 31.03.2016 Implement
        throw new NotImplementedException();
    }

    public Group setManagerRole(User executor, Group group, User targetUser, boolean newStatus){
        // TODO: 31.03.2016 Implement
        throw new NotImplementedException();
    }

    public Group changeAdmin(User executor, Group group, User newAdmin){
        // TODO: 31.03.2016 Implement
        throw new NotImplementedException();
    }

    public void deleteGroup(User executor, Group group){
        // TODO: 31.03.2016 Implement
        throw new NotImplementedException();
    }










    public class GroupFilter{
        public List<Group> filter(List<Group> groups){
            // TODO: 31.03.2016 Implement
            throw new NotImplementedException();
        }
    }
}
