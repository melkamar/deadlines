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

package com.melkamar.deadlines.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.melkamar.deadlines.config.ErrorCodes;
import com.melkamar.deadlines.config.StringConstants;
import com.melkamar.deadlines.controllers.httpbodies.ErrorResponse;
import com.melkamar.deadlines.controllers.httpbodies.TaskCreateRequestBody;
import com.melkamar.deadlines.controllers.httpbodies.TaskReportRequestBody;
import com.melkamar.deadlines.controllers.httpbodies.TaskSharingRequestBody;
import com.melkamar.deadlines.dao.processing.*;
import com.melkamar.deadlines.exceptions.*;
import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.task.*;
import com.melkamar.deadlines.services.api.GroupApi;
import com.melkamar.deadlines.services.api.SharingApi;
import com.melkamar.deadlines.services.api.TaskApi;
import com.melkamar.deadlines.services.api.UserApi;
import com.melkamar.deadlines.utils.DateConvertor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * This Controller class handles incoming requests made to an address "/task/**".
 * <p>
 * Actions performed by the controller deal with task listing, creating, reporting.
 *
 * @author Martin Melka
 */
@Controller
@RequestMapping(value = "/task")
public class TaskController {
    @Autowired
    private TaskApi taskApi;


    @Autowired
    private UserApi userApi;
    @Autowired
    private StringConstants stringConstants;
    @Autowired
    private SharingApi sharingApi;
    @Autowired
    private GroupApi groupApi;

    /**
     * Lists tasks of the calling user with given filters and ordering.
     *
     * @param userId          ID of the authenticated user making the request.
     * @param groupId         Optional parameter. ID of the {@link Group} for which to list tasks. If null, all tasks from
     *                        all groups of the user will be listed.
     * @param order           Optional parameter. Specifies ordering of the listed tasks. Accepted values are listed
     *                        in {@link TaskOrdering} class.
     *                        Default ordering is based on Urgency, descending.
     * @param orderDirection  Optional parameter. Specifies direction of the ordering.
     *                        Accepted values are "asc" for ascending or "desc" for descending.
     *                        Default direction is descending.
     * @param roleFilter      Optional parameter. Filters tasks based on the User's role in them. Accepted values are listed
     *                        in {@link StringConstants} as "FILTER_ROLE_*".
     * @param typeFilter      Optional parameter. Filters tasks based on their type. Accepted values are listed
     *                        in {@link StringConstants} as "FILTER_TYPE_*".
     * @param statusFilter    Optional parameter. Filters tasks based on their status. Accepted values are listed
     *                        in {@link StringConstants} as "FILTER_STATUS_*".
     * @param priorityFilters Optional parameter. Filters tasks based on their priority. Accepted values are listed
     *                        in {@link StringConstants} as "FILTER_PRIORITY_*".
     *                        Multiple priorities may be specified in format "?priorityfilter=val1&priorityfilter=val2&..."
     * @return A {@link ResponseEntity} object containing details of the response to the client.
     * @throws DoesNotExistException if the authenticated user ID does not exist. This should not happen.
     */
    @JsonView(JsonViews.Controller.TaskList.class)
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity listTasks(@AuthenticationPrincipal Long userId,
                                    @RequestParam(value = "group", required = false) Long groupId,
                                    @RequestParam(value = "order", required = false) String order,
                                    @RequestParam(value = "orderdirection", required = false) String orderDirection,
                                    @RequestParam(value = "rolefilter", required = false) String roleFilter,
                                    @RequestParam(value = "typefilter", required = false) String typeFilter,
                                    @RequestParam(value = "statusfilter", required = false) String statusFilter,
                                    @RequestParam(value = "priorityfilter", required = false) String[] priorityFilters) throws DoesNotExistException {
        User user = userApi.getUser(userId);

        TaskOrdering taskOrdering = getTaskOrderingFromParam(order, orderDirection);
        TaskFilter[] filters;

        try {
            filters = getTaskFiltersFromParam(user, roleFilter, typeFilter, statusFilter, priorityFilters);
        } catch (WrongParameterException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(ErrorCodes.WRONG_FILTER_VALUE, e.getMessage()));
        }

