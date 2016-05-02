package com.melkamar.deadlines.services.security;

import com.melkamar.deadlines.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(MockitoJUnitRunner.class)
public class AuthenticatorTest {
    @Mock
    User mockedUser;

    @Spy
    private ShaPasswordEncoder passwordEncoder = new ShaPasswordEncoder();

    @InjectMocks
    private Authenticator authenticator = new Authenticator();


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
