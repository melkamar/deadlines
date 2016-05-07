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

package com.melkamar.deadlines.controllers.httpbodies;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.melkamar.deadlines.model.task.Priority;
import com.melkamar.deadlines.model.task.TaskStatus;

import java.util.Date;
import java.util.List;

/**
 * @author Martin Melka
 */
public class TaskCreateRequestBody {
    public String name;
    public String description;
    public Priority priority;
    public double workEstimate;
    public TaskStatus status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    public Date deadline;
    public Double hoursToPeak;
    public List<Long> groupIds;

    public List<Long> getGroupIds() {
        return groupIds;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Priority getPriority() {
        return priority;
    }

    public double getWorkEstimate() {
        return workEstimate;
    }

    public Date getDeadline() {
        return deadline;
    }

    public Double getHoursToPeak() {
        return hoursToPeak;
    }

    public TaskStatus getStatus() {
        return status;
    }
}