        List<Task> tasks;
        if (groupId == null) {
            tasks = taskApi.listTasks(user, taskOrdering, filters);
        } else {
            Group group = groupApi.getGroup(groupId);

            try {
                tasks = taskApi.listTasks(user, group, taskOrdering, filters);
            } catch (NotMemberOfException e) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ErrorCodes.USER_NOT_MEMBER_OF_GROUP, e.getMessage()));
            } catch (GroupPermissionException e) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ErrorCodes.USER_NOT_ENOUGH_GROUP_PERMISSION, e.getMessage()));
            }
        }


        return ResponseEntity.ok().body(tasks);
    }

    /**
     * Creates a task.
     *
     * @param userId                ID of the authenticated user making the request.
     * @param taskCreateRequestBody A {@link TaskCreateRequestBody} object containing details of the task to be created.
     * @return A {@link ResponseEntity} object containing details of the response to the client.
     * @throws WrongParameterException if the request is missing required parameters.
     * @throws DoesNotExistException   if the authenticated user ID does not exist. This should not happen.
     */
    @JsonView(JsonViews.Controller.TaskDetails.class)
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity createTask(@AuthenticationPrincipal Long userId, @RequestBody TaskCreateRequestBody taskCreateRequestBody) throws WrongParameterException, DoesNotExistException {
        User creator = userApi.getUser(userId);

        checkIfDeadlineXorGrowing(taskCreateRequestBody);

        List<Group> groups = null;
        if (taskCreateRequestBody.getGroupIds() != null && taskCreateRequestBody.getGroupIds().size() > 0) {
            groups = groupsFromIds(taskCreateRequestBody.getGroupIds());
        }

        Task task;
        try {
            if (taskCreateRequestBody.getDeadline() != null) {
                task = taskApi.createTask(creator,
                        taskCreateRequestBody.getName(),
                        taskCreateRequestBody.getDescription(),
                        taskCreateRequestBody.getPriority(),
                        taskCreateRequestBody.getWorkEstimate(),
                        groups,
                        DateConvertor.dateToLocalDateTime(taskCreateRequestBody.getDeadline()));
            } else {
                task = taskApi.createTask(creator,
                        taskCreateRequestBody.getName(),
                        taskCreateRequestBody.getDescription(),
                        taskCreateRequestBody.getPriority(),
                        taskCreateRequestBody.getWorkEstimate(),
                        groups,
                        taskCreateRequestBody.getHoursToPeak());
            }
        } catch (GroupPermissionException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ErrorCodes.USER_NOT_ENOUGH_GROUP_PERMISSION, e.getMessage()));
        } catch (NotMemberOfException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ErrorCodes.USER_NOT_MEMBER_OF_GROUP, e.getMessage()));
        }

        return ResponseEntity.created(URI.create("/task/" + task.getId())).body(task);

    }

    private List<Group> groupsFromIds(List<Long> ids) throws DoesNotExistException {
        List<Group> groups = new ArrayList<>(ids.size());
        for (Long id : ids) {
            groups.add(groupApi.getGroup(id));
        }

        return groups;
    }

    /**
     * Shows details of a task.
     *
     * @param userId ID of the authenticated user making the request.
     * @param id     ID of the task to show.
     * @return A {@link ResponseEntity} object containing details of the response to the client.
     * @throws DoesNotExistException if the authenticated user ID or a {@link Task} with the given id does not exist.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @JsonView(JsonViews.Controller.TaskDetails.class)
    public ResponseEntity taskDetails(@AuthenticationPrincipal Long userId,
                                      @PathVariable("id") Long id) throws DoesNotExistException {
        User user = userApi.getUser(userId);

        try {
            Task task = taskApi.getTask(user, id);
            return ResponseEntity.ok(task);
        } catch (DoesNotExistException e) {
            return ResponseEntity.notFound().build();
        } catch (NotMemberOfException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ErrorCodes.USER_NOT_PARTICIPANT, e.getMessage()));
        }
    }

    /**
     * Edits an existing task.
     *
     * @param userId                ID of the authenticated user making the request.
     * @param id                    ID of the task to edit.
     * @param taskCreateRequestBody A {@link TaskCreateRequestBody} object containing details of the edit.
     * @return A {@link ResponseEntity} object containing details of the response to the client.
     * @throws DoesNotExistException   if the authenticated user ID or a {@link Task} with the given id does not exist.
     * @throws WrongParameterException if the request is missing required parameters.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity editTask(@AuthenticationPrincipal Long userId,
                                   @PathVariable("id") Long id,
                                   @RequestBody TaskCreateRequestBody taskCreateRequestBody) throws DoesNotExistException, WrongParameterException {
        User user = userApi.getUser(userId);

        try {
            Task task = taskApi.getTask(user, id);

            if (taskCreateRequestBody.getHoursToPeak() != null) {
                return ResponseEntity.badRequest().body(new ErrorResponse(ErrorCodes.CANNOT_EDIT_HRS_TO_PEAK, "Hours to peak of a task cannot be changed."));
            }

            taskApi.editTask(user, task, taskCreateRequestBody.getDescription(), DateConvertor.dateToLocalDateTime(taskCreateRequestBody.getDeadline()), taskCreateRequestBody.getWorkEstimate(), taskCreateRequestBody.getPriority());

            if (taskCreateRequestBody.getStatus() != null) {
                taskApi.setTaskStatus(user, task, taskCreateRequestBody.getStatus());
            }

            return ResponseEntity.ok(task);

        } catch (NotAllowedException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ErrorCodes.CANNOT_SET_DEADLINE_ON_NONDEADLINE_TASK, e.getMessage()));
        } catch (NotMemberOfException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ErrorCodes.USER_NOT_PARTICIPANT, e.getMessage()));
        } catch (TaskPermissionException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ErrorCodes.USER_NOT_WORKER, e.getMessage()));
        }
    }

    /**
     * Resets the urgency of a growing task.
     * <p>
     * Cannot be used to reset the urgency of a deadline task.
     *
     * @param userId ID of the authenticated user making the request.
     * @param taskId ID of the task to reset.
     * @return A {@link ResponseEntity} object containing details of the response to the client.
     * @throws DoesNotExistException if the authenticated user ID or a {@link Task} with the given id does not exist.
     */
    @RequestMapping(value = "/{id}/reseturgency", method = RequestMethod.POST)
    public ResponseEntity resetUrgency(@AuthenticationPrincipal Long userId,
                                       @PathVariable("id") Long taskId) throws DoesNotExistException {
        User user = userApi.getUser(userId);
        Task task = taskApi.getTask(taskId);

        try {
            taskApi.resetUrgency(user, task);
            return ResponseEntity.ok().build();
        } catch (NotAllowedException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ErrorCodes.CANNOT_RESET_NON_GROWING, e.getMessage()));
        } catch (NotMemberOfException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ErrorCodes.USER_NOT_PARTICIPANT, e.getMessage()));
        } catch (TaskPermissionException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ErrorCodes.USER_NOT_WORKER, e.getMessage()));
        }

    }

    /**
     * Offers task sharing to a list of Users and Groups.
     *
     * @param userId      ID of the authenticated user making the request.
     * @param id          ID of the task to share.
     * @param requestBody A {@link TaskSharingRequestBody} object containing details of the task sharing.
     * @return A {@link ResponseEntity} object containing details of the response to the client.
     * @throws DoesNotExistException   if the authenticated user ID or a {@link Task} with the given id does not exist.
     * @throws WrongParameterException if the request is missing required parameters.
     */
    @RequestMapping(value = "/share/{id}", method = RequestMethod.POST)
    public ResponseEntity shareTask(@AuthenticationPrincipal Long userId,
                                    @PathVariable("id") Long id,
                                    @RequestBody TaskSharingRequestBody requestBody) throws DoesNotExistException, WrongParameterException {
        User user = userApi.getUser(userId);

        try {
            Task task = taskApi.getTask(user, id);

            for (Long offeredToId : requestBody.getUsers()) {
                User offeredTo = userApi.getUser(offeredToId);

                try {
                    sharingApi.offerTaskSharing(user, task, offeredTo);
                } catch (AlreadyExistsException e) {
                    // Doesn't matter, just ignore it if user is already a member
                }
            }

            for (Long offeredToId : requestBody.getGroups()) {
                Group offeredTo = groupApi.getGroup(offeredToId);
                try {
                    sharingApi.offerTaskSharing(user, task, offeredTo);
                } catch (AlreadyExistsException e) {
                    // Doesn't matter, just ignore it if user is already a member
                }
            }

            return ResponseEntity.ok(task);

        } catch (NotMemberOfException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ErrorCodes.USER_NOT_PARTICIPANT, e.getMessage()));
