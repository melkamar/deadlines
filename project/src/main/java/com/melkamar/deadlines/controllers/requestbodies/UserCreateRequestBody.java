package com.melkamar.deadlines.controllers.requestbodies;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 08.04.2016 11:10
 */
public class UserCreateRequestBody {
    private String username;
    private String password;
    private String name;
    private String email;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
