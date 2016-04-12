package com.melkamar.deadlines.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.melkamar.deadlines.config.ErrorCodes;
import com.melkamar.deadlines.controllers.requestbodies.UserCreateRequestBody;
import com.melkamar.deadlines.exceptions.AlreadyExistsException;
import com.melkamar.deadlines.exceptions.DoesNotExistException;
import com.melkamar.deadlines.exceptions.WrongParameterException;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.misc.ErrorResponse;
import com.melkamar.deadlines.services.api.UserAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 07.04.2016 21:09
 */
@RestController
public class UserController {
    final static String CTYPE_JSON = "application/json";

    @Autowired
    private UserAPI userAPI;

    private final ObjectMapper objectMapper = new ObjectMapper();


    @RequestMapping(value = "/user", method = RequestMethod.GET, produces = CTYPE_JSON)
    public ResponseEntity listUsers() {
        List<User> users = userAPI.listUsers();
        return ResponseEntity.ok().body(users);
    }

    @RequestMapping(value = "/user", method = RequestMethod.POST, consumes = CTYPE_JSON, produces = CTYPE_JSON)
    public ResponseEntity createUser(@RequestBody UserCreateRequestBody userCreateRequestBody) {
        try {
            User user = userAPI.createUser(userCreateRequestBody.getUsername(),
                    userCreateRequestBody.getPassword(),
                    userCreateRequestBody.getName(),
                    userCreateRequestBody.getEmail());
            return ResponseEntity.created(URI.create("/user/"+user.getId())).body(user);
        } catch (WrongParameterException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ErrorResponse(ErrorCodes.WRONG_PARAMETERS, e.getMessage()));
        } catch (AlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    new ErrorResponse(ErrorCodes.USER_ALREADY_EXISTS, e.getMessage()));
        }
    }

    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET, produces = CTYPE_JSON)
    public ResponseEntity userDetails(@PathVariable("id") long id) {
        try {
            User user = userAPI.getUser(id);

            return ResponseEntity.ok().body(user);
        } catch (DoesNotExistException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @RequestMapping(value = "/user/{id}", method = RequestMethod.PUT, produces = CTYPE_JSON, consumes = CTYPE_JSON)
    public ResponseEntity editUser(@AuthenticationPrincipal Long userId, @PathVariable("id") Long id, @RequestBody UserCreateRequestBody request) {
        User user = null;
        try {
            user = userAPI.getUser(userId);
        } catch (DoesNotExistException e) {
            return ResponseEntity.notFound().build();
        }

        if (!user.getId().equals(id)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        userAPI.editUserDetails(user, request.getName(), request.getEmail(), request.getPassword());

        return ResponseEntity.ok().body(user);
    }
}
