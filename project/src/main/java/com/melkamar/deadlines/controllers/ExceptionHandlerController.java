package com.melkamar.deadlines.controllers;

import com.melkamar.deadlines.model.misc.ErrorResponse;
import org.hibernate.hql.internal.ast.ErrorReporter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 08.04.2016 14:32
 */
@ControllerAdvice
public class ExceptionHandlerController {

//    @ExceptionHandler(value = HttpMessageNotReadableException.class)
//    public ErrorResponse emptyBodyHandler() {
//        return new ErrorResponse(159, "sample");
//    }
}
