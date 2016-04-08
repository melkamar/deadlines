package com.melkamar.deadlines.controllers;

import com.melkamar.deadlines.config.ErrorCodes;
import com.melkamar.deadlines.controllers.stubs.UserStub;
import com.melkamar.deadlines.exceptions.UserAlreadyExistsException;
import com.melkamar.deadlines.exceptions.WrongParameterException;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.misc.ErrorResponse;
import com.melkamar.deadlines.services.api.TaskAPI;
import com.melkamar.deadlines.services.api.UserAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 07.04.2016 21:09
 */
@RestController
public class UserController {


    @Autowired
    private UserAPI userAPI;
    @Autowired
    private TaskAPI taskAPI;
    @Autowired
    private ErrorCodes errorCodes;


    @RequestMapping(value = "/user", method = RequestMethod.GET, produces = "application/json")
    public List<User> listUsers() {
        return userAPI.listUsers();
    }

    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public ResponseEntity createUser(@RequestBody UserStub userStub, BindingResult bindingResult) {

        try {
            User user = userAPI.createUser(userStub.getUsername(), userStub.getPassword(), userStub.getName(), userStub.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        } catch (WrongParameterException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ErrorResponse(errorCodes.WRONG_PARAMETERS, e.getMessage()));
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    new ErrorResponse(errorCodes.USER_ALREADY_EXISTS, e.getMessage()));
        }
    }
}
