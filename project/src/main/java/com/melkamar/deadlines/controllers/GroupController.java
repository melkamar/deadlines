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
import com.melkamar.deadlines.controllers.httpbodies.GroupRequestBody;
import com.melkamar.deadlines.controllers.httpbodies.MemberRequestBody;
import com.melkamar.deadlines.controllers.httpbodies.MembershipOfferRequestBody;
import com.melkamar.deadlines.exceptions.*;
import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.MemberRole;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.task.Task;
import com.melkamar.deadlines.services.api.GroupApi;
import com.melkamar.deadlines.services.api.SharingApi;
import com.melkamar.deadlines.services.api.TaskApi;
import com.melkamar.deadlines.services.api.UserApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.text.MessageFormat;

/**
 * This Controller class handles incoming requests made to an address "/group/**".
 *
 * @author Martin Melka
 */
@Controller
@RequestMapping(value = "/group")
public class GroupController {

    @Autowired
    private GroupApi groupApi;

    @Autowired
    private UserApi userApi;
    @Autowired
    private StringConstants stringConstants;
    @Autowired
    private SharingApi sharingApi;
    @Autowired
    private TaskApi taskApi;


    /**
     * Lists groups in the system. Based on parameters it will list all groups, only groups the calling user is a
     * member of, or only groups in which he has a certain role.
     *
     * @param userId ID of the authenticated user making the request.
     * @param role   Optional parameter specifying groups to list.
     * @return A {@link ResponseEntity} object containing details of the response to the client.
     * @throws DoesNotExistException if the authenticated user ID does not exist. This should not happen.
     */
    @JsonView(JsonViews.Controller.GroupList.class)
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity listGroups(@AuthenticationPrincipal Long userId,
                                     @RequestParam(value = "role", required = false) String role) throws DoesNotExistException {
        User user = userApi.getUser(userId);
        if (role == null || role.isEmpty()) {
            return ResponseEntity.ok().body(groupApi.listGroups());
        } else {
            try {
                if (role.toUpperCase().equals("ANY")) {
                    return ResponseEntity.ok().body(groupApi.listGroups(user));
                } else {
                    return ResponseEntity.ok().body(groupApi.listGroups(user, paramToMemberRole(role)));
                }

            } catch (WrongParameterException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ErrorCodes.WRONG_MEMBERROLE_VALUE, e.getMessage()));
            }
        }
    }

    /**
     * Creates a group from the given parameters.
     *
     * @param userId      ID of the authenticated user making the request.
     * @param requestBody A {@link GroupRequestBody} containing details of the group to be created.
     * @return A {@link ResponseEntity} object containing details of the response to the client.
     * @throws DoesNotExistException   if the authenticated user ID does not exist. This should not happen.
     * @throws WrongParameterException if the request contained unknown parameters.
     */
    @RequestMapping(value = "", method = RequestMethod.POST, produces = StringConstants.CONTENT_TYPE_APP_JSON)
    public ResponseEntity createGroup(@AuthenticationPrincipal Long userId,
                                      @RequestBody GroupRequestBody requestBody) throws DoesNotExistException, WrongParameterException {
        User user = userApi.getUser(userId);

        try {
            Group group = groupApi.createGroup(requestBody.getName(), user, requestBody.getDescription());
            return ResponseEntity.created(URI.create("/group/" + group.getId())).body(group);
        } catch (AlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(ErrorCodes.GROUP_NAME_ALREADY_EXISTS, e.getMessage()));
        }
    }

    /**
     * Shows details of a group.
     *
     * @param userId  ID of the authenticated user making the request.
     * @param groupId ID of the group to show.
     * @return A {@link ResponseEntity} object containing details of the response to the client.
     * @throws DoesNotExistException if the authenticated user ID or a group with the given ID does not exist.
     */
    @JsonView(JsonViews.Controller.GroupDetails.class)
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = StringConstants.CONTENT_TYPE_APP_JSON)
    public ResponseEntity groupDetails(@AuthenticationPrincipal Long userId,
                                       @PathVariable("id") Long groupId) throws DoesNotExistException {
        User user = userApi.getUser(userId);
        Group group;
        try {
            group = groupApi.getGroup(groupId, user);
            return ResponseEntity.ok().body(group);
        } catch (NotMemberOfException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ErrorCodes.USER_NOT_MEMBER_OF_GROUP, e.getMessage()));
        }
    }

    /**
     * Edits information of an existing group.
     *
     * @param userId      ID of the authenticated user making the request.
     * @param groupId     ID of the group to be edited.
     * @param requestBody A {@link GroupRequestBody} object containing information to be edited.
     * @return A {@link ResponseEntity} object containing details of the response to the client.
     * @throws DoesNotExistException if the authenticated user ID or a group with the given ID does not exist.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = StringConstants.CONTENT_TYPE_APP_JSON)
    public ResponseEntity editGroup(@AuthenticationPrincipal Long userId,
                                    @PathVariable("id") Long groupId,
                                    @RequestBody GroupRequestBody requestBody) throws DoesNotExistException {
        User user = userApi.getUser(userId);
        Group group = groupApi.getGroup(groupId);

        String newDescription = requestBody.getDescription();
        if (newDescription == null) {
            return ResponseEntity.badRequest().body(new ErrorResponse(ErrorCodes.WRONG_PARAMETERS, "Field 'description' cannot be empty."));
        }

        try {
            groupApi.editDetails(user, group, newDescription);
            return ResponseEntity.ok().body(group);
        } catch (NotMemberOfException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ErrorCodes.USER_NOT_MEMBER_OF_GROUP, e.getMessage()));
        } catch (GroupPermissionException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ErrorCodes.USER_NOT_ENOUGH_GROUP_PERMISSION, e.getMessage()));
        }
    }

    /**
     * Delete an existing group.
     *
     * @param userId  ID of the authenticated user making the request.
     * @param groupId ID of the group to be deleted.
     * @return A {@link ResponseEntity} object containing details of the response to the client.
     * @throws DoesNotExistException   if the authenticated user ID or a group with the given ID does not exist.
     * @throws WrongParameterException if the request contained unknown parameters.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity deleteGroup(@AuthenticationPrincipal Long userId,
                                      @PathVariable("id") Long groupId) throws DoesNotExistException, WrongParameterException {
        User user = userApi.getUser(userId);
        Group group = groupApi.getGroup(groupId);

        try {
            groupApi.deleteGroup(user, group);
            return ResponseEntity.ok().build();
        } catch (NotMemberOfException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ErrorCodes.USER_NOT_MEMBER_OF_GROUP, e.getMessage()));
        } catch (GroupPermissionException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ErrorCodes.USER_NOT_ENOUGH_GROUP_PERMISSION, e.getMessage()));
        }
    }

    /**
     * Offer a membership to a user.
     *
     * @param userId      ID of the authenticated user making the request.
     * @param groupId     ID of the group for which to offer the membership.
     * @param requestBody A {@link MembershipOfferRequestBody} object containing details about the offer to make.
     * @return A {@link ResponseEntity} object containing details of the response to the client.
     * @throws DoesNotExistException if the authenticated user ID or a group with the given ID does not exist.
     */
    @RequestMapping(value = "/{id}/member/offer", method = RequestMethod.POST)
    public ResponseEntity offerMembership(@AuthenticationPrincipal Long userId,
                                          @PathVariable("id") Long groupId,
                                          @RequestBody MembershipOfferRequestBody requestBody) throws DoesNotExistException {
        User user = userApi.getUser(userId);
        Group group = groupApi.getGroup(groupId);

        if (requestBody.getUserIds() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ErrorCodes.WRONG_PARAMETERS, "Expected array field 'userIds'"));
        }

        try {
            for (Long offerToId : requestBody.getUserIds()) {
                User offerTo = userApi.getUser(offerToId);
                sharingApi.offerMembership(user, group, offerTo);
            }
            return ResponseEntity.ok().build();
        } catch (NotMemberOfException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ErrorCodes.USER_NOT_MEMBER_OF_GROUP, e.getMessage()));
        } catch (GroupPermissionException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ErrorCodes.USER_NOT_ENOUGH_GROUP_PERMISSION, e.getMessage()));
        } catch (AlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ErrorCodes.USER_ALREADY_MEMBER, e.getMessage()));
        }
    }

    /**
     * Edit role of a group member.
     * <p>
     * Used to promote and demote Managers in a group.
     *
     * @param userId       ID of the authenticated user making the request.
     * @param groupId      ID of the group in which to edit a member's role.
     * @param targetUserId ID of the user whose role is to be edited.
     * @param requestBody  A {@link MemberRequestBody} object containing details of the edit to be made.
     * @return A {@link ResponseEntity} object containing details of the response to the client.
     * @throws DoesNotExistException   if the authenticated user ID or a group with the given ID does not exist.
     * @throws WrongParameterException if the request contained unknown parameters.
     */
    @RequestMapping(value = "/{id}/member/{memberid}", method = RequestMethod.PUT)
    public ResponseEntity editMemberRole(@AuthenticationPrincipal Long userId,
                                         @PathVariable("id") Long groupId,
                                         @PathVariable("memberid") Long targetUserId,
                                         @RequestBody MemberRequestBody requestBody) throws DoesNotExistException, WrongParameterException { // V request těle bude prostě newrole:member/manager/admin
        User user = userApi.getUser(userId);
        Group group = groupApi.getGroup(groupId);

        if (requestBody.getRole() == null) {
            return ResponseEntity.badRequest().body(new ErrorResponse(ErrorCodes.WRONG_PARAMETERS, "Field 'role' expected with values MEMBER,MANAGER,ADMIN"));
        }

        User targetUser = userApi.getUser(targetUserId);

        MemberRole newRole = requestBody.getRole();
        if (newRole == MemberRole.ADMIN) {
            try {
                groupApi.changeAdmin(user, group, targetUser);
            } catch (NotMemberOfException e) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ErrorCodes.USER_NOT_MEMBER_OF_GROUP, e.getMessage()));
            } catch (GroupPermissionException e) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ErrorCodes.USER_NOT_ENOUGH_GROUP_PERMISSION, e.getMessage()));
            }
        } else {
            boolean newValue = (newRole == MemberRole.MANAGER);
            try {
                groupApi.setManager(user, group, targetUser, newValue);
            } catch (GroupPermissionException e) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ErrorCodes.USER_NOT_ENOUGH_GROUP_PERMISSION, e.getMessage()));
            } catch (NotMemberOfException e) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ErrorCodes.USER_NOT_MEMBER_OF_GROUP, e.getMessage()));
            } catch (NotAllowedException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ErrorCodes.CANNOT_DEMOTE_ADMIN, stringConstants.EXC_NOT_ALLOWED_DEMOTE_ADMIN));
            }
        }

        return ResponseEntity.ok().build();
    }

    /**
     * @param userId       ID of the authenticated user making the request.
     * @param groupId      ID of the group from which to remove a member.
     * @param targetUserId ID of the user whom to remove from the group.
     * @return A {@link ResponseEntity} object containing details of the response to the client.
     * @throws DoesNotExistException   if the authenticated user ID or a group with the given ID does not exist.
     * @throws WrongParameterException if the request contained unknown parameters.
     */
    @RequestMapping(value = "/{id}/member/{memberid}", method = RequestMethod.DELETE)
    public ResponseEntity removeMember(@AuthenticationPrincipal Long userId,
                                       @PathVariable("id") Long groupId,
                                       @PathVariable("memberid") Long targetUserId) throws DoesNotExistException, WrongParameterException {
        User user = userApi.getUser(userId);
        Group group = groupApi.getGroup(groupId);
        User targetUser = userApi.getUser(targetUserId);

        try {
            groupApi.removeMember(user, group, targetUser);
            return ResponseEntity.ok().build();
        } catch (NotAllowedException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ErrorCodes.CANNOT_DEMOTE_ADMIN, stringConstants.EXC_NOT_ALLOWED_ADMIN_LEAVE_OR_REMOVE));
        } catch (NotMemberOfException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ErrorCodes.USER_NOT_MEMBER_OF_GROUP, e.getMessage()));
        } catch (GroupPermissionException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ErrorCodes.USER_NOT_ENOUGH_GROUP_PERMISSION, e.getMessage()));
        }
    }

    /**
     * @param userId       ID of the authenticated user making the request.
     * @param groupId      ID of the group from which to remove a task.
     * @param targetTaskId ID of the task to remove from the group.
     * @return A {@link ResponseEntity} object containing details of the response to the client.
     * @throws DoesNotExistException   if the authenticated user ID or a group with the given ID does not exist.
     * @throws WrongParameterException if the request contained unknown parameters.
     */
    @RequestMapping(value = "/{id}/task/{taskid}", method = RequestMethod.DELETE)
    public ResponseEntity removeTask(@AuthenticationPrincipal Long userId,
                                     @PathVariable("id") Long groupId,
                                     @PathVariable("taskid") Long targetTaskId) throws DoesNotExistException, WrongParameterException {
        User user = userApi.getUser(userId);
        Group group = groupApi.getGroup(groupId);
        Task task = taskApi.getTask(targetTaskId);

        try {
            groupApi.leaveTask(user, group, task);
            return ResponseEntity.ok().build();
        } catch (NotMemberOfException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ErrorCodes.USER_NOT_MEMBER_OF_GROUP, e.getMessage()));
        } catch (GroupPermissionException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ErrorCodes.USER_NOT_ENOUGH_GROUP_PERMISSION, e.getMessage()));
        } catch (NotAllowedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ErrorCodes.TASK_NOT_OF_GROUP, e.getMessage()));
        }
    }

    private MemberRole paramToMemberRole(String param) throws WrongParameterException {
        try {
            return MemberRole.valueOf(param.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new WrongParameterException(MessageFormat.format(stringConstants.EXC_PARAM_MEMBER_ROLE_INVALID, param));
        }
    }
}
