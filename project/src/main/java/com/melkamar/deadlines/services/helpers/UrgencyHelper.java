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
 * @author Martin Melka
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
