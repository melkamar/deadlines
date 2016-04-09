package com.melkamar.deadlines.config;

import com.melkamar.deadlines.services.security.DeadlinesAuthenticationEntryPoint;
import com.melkamar.deadlines.services.security.DeadlinesAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 07.04.2016 21:16
 */
@Configuration
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class ApplicationSecurity extends WebSecurityConfigurerAdapter {
    @Autowired
    DeadlinesAuthenticationProvider authenticationProvider;
    @Autowired
    DeadlinesAuthenticationEntryPoint deadlinesAuthenticationEntryPoint;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authenticationProvider(authenticationProvider)
                .authorizeRequests()
                .antMatchers("/user").permitAll()
                .antMatchers("/user/**").permitAll()
                .anyRequest().authenticated().and().httpBasic().authenticationEntryPoint(deadlinesAuthenticationEntryPoint);
//                .anyRequest().permitAll();
    }
}
