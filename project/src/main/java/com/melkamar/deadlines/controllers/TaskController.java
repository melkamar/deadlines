package com.melkamar.deadlines.controllers;

import com.melkamar.deadlines.config.ErrorCodes;
import com.melkamar.deadlines.config.StringConstants;
import com.melkamar.deadlines.controllers.stubs.TaskReportRequestBody;
import com.melkamar.deadlines.controllers.stubs.TaskSharingRequestBody;
import com.melkamar.deadlines.controllers.stubs.TaskStub;
import com.melkamar.deadlines.dao.processing.*;
import com.melkamar.deadlines.exceptions.*;
import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.misc.ErrorResponse;
import com.melkamar.deadlines.model.task.*;
import com.melkamar.deadlines.services.DateConvertor;
import com.melkamar.deadlines.services.api.GroupAPI;
import com.melkamar.deadlines.services.api.SharingAPI;
import com.melkamar.deadlines.services.api.TaskAPI;
import com.melkamar.deadlines.services.api.UserAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 09.04.2016 10:58
 */
@Controller
@RequestMapping(value = "/task")
public class TaskController {
    @Autowired
    private TaskAPI taskAPI;


    @Autowired
    private UserAPI userAPI;
    @Autowired
    private StringConstants stringConstants;
    @Autowired
    private SharingAPI sharingAPI;
    @Autowired
    private GroupAPI groupAPI;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity listTasks(@AuthenticationPrincipal Long userId,
                                    @RequestParam(value = "order", required = false) String order,
                                    @RequestParam(value = "orderdirection", required = false) String orderDirection,
                                    @RequestParam(value = "rolefilter", required = false) String roleFilter,
                                    @RequestParam(value = "typefilter", required = false) String typeFilter,
                                    @RequestParam(value = "statusfilter", required = false) String statusFilter,
                                    @RequestParam(value = "priorityfilter", required = false) String[] priorityFilters) throws DoesNotExistException {
        User user = userAPI.getUser(userId);

        TaskOrdering taskOrdering = getTaskOrderingFromParam(order, orderDirection);
        TaskFilter[] filters = null;

        try {
            filters = getTaskFiltersFromParam(user, roleFilter, typeFilter, statusFilter, priorityFilters);
        } catch (WrongParameterException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(ErrorCodes.WRONG_FILTER_VALUE, e.getMessage()));
        }

        List<Task> tasks = taskAPI.listTasks(user, taskOrdering, filters);
        return ResponseEntity.ok().body(tasks);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity createTask(@AuthenticationPrincipal Long userId, @RequestBody TaskStub taskStub) throws WrongParameterException, DoesNotExistException {
        User creator = userAPI.getUser(userId);

        checkIfDeadlineXorGrowing(taskStub);

        Task task;
        if (taskStub.getDeadline() != null) {
            task = taskAPI.createTask(creator, taskStub.getName(), taskStub.getDescription(), taskStub.getPriority(), taskStub.getWorkEstimate(),
                    DateConvertor.dateToLocalDateTime(taskStub.getDeadline()));
        } else {
            task = taskAPI.createTask(creator, taskStub.getName(), taskStub.getDescription(), taskStub.getPriority(), taskStub.getWorkEstimate(),
                    taskStub.getGrowSpeed());
        }

        return ResponseEntity.ok().body(task);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity getTaskDetails(@AuthenticationPrincipal Long userId, @PathVariable("id") Long id) throws DoesNotExistException {
        User user = userAPI.getUser(userId);

        try {
            Task task = taskAPI.getTask(user, id);
            return ResponseEntity.ok(task);
        } catch (DoesNotExistException e) {
            return ResponseEntity.notFound().build();
        } catch (NotMemberOfException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ErrorCodes.USER_NOT_PARTICIPANT, e.getMessage()));
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity editTask(@AuthenticationPrincipal Long userId, @PathVariable("id") Long id, @RequestBody TaskStub taskStub) throws DoesNotExistException, WrongParameterException {
        User user = userAPI.getUser(userId);

        try {
            Task task = taskAPI.getTask(user, id);

            if (taskStub.getGrowSpeed() != null) {
                return ResponseEntity.badRequest().body(new ErrorResponse(ErrorCodes.CANNOT_EDIT_GROWSPEED, "Growing speed of a task cannot be changed."));
            }

            taskAPI.editTask(user, task, taskStub.getDescription(), DateConvertor.dateToLocalDateTime(taskStub.getDeadline()), taskStub.getWorkEstimate(), taskStub.getPriority());

            if (taskStub.getStatus() != null) {
                taskAPI.setTaskStatus(user, task, taskStub.getStatus());
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
     * Offers task to a list of Users and Groups.
     * @param userId
     * @param id
     * @param requestBody
     * @return
     * @throws DoesNotExistException
     * @throws WrongParameterException
     */
    @RequestMapping(value = "/share/{id}", method = RequestMethod.POST)
    public ResponseEntity shareTask(@AuthenticationPrincipal Long userId,
                                    @PathVariable("id") Long id,
                                    @RequestBody TaskSharingRequestBody requestBody) throws DoesNotExistException, WrongParameterException {
        User user = userAPI.getUser(userId);

        try {
            Task task = taskAPI.getTask(user, id);

            for (Long offeredToId : requestBody.getUsers()) {
                User offeredTo = userAPI.getUser(offeredToId);
                sharingAPI.offerTaskSharing(user, task, offeredTo);
            }

            for (Long offeredToId : requestBody.getGroups()) {
                Group offeredTo = groupAPI.getGroup(offeredToId);
                sharingAPI.offerTaskSharing(user, task, offeredTo);
            }

            return ResponseEntity.ok(task);

        } catch (NotMemberOfException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ErrorCodes.USER_NOT_PARTICIPANT, e.getMessage()));
        } catch (AlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ErrorCodes.USER_ALREADY_PARTICIPANT, e.getMessage()));
        }
    }

