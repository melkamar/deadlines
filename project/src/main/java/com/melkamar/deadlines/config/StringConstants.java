package com.melkamar.deadlines.config;

import org.springframework.stereotype.Service;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 26.03.2016 15:06
 */
@Service("stringConstants")
public class StringConstants {
    public final String EXC_PARAM_USERNAME_EMPTY = "Username cannot be empty.";
    public final String EXC_PARAM_PASSWORD_EMPTY = "Password cannot be empty.";
    public final String EXC_PARAM_NAME_EMPTY = "Name cannot be empty.";
    public final String EXC_PARAM_FOUNDER_NULL = "Founder cannot be null.";
}
