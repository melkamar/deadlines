package com.melkamar.deadlines.controllers.requestbodies;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.melkamar.deadlines.model.task.Priority;
import com.melkamar.deadlines.model.task.TaskStatus;

import java.util.Date;
import java.util.List;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 09.04.2016 13:19
 */
public class TaskCreateRequestBody {
    private String name;
    private String description;
    private Priority priority;
    private double workEstimate;
    private TaskStatus status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private Date deadline;
    private Double hoursToPeak;
    private List<Long> groupIds;

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
