package com.melkamar.deadlines.exceptions;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 08.04.2016 15:02
 */
public class DoesNotExistException extends Exception{
    public DoesNotExistException(String message) {
        super(message);
    }
}
