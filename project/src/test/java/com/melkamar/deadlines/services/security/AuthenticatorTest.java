package com.melkamar.deadlines.services.security;

import com.melkamar.deadlines.DeadlinesApplication;
import com.melkamar.deadlines.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.mockito.Mockito;

import static org.junit.Assert.*;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 31.03.2016 11:48
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DeadlinesApplication.class)
@WebAppConfiguration
public class AuthenticatorTest {
    User mockedUser = Mockito.mock(User.class);
    @Autowired
    private Authenticator authenticator;

    @Before
    public void setUp() throws Exception {
        Mockito.when(mockedUser.getUsername()).thenReturn("User2");
        Mockito.when(mockedUser.getPasswordHash()).thenReturn("375c3858a8d203ac6393d948098f797d7d871e32");
        Mockito.when(mockedUser.getPasswordSalt()).thenReturn("c7badfd9725a9f2e");
    }

    @Transactional
    @Test
    public void authenticate() throws Exception {
        String correctPassword = "password";
        String wrongPassword = "abcd";

        assertNull(authenticator.authenticate(mockedUser, wrongPassword));
        assertEquals(authenticator.authenticate(mockedUser, correctPassword), mockedUser);
    }
}