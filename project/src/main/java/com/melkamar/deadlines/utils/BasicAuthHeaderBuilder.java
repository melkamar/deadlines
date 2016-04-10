package com.melkamar.deadlines.utils;

import java.util.Base64;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 10.04.2016 18:29
 */
public class BasicAuthHeaderBuilder {
    public static String buildAuthHeader(String username, String password) {
        String str = username + ":" + password;
        String encoded = "Basic " + Base64.getEncoder().encodeToString(str.getBytes());
        System.out.println("ENCODING: " + username + " " + password + " --> " + encoded);
        return encoded;
    }
}
