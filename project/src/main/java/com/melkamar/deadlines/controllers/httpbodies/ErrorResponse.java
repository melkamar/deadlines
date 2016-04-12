package com.melkamar.deadlines.controllers.httpbodies;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.text.MessageFormat;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 08.04.2016 13:54
 */
@Entity
public class ErrorResponse {
    @JsonIgnore
    @Id
    private final int id;
    public final int errorCode;
    public final String errorMessage;

    public ErrorResponse(int errorCode, String errorMessage) {
        this.id = 0;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public ErrorResponse() {
        this.id = 0;
        errorCode = -1;
        errorMessage = null;
    }

    @Override
    public String toString() {
        return MessageFormat.format("'{'\"errorCode\":{0}, \"errorMessage\":\"{1}\"'}'", errorCode+"", errorMessage);
    }
}