    @RequestMapping(value = "/leave/{id}", method = RequestMethod.POST)
    public ResponseEntity leaveTask(@AuthenticationPrincipal Long userId, @PathVariable("id") Long id) throws DoesNotExistException {
        User user = userAPI.getUser(userId);

        try {
            Task task = taskAPI.getTask(user, id);

            userAPI.leaveTask(user, task);

            return ResponseEntity.ok(task);

        } catch (NotMemberOfException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ErrorCodes.USER_NOT_PARTICIPANT, e.getMessage()));
        }
    }

    /**
     * Change a role of a user at a task (WATCHER/WORKER).
     *
     * @param userId
     * @param id
     * @param targetUserId
     * @param targetGroupId
     * @param targetRole
     * @return
     * @throws DoesNotExistException
     * @throws WrongParameterException
     */
    @RequestMapping(value = "/role/{id}", method = RequestMethod.POST)
    public ResponseEntity changeTaskRole(@AuthenticationPrincipal Long userId,
                                         @PathVariable("id") Long id,
                                         @RequestParam(value = "targetUser", required = false) Long targetUserId,
                                         @RequestParam(value = "targetGroup", required = false) Long targetGroupId,
                                         @RequestParam(value = "newRole", required = true) String targetRole) throws DoesNotExistException, WrongParameterException {
        User user = userAPI.getUser(userId);
        Task task;
        try {
            task = taskAPI.getTask(user, id);
        } catch (NotMemberOfException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ErrorCodes.USER_NOT_PARTICIPANT, e.getMessage()));
        }

        if (targetUserId == null) { // No target user -> apply to caller
            try {
                taskAPI.setTaskRole(user, task, TaskRole.valueOf(targetRole.toUpperCase()));
            } catch (NotMemberOfException e) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ErrorCodes.USER_NOT_PARTICIPANT, e.getMessage()));
            }
        } else { // Target user exists -> change someone else's role
            if (targetGroupId == null)
                throw new WrongParameterException(stringConstants.EXC_TASK_ROLE_TARGET_USER_NOT_GROUP);

            User targetUser = userAPI.getUser(targetUserId);
            Group targetGroup = groupAPI.getGroup(targetGroupId);

            // TODO: 10.04.2016 Check if FORBIDDEN is okay for this use (or use BAD REQUEST?)

            try {
                taskAPI.setTaskRole(targetUser, task, TaskRole.valueOf(targetRole.toUpperCase()), user, targetGroup);
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

    @RequestMapping(value = "/report/{id}", method = RequestMethod.POST)
    public ResponseEntity reportWork(@AuthenticationPrincipal Long userId,
                                     @PathVariable("id") Long id,
                                     @RequestBody TaskReportRequestBody requestBody) throws DoesNotExistException {
        User user = userAPI.getUser(userId);

        try {
            Task task = taskAPI.getTask(user, id);
            taskAPI.reportWork(user, task, requestBody.getWorkDone());

            return ResponseEntity.ok(task);

        } catch (NotMemberOfException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ErrorCodes.USER_NOT_PARTICIPANT, e.getMessage()));
        } catch (TaskPermissionException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ErrorCodes.USER_NOT_WORKER, e.getMessage()));
        } catch (WrongParameterException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ErrorCodes.WRONG_PARAMETERS, e.getMessage()));
        }
    }


    private void checkIfDeadlineXorGrowing(TaskStub taskStub) throws WrongParameterException {
        if ((taskStub.getDeadline() == null && taskStub.getGrowSpeed() == null)
                || (taskStub.getDeadline() != null && taskStub.getGrowSpeed() != null)) {
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
            switch (roleFilter) {
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
            switch (typeFilter) {
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
            switch (statusFilter) {
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

                switch (value) {
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

        boolean descending = true;
        if (direction == null || direction.isEmpty() || !direction.equals("asc")) {
            descending = true;
        } else {
            descending = false;
        }

        switch (order) {
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
