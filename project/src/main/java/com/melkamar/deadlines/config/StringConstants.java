package com.melkamar.deadlines.config;

import org.springframework.stereotype.Service;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 26.03.2016 15:06
 */
@Service("stringConstants")
public class StringConstants {
    public final String EXC_PARAM_USERNAME_EMPTY = "Username cannot be empty.";
    public final String EXC_PARAM_PASSWORD_EMPTY = "Password cannot be empty.";
    public final String EXC_PARAM_NAME_EMPTY = "Name cannot be empty.";
    public final String EXC_PARAM_FOUNDER_NULL = "Founder cannot be null.";
    public final String EXC_PARAM_TASK_CREATOR_NULL = "Task creator cannot be null.";
    public final String EXC_PARAM_TASK_DEADLINE_NULL = "Task deadline cannot be null.";
    public final String EXC_PARAM_TASK_GROWSPEED_INVALID = "Task grow speed must be >=0.";
    public final String EXC_ALREADY_EXISTS_TASK_PARTICIPANT = "Task participant already exists! {0} -- {1}";
}
