package com.melkamar.deadlines.services.helpers.urgency;

import com.melkamar.deadlines.model.task.DeadlineTask;
import com.melkamar.deadlines.model.task.GrowingTask;
import com.melkamar.deadlines.model.task.TaskStatus;
import com.melkamar.deadlines.services.DateConvertor;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 06.04.2016 19:19
 * <p>
 * Calculates urgency of a task.
 * <h1>DeadlineTask computation is based on the formula:</h1>
 * urgency = |(a^reserve - 1) * b| + b + c for reserve <=0
 * urgency = a^(-reserve) * b + c,  for reserve >=0
 * <p>
 * Parameter a determines how "sharp" the urgency curve is.
 * Parameter b determines the "range" in which the urgency values will lie.
 * Parameter c determines offset of the curve in the y-axis.
 * <p>
 * <p>
 * Where reserve = [remaining time] / [remaining work] + [remaining time] / d.
 * Parameter d denotes the "significance" of remaining time. So if there are two tasks
 * with equal time-to-work ratio (e.g. 6:3 vs 4:2), the reserve will be greater for the task that has longer
 * until its deadline.
 * This way the tasks that are nearing their deadlines are prioritized over tasks that have equal
 * "reserve", but longer until their deadline.
 * <p>
 * E.g. there is 10 hours left on a task and 25 hours is left to its deadline.
 * Reserve therefore equals 25/10 = 2.5
 * <h1>GrowingTask computation is based on the formula:</h1>
 * linear ...
 */
@Service("urgencyComputer")
public class DefaultUrgencyComputer implements UrgencyComputer {
    private static final double a = 1.1;
    private static final double b = 100;
    private static final double c = 0;
    private static final double d = 5;

    private static final double maxUrgency = 200; // Maximum urgency, cut anything greater

    @Override
    public double computeDeadlineTaskUrgency(DeadlineTask task) {
        if (task.getStatus() == TaskStatus.CANCELLED || task.getStatus() == TaskStatus.COMPLETED) {
            return 0;
        }

        double remainingWork = getRemainingWork(task);
        if (remainingWork <= 0) {
            return 0;
        }

        double remainingTime = getRemainingTime(task);

        double reserve = remainingTime / d + remainingTime / remainingWork;

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
        task.getGrowspeed();

        double hoursSinceUpdate = DateConvertor.dateToLocalDateTime(task.getUrgency().getLastUpdate()).until(LocalDateTime.now(), ChronoUnit.HOURS);
        double increment = task.getGrowspeed() * hoursSinceUpdate;
        double res = task.getUrgency().getValue() + increment;
        return Math.min(res, maxUrgency);
    }


    private double getRemainingTime(DeadlineTask task) {
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime deadlineTime = DateConvertor.dateToLocalDateTime(task.getDeadline());
        return currentTime.until(deadlineTime, ChronoUnit.HOURS);
    }

    private double getRemainingWork(DeadlineTask task) {
        return task.getWorkEstimate() - task.getManhoursWorked();
    }

}
