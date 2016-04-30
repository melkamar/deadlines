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
import com.melkamar.deadlines.controllers.httpbodies.OfferResolutionRequestBody;
import com.melkamar.deadlines.exceptions.*;
import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.offer.GroupTaskSharingOffer;
import com.melkamar.deadlines.model.offer.MembershipOffer;
import com.melkamar.deadlines.model.offer.UserTaskSharingOffer;
import com.melkamar.deadlines.model.task.Task;
import com.melkamar.deadlines.services.api.GroupApi;
import com.melkamar.deadlines.services.api.SharingApi;
import com.melkamar.deadlines.services.api.UserApi;
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
    private SharingApi sharingApi;
    @Autowired
    private UserApi userApi;
    @Autowired
    private StringConstants stringConstants;
    @Autowired
    private GroupApi groupApi;


    @RequestMapping(value = "/task/user", method = RequestMethod.GET)
    @JsonView(JsonViews.Controller.OfferList.class)
    public ResponseEntity listUserTaskOffers(@AuthenticationPrincipal Long userId) throws DoesNotExistException {
        User user = userApi.getUser(userId);
        Set<UserTaskSharingOffer> offers = sharingApi.listTaskOffersOfUser(user);

        return ResponseEntity.ok(offers);
    }

    @RequestMapping(value = "/task/user/resolve/{id}", method = RequestMethod.POST)
    public ResponseEntity resolveUserTaskOffer(@AuthenticationPrincipal Long userId,
                                               @PathVariable("id") Long id,
                                               @RequestBody OfferResolutionRequestBody requestBody) throws DoesNotExistException, WrongParameterException {
        User user = userApi.getUser(userId);
        UserTaskSharingOffer offer = sharingApi.getUserTaskSharingOffer(id);

        try {
            Boolean accept = requestBody.isAccept();
            if (accept == null) {
                throw new WrongParameterException(MessageFormat.format(stringConstants.EXC_BODY_MUST_HAVE_FIELD, "accept:true|false"));
            }

            Task task = sharingApi.resolveTaskSharingOffer(user, offer, accept);
            return ResponseEntity.ok().build();
        } catch (NotMemberOfException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ErrorCodes.OFFER_USER_NOT_OWNER, e.getMessage()));
        }
    }

    @JsonView(JsonViews.Controller.OfferList.class)
    @RequestMapping(value = "/membership", method = RequestMethod.GET)
    public ResponseEntity listMembershipOffers(@AuthenticationPrincipal Long userId) throws DoesNotExistException {
        User user = userApi.getUser(userId);
        Set<MembershipOffer> offers = sharingApi.listMembershipOffersOfUser(user);

        return ResponseEntity.ok(offers);
    }

    @RequestMapping(value = "/membership/resolve/{id}", method = RequestMethod.POST)
    public ResponseEntity resolveMembershipOffer(@AuthenticationPrincipal Long userId,
                                                 @PathVariable("id") Long id,
                                                 @RequestBody OfferResolutionRequestBody requestBody) throws DoesNotExistException, WrongParameterException {
        User user = userApi.getUser(userId);
        MembershipOffer offer = sharingApi.getMembershipOffer(id);

        try {
            Boolean accept = requestBody.isAccept();
            if (accept == null) {
                throw new WrongParameterException(MessageFormat.format(stringConstants.EXC_BODY_MUST_HAVE_FIELD, "accept:true|false"));
            }

            Group group = sharingApi.resolveMembershipOffer(user, offer, accept);
            return ResponseEntity.ok().build();
        } catch (NotMemberOfException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ErrorCodes.OFFER_USER_NOT_OWNER, e.getMessage()));
        } catch (GroupPermissionException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ErrorCodes.OFFER_OFFERER_NOT_PERMISSION, e.getMessage()));
        } catch (AlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ErrorCodes.USER_ALREADY_MEMBER, e.getMessage()));
        }

    }

    @JsonView(JsonViews.Controller.OfferList.class)
    @RequestMapping(value = "/task/group/{id}", method = RequestMethod.GET)
    public ResponseEntity listGroupTaskOffers(@AuthenticationPrincipal Long userId,
                                              @PathVariable("id") Long id) throws DoesNotExistException {
        User user = userApi.getUser(userId);
        Group group = groupApi.getGroup(id);

        try {
            Set<GroupTaskSharingOffer> offers = sharingApi.listTaskOffersOfGroup(user, group);
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
        User user = userApi.getUser(userId);
        Group group = groupApi.getGroup(groupId);

        GroupTaskSharingOffer offer = sharingApi.getGroupTaskSharingOffer(offerId);

        try {
            checkResolutionRequestBody(requestBody.isAccept());
            Task task = sharingApi.resolveTaskSharingOffer(group, user, offer, requestBody.isAccept());

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
