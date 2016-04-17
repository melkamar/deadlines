package com.melkamar.deadlines.services.api.implementation;

import com.melkamar.deadlines.dao.task.TaskDAO;
import com.melkamar.deadlines.model.task.Task;
import com.melkamar.deadlines.model.task.TaskStatus;
import com.melkamar.deadlines.services.api.InternalApi;
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
@Service("internalApi")
@Transactional
public class InternalApiImpl implements InternalApi {
    @Autowired
    private TaskDAO taskDAO;
    @Autowired
    private UrgencyHelper urgencyHelper;


    @Override
    public void updateAllUrgencies(){
        updateAllUrgencies(false);
    }

    @Override
    public void updateAllUrgencies(boolean force){
        for (TaskStatus status: Task.activeStates){
            for (Task task: taskDAO.findByStatus(status)){
                urgencyHelper.updateUrgency(task, force);
            }
        }
    }
}
