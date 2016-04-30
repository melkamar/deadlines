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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.melkamar.deadlines.model.User;

import javax.persistence.*;

/**
 * @author Martin Melka
 */
@Entity
public class TaskWork {
    static final String COL_TASKWORK_ID = "TASKWORK_ID";
    static final String COL_MANHOURS = "MANHOURS";
    static final String COL_OWNING_TASK_ID = "OWNING_TASK_ID";

    @Id
    @Column(name = COL_TASKWORK_ID, nullable = false)
    @GeneratedValue(strategy = GenerationType.TABLE)
    Long id;

    @Column(name = COL_MANHOURS, nullable = false)
    private final Double manhours;

    @ManyToOne
    @JoinColumn(name = User.COL_USER_ID)
    @JsonIgnore
    private final User workedBy;

    @Column(name = COL_OWNING_TASK_ID)
    private Long ownerTaskId;

    public TaskWork(Double manhours, User workedBy) {
        this.manhours = manhours;
        this.workedBy = workedBy;
    }

    public TaskWork() {
        this.manhours = 0d;
        this.workedBy = null;
    }

    public Long getId() {
        return id;
    }

    public Double getManhours() {
        return manhours;
    }

    public User getWorkedBy() {
        return workedBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaskWork)) return false;

        TaskWork taskWork = (TaskWork) o;

        return id != null ? id.equals(taskWork.id) : taskWork.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @JsonProperty(value = "userId")
    public Long getUserId(){
        return this.workedBy.getId();
    }
}
