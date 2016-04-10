package com.melkamar.deadlines.config;

import org.springframework.stereotype.Service;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 08.04.2016 14:12
 */
public class ErrorCodes {
    public static final int USER_ALREADY_EXISTS = 1;
    public static final int WRONG_PARAMETERS = 2;
    public static final int WRONG_FILTER_VALUE = 3; // Wrong value for a filter (e.g. rolefilter may only have watcher or worker as value)
    public static final int USER_NOT_WORKER = 4; // Calling user is not a worker on a task.
    public static final int USER_NOT_PARTICIPANT = 5; // Calling user is not a participant on a task.
    public static final int INVALID_CREDENTIALS = 6; // Username:password combination invalid
    public static final int CANNOT_SET_DEADLINE_ON_NONDEADLINE_TASK = 7; // Calling user tried to set deadline on a growing task
    public static final int CANNOT_EDIT_GROWSPEED = 8;
    public static final int USER_ALREADY_PARTICIPANT = 9; // Calling user is already a participant on a task
    public static final int USER_NOT_MEMBER_OF_GROUP = 10; // Calling user is not a member of affected group
    public static final int USER_NOT_ENOUGH_GROUP_PERMISSION = 11; // Calling user does not have enough permissions in affected group
    public static final int OFFER_USER_NOT_OWNER = 12; // The offer is not adressed to the calling user
    public static final int OFFER_OFFERER_NOT_PERMISSION = 13; // The original offerer does not have enough permissions anymore
    public static final int USER_ALREADY_MEMBER = 14; // Calling user is already a member of a group
    public static final int TASK_ALREADY_SHARED_WITH_GROUP = 15; // Task is already shared with a group (called on resolving TaskSharingOffer)

}
