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
    public final String EXC_PARAM_GROUP_NULL = "Group cannot be null.";
    public final String EXC_PARAM_USER_NULL = "User cannot be null.";
    public final String EXC_PARAM_MEMBER_ROLE_NULL = "Member role cannot be null.";
    public final String EXC_PARAM_TASK_GROWSPEED_INVALID = "Task grow speed must be >=0.";
    public final String EXC_PARAM_TASK_MANHOURS_INVALID = "Manhours done must be >=0.";

    public final String EXC_PARAM_ALL_NEED_NULL = "All parameters need to be not null.";

    public final String EXC_ALREADY_EXISTS_TASK_PARTICIPANT = "Task participant already exists! {0} -- {1}";
    public final String EXC_ALREADY_EXISTS_GROUP_MEMBER = "Group member already exists! {0} -- {1}";

    public final String EXC_USER_NOT_PARTICIPANT = "User [{0}] is not a participant in a task [{1}] with ID {2}.";
    public final String EXC_USER_NOT_PARTICIPANT_IS_NULL = "TaskParticipant of User [{0}] and task [{1}] with ID {2} is NULL. This should not happen.";

    public final String EXC_USER_NOT_WORKER = "User [{0}] is not a worker on task [{1}] with ID {2}. Therefore he/she cannot report work done on it.";
    public final String EXC_USER_NOT_MEMBER_CANT_PROMOTE = "User [{0}] is not a member of group [{1}]. He cannot be promoted.";

    public final String EXC_GROUP_PERMISSION = "Permission denied. Operation requires {0}. User [{1}] is not that in group [{2}].";
}
