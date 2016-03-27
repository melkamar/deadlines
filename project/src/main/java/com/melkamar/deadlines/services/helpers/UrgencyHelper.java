package com.melkamar.deadlines.services.helpers;

import com.melkamar.deadlines.model.task.Task;
import com.melkamar.deadlines.model.task.Urgency;
import org.springframework.stereotype.Service;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 27.03.2016 13:32
 */
@Service
public class UrgencyHelper {
    /**
     * Recalculate Urgency for a given Task and save it in the Task.
     * @param task Task for which to calculate the Urgency.
     * @return Calculated Urgency.
     */
    public Urgency computeUrgency(Task task){
        // TODO: 27.03.2016
        return null;
    }
}
