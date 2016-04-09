package com.melkamar.deadlines.config;

import org.springframework.stereotype.Service;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 08.04.2016 14:12
 */
public class ErrorCodes {
    public static final int USER_ALREADY_EXISTS = 1;
    public static final int WRONG_PARAMETERS = 2;
    public static final int WRONG_FILTER_VALUE = 3;
    public static final int USER_NOT_WORKER = 4;
    public static final int USER_NOT_PARTICIPANT = 5;
    public static final int INVALID_CREDENTIALS = 6;
    public static final int CANNOT_SET_DEADLINE_ON_NONDEADLINE_TASK = 7;
    public static final int CANNOT_EDIT_GROWSPEED = 8;
}
