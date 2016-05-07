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

import org.springframework.stereotype.Service;

/**
 * This class defines string constants, such as error messages, used in the application.
 *
 * @author Martin Melka
 */
@Service("stringConstants")
public class StringConstants {
    public final String EXC_PARAM_USERNAME_EMPTY = "Username cannot be empty.";
    public final String EXC_PARAM_PASSWORD_EMPTY = "Password cannot be empty.";
    public final String EXC_PARAM_NAME_EMPTY = "Name cannot be empty.";
    public final String EXC_PARAM_FOUNDER_NULL = "Founder cannot be null.";
    public final String EXC_PARAM_TASK_CREATOR_NULL = "Task creator cannot be null.";
    public final String EXC_PARAM_TASK_DEADLINE_NULL = "Task deadline cannot be null.";
    public final String EXC_PARAM_GROUP_NULL = "Group cannot be null.";
    public final String EXC_PARAM_USER_NULL = "User cannot be null.";
    public final String EXC_PARAM_MEMBER_ROLE_NULL = "Member role cannot be null.";
    public final String EXC_PARAM_TASK_GROWSPEED_INVALID = "Task grow speed must be >=0.";
    public final String EXC_PARAM_TASK_MANHOURS_INVALID = "Manhours done must be >=0.";
//    public final String EXC_PARAM_NOT_NULL = "All parameters need to be non-null";
    public final String EXC_FILTER_WRONG_VALUE = "Wrong value for filter {0}.";
    public final String EXC_SET_DEADLINE_OR_GROWSPEED = "Either hoursToPeak or deadline must be set and not both.";
    public final String EXC_PARAM_MEMBER_ROLE_INVALID = "Member role can be one of member, manager, admin. Provided: [{0}].";

    public final String EXC_PARAM_ALL_NEED_NOT_NULL = "All parameters need to be not null.";

    public final String EXC_BODY_MUST_HAVE_FIELD = "Request body must contain field: {0}";

    public final String EXC_ALREADY_EXISTS_USER_NAME = "User with username [{0}] already exists.";
    public final String EXC_ALREADY_EXISTS_TASK_PARTICIPANT = "Task participant already exists! {0} -- {1}";
    public final String EXC_ALREADY_EXISTS_GROUP_NAME = "Group with a name [{0}] already exists.";
    public final String EXC_ALREADY_EXISTS_GROUP_MEMBER = "Group member already exists! {0} -- {1}";
    public final String EXC_ALREADY_EXISTS_TASK_OF_GROUP = "Task [{0}] is already shared with group [{1}].";
    public final String EXC_ALREADY_EXISTS_TASK_OFFER = "Task offer of task [{0}] for [{1}] already exists.";
    public final String EXC_ALREADY_EXISTS_MEMBERSHIP_OFFER = "Membership offer for [{0}] to join [{1}] already exists.";

    public final String EXC_USER_NOT_PARTICIPANT = "User [{0}] is not a participant in a task [{1}].";
    public final String EXC_USER_NOT_PARTICIPANT_IS_NULL = "TaskParticipant of User [{0}] and task [{1}] with ID {2} is NULL. This should not happen.";

    public final String EXC_USER_NOT_WORKER = "User [{0}] is not a worker on task [{1}].";
    public final String EXC_USER_NOT_MEMBER_OF_GROUP = "User [{0}] is not a member of group [{1}].";
    public final String EXC_GROUP_NOT_IN_TASK = "Group [{0}] is not participating on a task [{1}].";

    public final String EXC_OFFER_NOT_FOR_USER = "Offer [{0}] is not for user [{1}].";
    public final String EXC_OFFER_NOT_FOR_GROUP = "Offer [{0}] is not for group [{1}].";

    public final String EXC_GROUP_PERMISSION = "Permission denied. Operation requires {0}. User [{1}] is not that in group [{2}].";

    public final String EXC_NOT_ALLOWED_DEMOTE_ADMIN = "Admin cannot be demoted to manager. He needs to grant Admin role to someone else first.";
    public final String EXC_NOT_ALLOWED_ADMIN_LEAVE_OR_REMOVE = "Admin cannot leave or be removed from the group. Either give the Admin rights to another user, or delete the group.";
    public final String EXC_NOT_ALLOWED_SETING_DEADLINE_ON_GROWING = "Deadline cannot be set on a non-DeadlineTask.";
    public final String EXC_NOT_ALLOWED_RESET_URGENCY_ON_DEADLINE = "Urgency cannot be reset on a DeadlineTask.";

    public final String EXC_DOES_NOT_EXIST_USER_ID = "User with id {0} does not exist.";
    public final String EXC_DOES_NOT_EXIST_USERNAME = "User with username {0} does not exist.";
    public final String EXC_DOES_NOT_EXIST_TASK = "Task with id {0} does not exist.";
    public final String EXC_DOES_NOT_EXIST_OFFER = "Offer with id {0} does not exist.";
    public final String EXC_DOES_NOT_EXIST_GROUP = "Group with id {0} does not exist.";

    public final String EXC_TASK_ROLE_TARGET_USER_NOT_GROUP = "targetGroup=[id] must be supplied with targetUser.";
    public final String EXC_BODY_MUST_ = "targetGroup=[id] must be supplied with targetUser.";

    /******************************************************************************************************************/
    public final static String FILTER_ROLE_WORKER = "worker";
    public final static String FILTER_ROLE_WATCHER = "watcher";
    public final static String FILTER_TYPE_DEADLINE = "deadline";
    public final static String FILTER_TYPE_GROWING = "growing";
    public final static String FILTER_STATUS_OPEN = "open";
    public final static String FILTER_STATUS_IN_PROGRESS = "inprogress";
    public final static String FILTER_STATUS_CANCELLED = "cancelled";
    public final static String FILTER_STATUS_COMPLETED = "completed";
    public final static String FILTER_PRIORITY_1 = "lowest";
    public final static String FILTER_PRIORITY_2 = "low";
    public final static String FILTER_PRIORITY_3 = "normal";
    public final static String FILTER_PRIORITY_4 = "high";
    public final static String FILTER_PRIORITY_5 = "highest";
}
