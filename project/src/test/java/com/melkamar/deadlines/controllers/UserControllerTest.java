package com.melkamar.deadlines.controllers;

import com.melkamar.deadlines.config.ErrorCodes;
import com.melkamar.deadlines.exceptions.UserAlreadyExistsException;
import com.melkamar.deadlines.exceptions.WrongParameterException;
import com.melkamar.deadlines.services.api.UserAPI;
import org.hamcrest.core.StringContains;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.Matchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 08.04.2016 15:14
 */
@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {
    private MockMvc mvc;
    @Mock
    UserAPI userAPI;

    @InjectMocks
    UserController testedController;


    @Before
    public void setUp() throws Exception {
        mvc = MockMvcBuilders.standaloneSetup(testedController).build();
    }

    @Test
    @Transactional
    public void testCreateUser() throws Exception {
        this.mvc.perform(MockMvcRequestBuilders
                .post("/user").content("{ \"username\":\"NewgUasasdername\", \"password\":\"haha\"} ").contentType("application/json"))
                .andExpect(status().isCreated());
    }

    @Test
    @Transactional
    public void testCreateDuplicateUser() throws Exception {
        Mockito.when(userAPI.createUser(any(), any(), any(), any())).thenThrow(new UserAlreadyExistsException("Foo msg"));
        this.mvc.perform(MockMvcRequestBuilders
                .post("/user").content("{ \"username\":\"NewgUasasdername\", \"password\":\"haha\"} ").contentType("application/json"))
                .andExpect(status().isConflict())
                .andExpect(content().string(StringContains.containsString(
                        "\"errorCode\":" + ErrorCodes.USER_ALREADY_EXISTS)));

    }

    @Test
    @Transactional
    public void testCreateUserMissingParams() throws Exception {
        Mockito.when(userAPI.createUser(any(), any(), any(), any())).thenThrow(new WrongParameterException("Foo msg"));
        this.mvc.perform(MockMvcRequestBuilders
                .post("/user").content("{ \"username\":\"NewgUasasdername\"} ").contentType("application/json"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(StringContains.containsString(
                        "\"errorCode\":" + ErrorCodes.WRONG_PARAMETERS)));
    }

}