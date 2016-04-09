package com.melkamar.deadlines.services.security;

import com.melkamar.deadlines.config.ErrorCodes;
import com.melkamar.deadlines.model.misc.ErrorResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 09.04.2016 15:52
 */
@Component
public class DeadlinesAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getOutputStream().println(new ErrorResponse(ErrorCodes.INVALID_CREDENTIALS, "Provided user credentials are invalid.").toString());
    }
}
