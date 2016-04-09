package com.melkamar.deadlines.controllers.stubs;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.melkamar.deadlines.model.task.Priority;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 09.04.2016 13:19
 */
public class TaskStub {
    private String name;
    private String description;
    private Priority priority;
    private double workEstimate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private Date deadline;
    private Double growSpeed;

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

    public Double getGrowSpeed() {
        return growSpeed;
    }
}
