package com.melkamar.deadlines.services.helpers.urgency;

import com.melkamar.deadlines.model.task.DeadlineTask;
import com.melkamar.deadlines.model.task.GrowingTask;
import com.melkamar.deadlines.model.task.Task;
import com.melkamar.deadlines.model.task.Urgency;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 06.04.2016 19:18
 */
public interface UrgencyComputer {
    public double computeDeadlineTaskUrgency(DeadlineTask task);
    public double computeGrowingTaskUrgency(GrowingTask task);
}
