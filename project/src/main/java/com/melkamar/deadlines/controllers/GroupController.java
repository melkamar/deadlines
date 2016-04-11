package com.melkamar.deadlines.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.melkamar.deadlines.config.ErrorCodes;
import com.melkamar.deadlines.config.StringConstants;
import com.melkamar.deadlines.controllers.requestobjects.GroupRequestBody;
import com.melkamar.deadlines.controllers.requestobjects.MemberRequestBody;
import com.melkamar.deadlines.controllers.requestobjects.MembershipOfferRequestBody;
import com.melkamar.deadlines.controllers.views.JsonViews;
import com.melkamar.deadlines.exceptions.*;
import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.MemberRole;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.misc.ErrorResponse;
import com.melkamar.deadlines.model.task.Task;
import com.melkamar.deadlines.services.api.GroupAPI;
import com.melkamar.deadlines.services.api.SharingAPI;
import com.melkamar.deadlines.services.api.TaskAPI;
import com.melkamar.deadlines.services.api.UserAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 10.04.2016 12:31
 */
@Controller
@RequestMapping(value = "/group")
public class GroupController {

    @Autowired
    private GroupAPI groupAPI;

    @Autowired
    private UserAPI userAPI;
    @Autowired
    private StringConstants stringConstants;
    @Autowired
    private SharingAPI sharingAPI;
    @Autowired
    private TaskAPI taskAPI;


