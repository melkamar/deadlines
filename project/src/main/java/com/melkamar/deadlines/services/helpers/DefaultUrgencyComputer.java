/*
 * Copyright (c) 2016 Martin Melka
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.melkamar.deadlines.services.helpers;

import com.melkamar.deadlines.model.task.DeadlineTask;
import com.melkamar.deadlines.model.task.GrowingTask;
import com.melkamar.deadlines.model.task.TaskStatus;
import com.melkamar.deadlines.utils.DateConvertor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * @author Martin Melka
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
 * Parameter d denotes the "significance" of remaining time. So if there are two jobs
 * with equal time-to-work ratio (e.g. 6:3 vs 4:2), the reserve will be greater for the task that has longer
 * until its deadline.
 * This way the jobs that are nearing their deadlines are prioritized over jobs that have equal
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

    // If no work estimate set or worked more than the estimate, use this number instead.
    // That way urgency will still be generated for proper sorting (even though its values might be misleading)
    private static final double emptyRemainingWork = 0.5;

    @Override
    public double computeDeadlineTaskUrgency(DeadlineTask task) {
        if (task.getStatus() == TaskStatus.CANCELLED || task.getStatus() == TaskStatus.COMPLETED) {
            return 0;
        }

        double remainingWork = getRemainingWork(task);
        if (remainingWork <= 0) {
            remainingWork = emptyRemainingWork;
        }

        double remainingTime = getRemainingTime(task);
        double reserve = remainingTime / d + remainingTime / remainingWork;

        double urgencyValue;
        if (reserve >= 0) {
            urgencyValue = Math.pow(a, -reserve+1) * b + c;
        } else { // reserve < 0
            urgencyValue = Math.abs((Math.pow(a, reserve-1) - 1) * b) + b + c;
        }

        return urgencyValue;
    }

    @Override
    public double computeGrowingTaskUrgency(GrowingTask task) {
        double hoursToPeak = task.getHoursToPeak();
        double incrPerHour;
        if (hoursToPeak < 0.0000001) {
            incrPerHour = 0;
        } else {
            incrPerHour = 1 / hoursToPeak * 100;
        }

        double hoursSinceUpdate = DateConvertor.dateToLocalDateTime(task.getUrgency().getLastUpdate()).until(LocalDateTime.now(), ChronoUnit.SECONDS) / 3600.0;
//        double increment = task.getHoursToPeak() * hoursSinceUpdate;
        double increment = incrPerHour * hoursSinceUpdate;
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
