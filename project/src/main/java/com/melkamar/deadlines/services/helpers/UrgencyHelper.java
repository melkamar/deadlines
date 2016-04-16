package com.melkamar.deadlines.services.helpers;

import com.melkamar.deadlines.model.task.Task;
import com.melkamar.deadlines.utils.DateConvertor;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    Logger logger = Logger.getLogger(this.getClass());
    @Autowired
    private UrgencyComputer urgencyComputer;

    @Value("update.urgency.interval")
    private static int CHECK_INTERVAL_MILLIS = 30;

    /**
     * Recalculate Urgency for a given Task and save it in the Task.
     *
     * @param task  Task for which to calculate the Urgency.
     * @param force When true, urgency will be updated. If false, urgency will
     *              only be updated if {@link UrgencyHelper#needsUpdate(Task)} is true.
     */
    @Transactional
    public void updateUrgency(Task task, boolean force) {
        if (force) {
            task.updateUrgency(urgencyComputer);
        } else {
            if (needsUpdate(task)) {
                logger.info("Updating urgency. "+task);
                task.updateUrgency(urgencyComputer);
            } else {
                logger.info("Skipping updating urgency, has been updated recently. "+task);
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
                .until(LocalDateTime.now(), ChronoUnit.MILLIS) > CHECK_INTERVAL_MILLIS;
    }
}
