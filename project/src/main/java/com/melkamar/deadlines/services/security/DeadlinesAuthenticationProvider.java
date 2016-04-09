package com.melkamar.deadlines.services.security;

import com.melkamar.deadlines.dao.user.UserDAO;
import com.melkamar.deadlines.dao.user.UserDAOHibernate;
import com.melkamar.deadlines.exceptions.RESTAuthenticationException;
import com.melkamar.deadlines.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 09.04.2016 9:46
 */
@Component
public class DeadlinesAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private Authenticator authenticator;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        User user = userDAO.findByUsername(username);
        User authenticatedUser = authenticator.authenticate(user, password);
        if (authenticatedUser == null){
            throw new RESTAuthenticationException("Auth failed");
        }

        System.out.println("OUT:" + user.getUsername());

        List<GrantedAuthority> authorityList = new ArrayList<>();
        authorityList.add(new SimpleGrantedAuthority("ROLE_USER"));
        return new UsernamePasswordAuthenticationToken(authenticatedUser.getId(), password, authorityList);
    }

    @Override
    public boolean supports(Class<?> aClass) {
//        return aClass.equals(UsernamePasswordAuthenticationToken.class);
        return true;
    }
}
