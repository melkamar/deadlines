package com.melkamar.deadlines.services.helpers.urgency;

import com.melkamar.deadlines.model.task.DeadlineTask;
import com.melkamar.deadlines.model.task.GrowingTask;
import com.melkamar.deadlines.model.task.TaskStatus;
import com.melkamar.deadlines.services.DateConvertor;
import org.springframework.stereotype.Service;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 06.04.2016 19:19
 * <p>
 * Calculates urgency of a task based on the formula:
 * <p>
 * urgency = |(a^reserve - 1) * b| + b + c for reserve <=0
 * urgency = a^(-reserve) * b + c,  for reserve >=0
 * <p>
 * Where reserve = [remaining time] / [remaining work].
 * <p>
 * E.g. there is 10 hours left on a task and 25 hours is left to its deadline.
 * Reserve therefore equals 25/10 = 2.5
 */
@Service("urgencyComputer")
public class DefaultUrgencyComputer implements UrgencyComputer {
    private static final double a = 1.1;
    private static final double b = 100;
    private static final double c = 0;

    @Override
    public double computeDeadlineTaskUrgency(DeadlineTask task) {
        if (task.getStatus() == TaskStatus.CANCELLED || task.getStatus() == TaskStatus.COMPLETED){
            return 0;
        }

        double remainingWork = getRemainingWork(task);
        if (remainingWork <= 0){
            return 0;
        }

        double remainingTime = getRemainingTime(task);

        double reserve = remainingTime / remainingWork;

        double urgencyValue;
        if (reserve >= 0) {
            urgencyValue = Math.pow(a, -reserve) * b + c;
        } else { // reserve < 0
            urgencyValue = Math.abs((Math.pow(a, reserve) - 1) * b) + b + c;
        }

        return urgencyValue;
    }

    @Override
    public double computeGrowingTaskUrgency(GrowingTask task) {
        throw new NotImplementedException();
//        return 0;
    }


    private double getRemainingTime(DeadlineTask task) {
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime deadlineTime = DateConvertor.dateToLocalDateTime(task.getDeadline());
        return currentTime.until(deadlineTime, ChronoUnit.HOURS);
    }

    private double getRemainingWork(DeadlineTask task){
        return task.getWorkEstimate() - task.getManhoursWorked();
    }

}
