package com.melkamar.deadlines.controllers;

import com.melkamar.deadlines.config.ErrorCodes;
import com.melkamar.deadlines.controllers.stubs.UserStub;
import com.melkamar.deadlines.exceptions.DoesNotExistException;
import com.melkamar.deadlines.exceptions.UserAlreadyExistsException;
import com.melkamar.deadlines.exceptions.WrongParameterException;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.misc.ErrorResponse;
import com.melkamar.deadlines.services.api.TaskAPI;
import com.melkamar.deadlines.services.api.UserAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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


    @RequestMapping(value = "/user", method = RequestMethod.GET, produces = CTYPE_JSON)
    public List<User> listUsers() {
        return userAPI.listUsers();
    }

    @RequestMapping(value = "/user", method = RequestMethod.POST, consumes = CTYPE_JSON)
    public ResponseEntity createUser(@RequestBody UserStub userStub) {
        try {
            User user = userAPI.createUser(userStub.getUsername(), userStub.getPassword(), userStub.getName(), userStub.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        } catch (WrongParameterException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ErrorResponse(ErrorCodes.WRONG_PARAMETERS, e.getMessage()));
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    new ErrorResponse(ErrorCodes.USER_ALREADY_EXISTS, e.getMessage()));
        }
    }

    @RequestMapping(value = "/user/{id}")
    public ResponseEntity userDetails(@PathVariable("id") long id){
        try {
            User user = userAPI.getUser(id);
            return ResponseEntity.ok().body(user);
        } catch (DoesNotExistException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
