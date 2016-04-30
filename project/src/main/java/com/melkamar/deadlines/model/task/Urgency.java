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

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 25.03.2016 17:45
 */
@Entity
public class Urgency {
    public static final String COL_URGENCY_ID = "URGENCY_ID";
    public static final String COL_LAST_UPDATE = "LAST_UPDATE";
    public static final String COL_VALUE = "VALUE";

    @Id
    @Column(name = COL_URGENCY_ID, nullable = false)
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    @Column(name = COL_LAST_UPDATE, nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    @JsonView(JsonViews.Always.class)
    private Date lastUpdate;

    @Column(name = COL_VALUE, nullable = false)
    @JsonView(JsonViews.Always.class)
    private double value;

    @Deprecated
    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Urgency() {
        this.value = 0;
        lastUpdate = new Date();
    }

    public void update(double newValue) {
        this.value = newValue;
        lastUpdate = new Date();
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Urgency{" +
                "lastUpdate=" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(lastUpdate) +
                ", value=" + value +
                '}';
    }
}
