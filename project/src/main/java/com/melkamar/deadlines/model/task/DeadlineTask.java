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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import com.melkamar.deadlines.controllers.JsonViews;
import com.melkamar.deadlines.services.helpers.UrgencyComputer;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Martin Melka
 */
@Entity
@DiscriminatorValue("Deadline")
public class DeadlineTask extends Task {
    public static final String COL_DATE_DEADLINE = "DATE_DEADLINE";

    @Column(name = COL_DATE_DEADLINE)
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    @JsonView(JsonViews.Task.Basic.class)
    protected Date deadline;

    public DeadlineTask() {
        super();
    }

    public DeadlineTask(Date dateCreated, Date deadline) {
        super(dateCreated);
        this.deadline = deadline;
    }

    /***************************************************************/

    @Override
    public String toString() {
        return super.toString() + "DeadlineTask{" +
                "deadline=" + deadline +
                '}';
    }

    @Override
    public void updateUrgency(UrgencyComputer computer) {
        this.urgency.update(computer.computeDeadlineTaskUrgency(this));
    }

    @Override
    public String taskTypeString() {
        return "DEADLINE";
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }
}
