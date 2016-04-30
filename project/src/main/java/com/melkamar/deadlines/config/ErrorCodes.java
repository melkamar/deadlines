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

package com.melkamar.deadlines.config;

/**
 * @author Martin Melka
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
    public static final int WRONG_MEMBERROLE_VALUE = 16; // Member role parameter is wrong (listGroups)
    public static final int GROUP_NAME_ALREADY_EXISTS = 17;
    public static final int TASK_NOT_OF_GROUP = 18; // A task is not shared with a group - manager cannot assign roles on it
    public static final int CANNOT_DEMOTE_ADMIN = 19; // Calling user is attempting to set MANAGER role to an ADMIN
    public static final int CANNOT_REMOVE_ADMIN = 20; // Calling user is attempting to remove an ADMIN from the group
    public static final int CANNOT_RESET_NON_GROWING = 21; // Calling user is attempting to reset deadline on a non-growing task
}
