package com.melkamar.deadlines.services.api;

import com.melkamar.deadlines.dao.task.TaskDAO;
import com.melkamar.deadlines.model.task.Task;
import com.melkamar.deadlines.model.task.TaskStatus;
import com.melkamar.deadlines.services.helpers.UrgencyHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 31.03.2016 10:27
 */
@Service
public class InternalAPI {
    @Autowired
    private TaskDAO taskDAO;
    @Autowired
    private UrgencyHelper urgencyHelper;


    @Transactional
    public void updateAllUrgencies(){
        updateAllUrgencies(false);
    }

    @Transactional
    public void updateAllUrgencies(boolean force){
        List<Task> activeTasks = new ArrayList<>();
        for (TaskStatus status: Task.activeStates){
            for (Task task: taskDAO.findByStatus(status)){
                urgencyHelper.updateUrgency(task, force);
            }
        }
    }
}
