package com.melkamar.deadlines.controllers;

import com.melkamar.deadlines.config.ErrorCodes;
import com.melkamar.deadlines.config.StringConstants;
import com.melkamar.deadlines.controllers.stubs.OfferResolutionRequestBody;
import com.melkamar.deadlines.exceptions.*;
import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.misc.ErrorResponse;
import com.melkamar.deadlines.model.offer.GroupTaskSharingOffer;
import com.melkamar.deadlines.model.offer.MembershipOffer;
import com.melkamar.deadlines.model.offer.UserTaskSharingOffer;
import com.melkamar.deadlines.model.task.Task;
import com.melkamar.deadlines.services.api.GroupAPI;
import com.melkamar.deadlines.services.api.SharingAPI;
import com.melkamar.deadlines.services.api.UserAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.text.MessageFormat;
import java.util.Set;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 09.04.2016 22:06
 */
@Controller
@RequestMapping(value = "/offer")
public class OfferController {

    @Autowired
    private SharingAPI sharingAPI;
    @Autowired
    private UserAPI userAPI;
    @Autowired
    private StringConstants stringConstants;
    @Autowired
    private GroupAPI groupAPI;


    @RequestMapping(value = "/task/user", method = RequestMethod.GET)
    public ResponseEntity listUserTaskOffers(@AuthenticationPrincipal Long userId) throws DoesNotExistException {
        User user = userAPI.getUser(userId);
        Set<UserTaskSharingOffer> offers = sharingAPI.listTaskOffersOfUser(user);

        return ResponseEntity.ok(offers);
    }

    @RequestMapping(value = "/task/user/resolve/{id}", method = RequestMethod.POST)
    public ResponseEntity resolveUserTaskOffer(@AuthenticationPrincipal Long userId,
                                               @PathVariable("id") Long id,
                                               @RequestBody OfferResolutionRequestBody requestBody) throws DoesNotExistException, WrongParameterException {
        User user = userAPI.getUser(userId);
        UserTaskSharingOffer offer = sharingAPI.getUserTaskSharingOffer(id);

        try {
            Boolean accept = requestBody.isAccept();
            if (accept == null) {
                throw new WrongParameterException(MessageFormat.format(stringConstants.EXC_BODY_MUST_HAVE_FIELD, "accept:true|false"));
            }

            Task task = sharingAPI.resolveTaskSharingOffer(user, offer, accept);
            return ResponseEntity.ok().build();
        } catch (NotMemberOfException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ErrorCodes.OFFER_USER_NOT_OWNER, e.getMessage()));
        }
    }

    @RequestMapping(value = "/membership", method = RequestMethod.GET)
    public ResponseEntity listMembershipOffers(@AuthenticationPrincipal Long userId) throws DoesNotExistException {
        User user = userAPI.getUser(userId);
        Set<MembershipOffer> offers = sharingAPI.listMembershipOffersOfUser(user);

        return ResponseEntity.ok(offers);
    }

    @RequestMapping(value = "/membership/resolve/{id}", method = RequestMethod.POST)
    public ResponseEntity resolveMembershipOffer(@AuthenticationPrincipal Long userId,
                                                 @PathVariable("id") Long id,
                                                 @RequestBody OfferResolutionRequestBody requestBody) throws DoesNotExistException, WrongParameterException {
        User user = userAPI.getUser(userId);
        MembershipOffer offer = sharingAPI.getMembershipOffer(id);

        try {
            Boolean accept = requestBody.isAccept();
            if (accept == null) {
                throw new WrongParameterException(MessageFormat.format(stringConstants.EXC_BODY_MUST_HAVE_FIELD, "accept:true|false"));
            }

            Group group = sharingAPI.resolveMembershipOffer(user, offer, accept);
            return ResponseEntity.ok().build();
        } catch (NotMemberOfException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ErrorCodes.OFFER_USER_NOT_OWNER, e.getMessage()));
        } catch (GroupPermissionException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ErrorCodes.OFFER_OFFERER_NOT_PERMISSION, e.getMessage()));
        } catch (AlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ErrorCodes.USER_ALREADY_MEMBER, e.getMessage()));
        }

    }

    @RequestMapping(value = "/task/group/{id}", method = RequestMethod.GET)
    public ResponseEntity listGroupTaskOffers(@AuthenticationPrincipal Long userId,
                                              @PathVariable("id") Long id) throws DoesNotExistException {
        User user = userAPI.getUser(userId);
        Group group = groupAPI.getGroup(id);

        try {
            Set<GroupTaskSharingOffer> offers = sharingAPI.listTaskOffersOfGroup(user, group);
            return ResponseEntity.ok().body(offers);
        } catch (NotMemberOfException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ErrorCodes.USER_NOT_MEMBER_OF_GROUP, e.getMessage()));
        } catch (GroupPermissionException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ErrorCodes.USER_NOT_ENOUGH_GROUP_PERMISSION, e.getMessage()));
        }
    }

    @RequestMapping(value = "/task/group/{groupid}/resolve/{offerid}", method = RequestMethod.POST)
    public ResponseEntity resolveGroupTaskOffer(@AuthenticationPrincipal Long userId,
                                                @PathVariable("groupid") Long groupId,
                                                @PathVariable("offerid") Long offerId,
                                                @RequestBody OfferResolutionRequestBody requestBody) throws DoesNotExistException, WrongParameterException {
        User user = userAPI.getUser(userId);
        Group group = groupAPI.getGroup(groupId);

        GroupTaskSharingOffer offer = sharingAPI.getGroupTaskSharingOffer(offerId);

        try {
            checkResolutionRequestBody(requestBody.isAccept());
            Task task = sharingAPI.resolveTaskSharingOffer(group, user, offer, requestBody.isAccept());

            return ResponseEntity.ok().build();
        } catch (NotMemberOfException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ErrorCodes.USER_NOT_MEMBER_OF_GROUP, e.getMessage()));
        } catch (AlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ErrorCodes.TASK_ALREADY_SHARED_WITH_GROUP, e.getMessage()));
        } catch (GroupPermissionException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ErrorCodes.USER_NOT_ENOUGH_GROUP_PERMISSION, e.getMessage()));
        }
    }

    private void checkResolutionRequestBody(Boolean accept) throws WrongParameterException {
        if (accept == null) {
            throw new WrongParameterException(MessageFormat.format(stringConstants.EXC_BODY_MUST_HAVE_FIELD, "accept:true|false"));
        }
    }
}
