package com.melkamar.deadlines.exceptions;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 08.04.2016 12:26
 */
public class UserAlreadyExistsException extends Exception {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
