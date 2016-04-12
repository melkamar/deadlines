package com.melkamar.deadlines.services.helpers;

import com.melkamar.deadlines.model.task.Task;
import com.melkamar.deadlines.utils.DateConvertor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 27.03.2016 13:32
 */
@Service
public class UrgencyHelper {
    @Autowired
    private UrgencyComputer urgencyComputer;
    private static int CHECK_INTERVAL_MINUTES = 30;

    /**
     * Recalculate Urgency for a given Task and save it in the Task.
     *
     * @param task  Task for which to calculate the Urgency.
     * @param force When true, urgency will be updated. If false, urgency will
     *              only be updated if {@link UrgencyHelper#needsUpdate(Task)} is true.
     */
    @Transactional
    public void updateUrgency(Task task, boolean force) {
        System.out.println("updateUrgency: "+task.getName());
        if (force) {
            task.updateUrgency(urgencyComputer);
        } else {
            if (needsUpdate(task)) {
                task.updateUrgency(urgencyComputer);
            }
        }
    }

    @Transactional
    public void resetUrgency(Task task){
        task.resetUrgency();
    }

    /**
     * Checks if enough time has passed and whether a Task Urgency object should be updated.
     *
     * @return True if the object should be updated, false otherwise.
     */
    public boolean needsUpdate(Task task) {
        return DateConvertor.dateToLocalDateTime(task.getUrgency().getLastUpdate())
                .until(LocalDateTime.now(), ChronoUnit.MINUTES) > CHECK_INTERVAL_MINUTES;
    }
}