    /**
     * Lists groups in the system. Based on parameters it will list all groups, only groups the calling user is a
     * member of, or only groups in which he has a certain role.
     *
     * @param userId
     * @param role
     * @return
     * @throws DoesNotExistException
     */
    @JsonView(JsonViews.Controller.GroupList.class)
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity listGroups(@AuthenticationPrincipal Long userId,
                                     @RequestParam(value = "role", required = false) String role) throws DoesNotExistException {
        User user = userAPI.getUser(userId);
        if (role == null || role.isEmpty()) {
            return ResponseEntity.ok().body(groupAPI.listGroups());
        } else {
            try {
                if (role.toUpperCase().equals("ANY")) {
                    return ResponseEntity.ok().body(groupAPI.listGroups(user));
                } else {
                    return ResponseEntity.ok().body(groupAPI.listGroups(user, paramToMemberRole(role)));
                }

            } catch (WrongParameterException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ErrorCodes.WRONG_MEMBERROLE_VALUE, e.getMessage()));
            }
        }
    }

    @RequestMapping(value = "", method = RequestMethod.POST, produces = StringConstants.CONTENT_TYPE_APP_JSON)
    public ResponseEntity createGroup(@AuthenticationPrincipal Long userId,
                                      @RequestBody GroupRequestBody requestBody) throws DoesNotExistException, WrongParameterException {
        User user = userAPI.getUser(userId);

        try {
            Group group = groupAPI.createGroup(requestBody.getName(), user, requestBody.getDescription());
            return ResponseEntity.status(HttpStatus.CREATED).body(group);
        } catch (AlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(ErrorCodes.GROUP_NAME_ALREADY_EXISTS, e.getMessage()));
        }
    }

    @JsonView(JsonViews.Controller.GroupDetails.class)
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = StringConstants.CONTENT_TYPE_APP_JSON)
    public ResponseEntity groupDetails(@AuthenticationPrincipal Long userId,
                                       @PathVariable("id") Long groupId) throws DoesNotExistException {
        User user = userAPI.getUser(userId);
        Group group = null;
        try {
            group = groupAPI.getGroup(groupId, user);
            return ResponseEntity.ok().body(group);
        } catch (NotMemberOfException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ErrorCodes.USER_NOT_MEMBER_OF_GROUP, e.getMessage()));
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = StringConstants.CONTENT_TYPE_APP_JSON)
    public ResponseEntity editGroup(@AuthenticationPrincipal Long userId,
                                    @PathVariable("id") Long groupId,
                                    @RequestBody GroupRequestBody requestBody) throws DoesNotExistException {
        User user = userAPI.getUser(userId);
        Group group = groupAPI.getGroup(groupId);

        String newDescription = requestBody.getDescription();
        if (newDescription == null) {
            return ResponseEntity.badRequest().body(new ErrorResponse(ErrorCodes.WRONG_PARAMETERS, "Field 'description' cannot be empty."));
        }

        try {
            groupAPI.editDetails(user, group, newDescription);
            return ResponseEntity.ok().body(group);
        } catch (NotMemberOfException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ErrorCodes.USER_NOT_MEMBER_OF_GROUP, e.getMessage()));
        } catch (GroupPermissionException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ErrorCodes.USER_NOT_ENOUGH_GROUP_PERMISSION, e.getMessage()));
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity deleteGroup(@AuthenticationPrincipal Long userId,
                                      @PathVariable("id") Long groupId) throws DoesNotExistException, WrongParameterException {
        User user = userAPI.getUser(userId);
        Group group = groupAPI.getGroup(groupId);

        try {
            groupAPI.deleteGroup(user, group);
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
     * @param userId
     * @return
     */
    @RequestMapping(value = "/{id}/member/offer", method = RequestMethod.POST)
    public ResponseEntity offerMembership(@AuthenticationPrincipal Long userId,
                                          @PathVariable("id") Long groupId,
                                          @RequestBody MembershipOfferRequestBody requestBody) throws DoesNotExistException {
        User user = userAPI.getUser(userId);
        Group group = groupAPI.getGroup(groupId);

        if (requestBody.getUserIds() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ErrorCodes.WRONG_PARAMETERS, "Expected array field 'userIds'"));
        }

        try {
            for (Long offerToId : requestBody.getUserIds()) {
                User offerTo = userAPI.getUser(offerToId);
                sharingAPI.offerMembership(user, group, offerTo);
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

    @RequestMapping(value = "/{id}/member/{memberid}", method = RequestMethod.PUT)
    public ResponseEntity editMemberRole(@AuthenticationPrincipal Long userId,
                                         @PathVariable("id") Long groupId,
                                         @PathVariable("memberid") Long targetUserId,
                                         @RequestBody MemberRequestBody requestBody) throws DoesNotExistException, WrongParameterException { // V request těle bude prostě newrole:member/manager/admin
        User user = userAPI.getUser(userId);
        Group group = groupAPI.getGroup(groupId);

        if (requestBody.getRole() == null) {
            return ResponseEntity.badRequest().body(new ErrorResponse(ErrorCodes.WRONG_PARAMETERS, "Field 'role' expected with values MEMBER,MANAGER,ADMIN"));
        }

        User targetUser = userAPI.getUser(targetUserId);

        MemberRole newRole = requestBody.getRole();
        if (newRole == MemberRole.ADMIN) {
            try {
                groupAPI.changeAdmin(user, group, targetUser);
            } catch (NotMemberOfException e) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ErrorCodes.USER_NOT_MEMBER_OF_GROUP, e.getMessage()));
            } catch (GroupPermissionException e) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ErrorCodes.USER_NOT_ENOUGH_GROUP_PERMISSION, e.getMessage()));
            }
        } else {
            boolean newValue = (newRole == MemberRole.MANAGER);
            try {
                groupAPI.setManager(user, group, targetUser, newValue);
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

    @RequestMapping(value = "/{id}/member/{memberid}", method = RequestMethod.DELETE)
    public ResponseEntity removeMember(@AuthenticationPrincipal Long userId,
                                       @PathVariable("id") Long groupId,
                                       @PathVariable("memberid") Long targetUserId) throws DoesNotExistException, WrongParameterException {
        User user = userAPI.getUser(userId);
        Group group = groupAPI.getGroup(groupId);
        User targetUser = userAPI.getUser(targetUserId);

        try {
            groupAPI.removeMember(user, group, targetUser);
            return ResponseEntity.ok().build();
        } catch (NotAllowedException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ErrorCodes.CANNOT_DEMOTE_ADMIN, stringConstants.EXC_NOT_ALLOWED_ADMIN_LEAVE_OR_REMOVE));
        } catch (NotMemberOfException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ErrorCodes.USER_NOT_MEMBER_OF_GROUP, e.getMessage()));
        } catch (GroupPermissionException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ErrorCodes.USER_NOT_ENOUGH_GROUP_PERMISSION, e.getMessage()));
        }
    }

    @RequestMapping(value = "/{id}/task/{taskid}", method = RequestMethod.DELETE)
    public ResponseEntity removeTask(@AuthenticationPrincipal Long userId,
                                     @PathVariable("id") Long groupId,
                                     @PathVariable("taskid") Long targetTaskId) throws DoesNotExistException, WrongParameterException {
        User user = userAPI.getUser(userId);
        Group group = groupAPI.getGroup(groupId);
        Task task = taskAPI.getTask(targetTaskId);

        try {
            groupAPI.leaveTask(user, group, task);
            return ResponseEntity.ok().build();
        } catch (NotMemberOfException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ErrorCodes.USER_NOT_MEMBER_OF_GROUP, e.getMessage()));
        } catch (GroupPermissionException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ErrorCodes.USER_NOT_ENOUGH_GROUP_PERMISSION, e.getMessage()));
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
