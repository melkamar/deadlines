package com.melkamar.deadlines.exceptions;

import org.springframework.security.core.AuthenticationException;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 09.04.2016 12:19
 */
public class RESTAuthenticationException extends AuthenticationException {
    public RESTAuthenticationException(String message) {
        super(message);
    }
}
