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

import com.melkamar.deadlines.config.ErrorCodes;
import com.melkamar.deadlines.controllers.httpbodies.ErrorResponse;
import com.melkamar.deadlines.controllers.httpbodies.UserCreateRequestBody;
import com.melkamar.deadlines.exceptions.AlreadyExistsException;
import com.melkamar.deadlines.exceptions.DoesNotExistException;
import com.melkamar.deadlines.exceptions.WrongParameterException;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.services.api.UserApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * This Controller class handles incoming requests made to an address "/user/**".
 * <p>
 * Actions performed by the controller deal with user listing and creating.
 *
 * @author Martin Melka
 */
@RestController
@RequestMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    @Autowired
    private UserApi userApi;

    /**
     * Lists all existing users.
     *
     * @return A {@link ResponseEntity} object containing details of the response to the client.
     */
    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity listUsers() {
        List<User> users = userApi.listUsers();
        return ResponseEntity.ok().body(users);
    }

    /**
     * Creates a new user.
     *
     * @param userCreateRequestBody A {@link UserCreateRequestBody} object containing details of the user to be created.
     * @return A {@link ResponseEntity} object containing details of the response to the client.
     */
    @RequestMapping(value = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createUser(@RequestBody UserCreateRequestBody userCreateRequestBody) {
        try {
            User user = userApi.createUser(userCreateRequestBody.getUsername(),
                    userCreateRequestBody.getPassword(),
                    userCreateRequestBody.getName(),
                    userCreateRequestBody.getEmail());
            return ResponseEntity.created(URI.create("/user/" + user.getId())).body(user);
        } catch (WrongParameterException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ErrorResponse(ErrorCodes.WRONG_PARAMETERS, e.getMessage()));
        } catch (AlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    new ErrorResponse(ErrorCodes.USER_ALREADY_EXISTS, e.getMessage()));
        }
    }

    /**
     * Shows details of an existing user.
     * @param id ID of the user whose details to show.
     * @return A {@link ResponseEntity} object containing details of the response to the client.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity userDetails(@PathVariable("id") long id) {
        try {
            User user = userApi.getUser(id);

            return ResponseEntity.ok().body(user);
        } catch (DoesNotExistException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Edits information of an existing user.
     *
     * @param userId ID of the authenticated user making the request.
     * @param id ID of the user whose details to edit.
     * @param request A {@link UserCreateRequestBody} object containing details of the edit.
     * @return A {@link ResponseEntity} object containing details of the response to the client.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity editUser(@AuthenticationPrincipal Long userId,
                                   @PathVariable("id") Long id,
                                   @RequestBody UserCreateRequestBody request) {
        User user;
        try {
            user = userApi.getUser(userId);
        } catch (DoesNotExistException e) {
            return ResponseEntity.notFound().build();
        }

        if (!user.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        userApi.editUserDetails(user, request.getName(), request.getEmail(), request.getPassword());

        return ResponseEntity.ok().body(user);
    }
}