//        } catch (AlreadyExistsException e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ErrorCodes.USER_ALREADY_PARTICIPANT, e.getMessage()));
//        }
        }
    }

    /**
     * Removes the calling user from being a participant on a task.
     *
     * @param userId ID of the authenticated user making the request.
     * @param id     ID of the task to leave.
     * @return A {@link ResponseEntity} object containing details of the response to the client.
     * @throws DoesNotExistException if the authenticated user ID or a {@link Task} with the given id does not exist.
     */
    @RequestMapping(value = "/leave/{id}", method = RequestMethod.POST)
    public ResponseEntity leaveTask(@AuthenticationPrincipal Long userId, @PathVariable("id") Long id) throws DoesNotExistException {
        User user = userApi.getUser(userId);

        try {
            Task task = taskApi.getTask(user, id);

            userApi.leaveTask(user, task);

            return ResponseEntity.ok().build();

        } catch (NotMemberOfException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ErrorCodes.USER_NOT_PARTICIPANT, e.getMessage()));
        }
    }

    /**
     * Change a role of a user at a task.
     * <p>
     * Used to change users between watchers and workers.
     * <p>
     * If targetUserId is specified, the role will be changed for this user rather than for the caller. In order to
     * do that, the calling user has to be a Manager of the group with id targetGroupId and the target user has to be a
     * member of that group.
     *
     * @param userId        ID of the authenticated user making the request.
     * @param id            ID of the task at which to change a role.
     * @param targetUserId  Optional parameter. ID of the user whose role should be changed.
     * @param targetGroupId Optional parameter. ID of the group in regard to which the role should be changed.
     * @param targetRole    The new role. Accepted values are values of {@link TaskRole}.
     * @return A {@link ResponseEntity} object containing details of the response to the client.
     * @throws DoesNotExistException   if the authenticated user ID, {@link Task} with the given id or target user or
     *                                 group does not exist.
     * @throws WrongParameterException
     */
    @RequestMapping(value = "/role/{id}", method = RequestMethod.POST)
    public ResponseEntity changeTaskRole(@AuthenticationPrincipal Long userId,
                                         @PathVariable("id") Long id,
                                         @RequestParam(value = "targetUser", required = false) Long targetUserId,
                                         @RequestParam(value = "targetGroup", required = false) Long targetGroupId,
                                         @RequestParam(value = "newRole") String targetRole) throws DoesNotExistException, WrongParameterException {
        User user = userApi.getUser(userId);
        Task task;
        try {
            task = taskApi.getTask(user, id);
        } catch (NotMemberOfException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ErrorCodes.USER_NOT_PARTICIPANT, e.getMessage()));
        }

        if (targetUserId == null) { // No target user -> apply to caller
            try {
                taskApi.setTaskRole(user, task, TaskRole.valueOf(targetRole.toUpperCase()));
            } catch (NotMemberOfException e) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ErrorCodes.USER_NOT_PARTICIPANT, e.getMessage()));
            }
        } else { // Target user exists -> change someone else's role
            if (targetGroupId == null)
                throw new WrongParameterException(stringConstants.EXC_TASK_ROLE_TARGET_USER_NOT_GROUP);

            User targetUser = userApi.getUser(targetUserId);
            Group targetGroup = groupApi.getGroup(targetGroupId);

            try {
                taskApi.setTaskRole(targetUser, task, TaskRole.valueOf(targetRole.toUpperCase()), user, targetGroup);
            } catch (NotMemberOfException e) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ErrorCodes.USER_NOT_MEMBER_OF_GROUP, e.getMessage()));
            } catch (GroupPermissionException e) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ErrorCodes.USER_NOT_ENOUGH_GROUP_PERMISSION, e.getMessage()));
            } catch (NotAllowedException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ErrorCodes.USER_NOT_MEMBER_OF_GROUP, e.getMessage()));
            }
        }

        return ResponseEntity.ok(task);
    }

    /**
     * Reports work done on a task.
     *
     * @param userId      ID of the authenticated user making the request.
     * @param id          ID of the task to report on.
     * @param requestBody A {@link TaskReportRequestBody} object containing details of the work report.
     * @return A {@link ResponseEntity} object containing details of the response to the client.
     * @throws DoesNotExistException if the authenticated user ID or a {@link Task} with the given id does not exist.
     */
    @RequestMapping(value = "/report/{id}", method = RequestMethod.POST)
    public ResponseEntity reportWork(@AuthenticationPrincipal Long userId,
                                     @PathVariable("id") Long id,
                                     @RequestBody TaskReportRequestBody requestBody) throws DoesNotExistException {
        User user = userApi.getUser(userId);

        try {
            Task task = taskApi.getTask(user, id);
            taskApi.reportWork(user, task, requestBody.getWorkDone());

            return ResponseEntity.ok(task);

        } catch (NotMemberOfException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ErrorCodes.USER_NOT_PARTICIPANT, e.getMessage()));
        } catch (TaskPermissionException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ErrorCodes.USER_NOT_WORKER, e.getMessage()));
        } catch (WrongParameterException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ErrorCodes.WRONG_PARAMETERS, e.getMessage()));
        }
    }


    private void checkIfDeadlineXorGrowing(TaskCreateRequestBody taskCreateRequestBody) throws WrongParameterException {
        if ((taskCreateRequestBody.getDeadline() == null && taskCreateRequestBody.getHoursToPeak() == null)
                || (taskCreateRequestBody.getDeadline() != null && taskCreateRequestBody.getHoursToPeak() != null)) {
            throw new WrongParameterException(stringConstants.EXC_SET_DEADLINE_OR_GROWSPEED);
        }
    }

    private TaskFilter[] getTaskFiltersFromParam(User user,
                                                 String roleFilter,
                                                 String typeFilter,
                                                 String statusFilter,
                                                 String[] priorityFilter) throws WrongParameterException {
        List<TaskFilter> filters = new ArrayList<>();

        if (roleFilter != null && !roleFilter.isEmpty()) {
            switch (roleFilter.toLowerCase()) {
                case StringConstants.FILTER_ROLE_WATCHER:
                    filters.add(new TaskFilterRole(user, TaskRole.WATCHER));
                    break;
                case StringConstants.FILTER_ROLE_WORKER:
                    filters.add(new TaskFilterRole(user, TaskRole.WORKER));
                    break;
                default:
                    throw new WrongParameterException(MessageFormat.format(stringConstants.EXC_FILTER_WRONG_VALUE, "rolefilter"));
            }
        }

        if (typeFilter != null && !typeFilter.isEmpty()) {
            switch (typeFilter.toLowerCase()) {
                case StringConstants.FILTER_TYPE_DEADLINE:
                    filters.add(new TaskFilterType(DeadlineTask.class));
                    break;
                case StringConstants.FILTER_TYPE_GROWING:
                    filters.add(new TaskFilterType(GrowingTask.class));
                    break;
                default:
                    throw new WrongParameterException(MessageFormat.format(stringConstants.EXC_FILTER_WRONG_VALUE, "typefilter"));
            }
        }

        if (statusFilter != null && !statusFilter.isEmpty()) {
            switch (statusFilter.toLowerCase()) {
                case StringConstants.FILTER_STATUS_OPEN:
                    filters.add(new TaskFilterStatus(TaskStatus.OPEN));
                    break;
                case StringConstants.FILTER_STATUS_IN_PROGRESS:
                    filters.add(new TaskFilterStatus(TaskStatus.IN_PROGRESS));
                    break;
                case StringConstants.FILTER_STATUS_CANCELLED:
                    filters.add(new TaskFilterStatus(TaskStatus.CANCELLED));
                    break;
                case StringConstants.FILTER_STATUS_COMPLETED:
                    filters.add(new TaskFilterStatus(TaskStatus.COMPLETED));
                    break;
                default:
                    throw new WrongParameterException(MessageFormat.format(stringConstants.EXC_FILTER_WRONG_VALUE, "statusfilter"));
            }
        }

        if (priorityFilter != null && priorityFilter.length > 0) {
            List<Priority> priorities = new ArrayList<>();

            for (String value : priorityFilter) {
                if (value == null || value.isEmpty()) continue;

                switch (value.toLowerCase()) {
                    case StringConstants.FILTER_PRIORITY_1:
                        priorities.add(Priority.LOWEST);
                        break;
                    case StringConstants.FILTER_PRIORITY_2:
                        priorities.add(Priority.LOW);
                        break;
                    case StringConstants.FILTER_PRIORITY_3:
                        priorities.add(Priority.NORMAL);
                        break;
                    case StringConstants.FILTER_PRIORITY_4:
                        priorities.add(Priority.HIGH);
                        break;
                    case StringConstants.FILTER_PRIORITY_5:
                        priorities.add(Priority.HIGHEST);
                        break;
                    default:
                        throw new WrongParameterException(MessageFormat.format(stringConstants.EXC_FILTER_WRONG_VALUE, "priorityfilter"));
                }
            }
            if (priorities.size() > 0)
                filters.add(new TaskFilterPriority(priorities.toArray(new Priority[priorities.size()])));
        }

        return filters.toArray(new TaskFilter[filters.size()]);
    }

    private TaskOrdering getTaskOrderingFromParam(String order, String direction) {
        if (order == null || order.isEmpty()) return TaskOrdering.URGENCY_DESC;

        boolean descending;
        descending = direction == null || direction.isEmpty() || !direction.equals("asc");

        switch (order.toLowerCase()) {
            case TaskOrdering.STR_NAME:
                if (descending) return TaskOrdering.NAME_DESC;
                else return TaskOrdering.NAME_ASC;

            case TaskOrdering.STR_DATE_START:
                if (descending) return TaskOrdering.DATE_START_DESC;
                else return TaskOrdering.DATE_START_ASC;

            case TaskOrdering.STR_DATE_DEADLINE:
                if (descending) return TaskOrdering.DATE_DEADLINE_DESC;
                else return TaskOrdering.DATE_DEADLINE_ASC;

            case TaskOrdering.STR_WORKED_PERCENT:
                if (descending) return TaskOrdering.WORKED_PERCENT_DESC;
                else return TaskOrdering.WORKED_PERCENT_ASC;

            case TaskOrdering.STR_PRIORITY:
                if (descending) return TaskOrdering.PRIORITY_DESC;
                else return TaskOrdering.PRIORITY_ASC;

            case TaskOrdering.STR_URGENCY:
                if (descending) return TaskOrdering.URGENCY_DESC;
                else return TaskOrdering.URGENCY_ASC;

            default:
                return TaskOrdering.URGENCY_DESC;
        }
    }
}
