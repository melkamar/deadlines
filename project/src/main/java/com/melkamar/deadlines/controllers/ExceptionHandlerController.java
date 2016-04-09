package com.melkamar.deadlines.controllers;

import com.melkamar.deadlines.config.ErrorCodes;
import com.melkamar.deadlines.exceptions.DoesNotExistException;
import com.melkamar.deadlines.exceptions.WrongParameterException;
import com.melkamar.deadlines.model.misc.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 08.04.2016 14:32
 */
@ControllerAdvice
@EnableWebMvc
public class ExceptionHandlerController {

    @ExceptionHandler({DoesNotExistException.class})
    public ResponseEntity doesNotExist(DoesNotExistException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(WrongParameterException.class)
    public ResponseEntity wrongParameters(WrongParameterException e){
        return ResponseEntity.badRequest().body(new ErrorResponse(ErrorCodes.WRONG_PARAMETERS, e.getMessage()));
    }

}
