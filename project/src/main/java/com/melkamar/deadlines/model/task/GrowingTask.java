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

package com.melkamar.deadlines.model.task;

import com.fasterxml.jackson.annotation.JsonView;
import com.melkamar.deadlines.controllers.JsonViews;
import com.melkamar.deadlines.services.helpers.UrgencyComputer;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.Date;

/**
 * @author Martin Melka
 */
@Entity
@DiscriminatorValue("Growing")
public class GrowingTask extends Task {
    public static final String COL_HOURS_TO_PEAK = "HOURS_TO_PEAK";

    @Column(name = COL_HOURS_TO_PEAK)
    @JsonView(JsonViews.Always.class)
    protected Double hoursToPeak;

    public GrowingTask() {
        super();
    }

    public GrowingTask(Date dateCreated, Double hoursToPeak) {
        super(dateCreated);
        this.hoursToPeak = hoursToPeak;
    }

    @Override
    public String toString() {
        return super.toString() + "GrowingTask{" +
                "hoursToPeak=" + hoursToPeak +
                '}';
    }

    @Override
    public void updateUrgency(UrgencyComputer computer) {
        this.urgency.update(computer.computeGrowingTaskUrgency(this));
    }

    @Override
    public String taskTypeString() {
        return "GROWING";
    }

    public Double getHoursToPeak() {
        return hoursToPeak;
    }
}
