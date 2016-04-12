package com.melkamar.deadlines.controllers;

import com.melkamar.deadlines.config.ErrorCodes;
import com.melkamar.deadlines.exceptions.AlreadyExistsException;
import com.melkamar.deadlines.exceptions.WrongParameterException;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.services.api.UserApi;
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

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 08.04.2016 15:14
 */
@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {
    private MockMvc mvc;
    @Mock
    UserApi userApi;
    @Mock
    User user;

    @InjectMocks
    UserController testedController;


    @Before
    public void setUp() throws Exception {

        mvc = MockMvcBuilders
                .standaloneSetup(testedController)
                .build();
    }

    @Test
    public void testListUsers() throws Exception {
        this.mvc.perform(MockMvcRequestBuilders
                .get("/user")).andExpect(status().isOk());
        Mockito.verify(userApi, Mockito.times(1)).listUsers();
    }

    @Test
    public void testCreateUser() throws Exception {
        when(userApi.createUser(any(), any(), any(), any())).thenReturn(user);
        when(user.getId()).thenReturn(12467L);

        this.mvc.perform(MockMvcRequestBuilders
                .post("/user").content("{ \"username\":\"NewgUasasdername\", \"password\":\"haha\"} ").contentType("application/json"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/user/12467"));
    }

    @Test
    public void testCreateDuplicateUser() throws Exception {
        Mockito.when(userApi.createUser(any(), any(), any(), any())).thenThrow(new AlreadyExistsException("Foo msg"));
        this.mvc.perform(MockMvcRequestBuilders
                .post("/user").content("{ \"username\":\"NewgUasasdername\", \"password\":\"haha\"} ").contentType("application/json"))
                .andExpect(status().isConflict())
                .andExpect(content().string(StringContains.containsString(
                        "\"errorCode\":" + ErrorCodes.USER_ALREADY_EXISTS)));

    }

    @Test
    public void testCreateUserMissingParams() throws Exception {
        Mockito.when(userApi.createUser(any(), any(), any(), any())).thenThrow(new WrongParameterException("Foo msg"));
        this.mvc.perform(MockMvcRequestBuilders
                .post("/user").content("{ \"username\":\"NewgUasasdername\"} ").contentType("application/json"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(StringContains.containsString(
                        "\"errorCode\":" + ErrorCodes.WRONG_PARAMETERS)));
    }

    @Test
    public void editUser() throws Exception {
        when(userApi.getUser(any(Long.class))).thenReturn(user);
        when(user.getId()).thenReturn(1L);

        this.mvc.perform(MockMvcRequestBuilders
                .put("/user/1").content("{\"password\":\"haha\"} ").contentType("application/json"))
                .andExpect(status().isOk());
        Mockito.verify(userApi, Mockito.times(1)).editUserDetails(any(User.class), isNull(String.class), isNull(String.class), eq("haha"));
    }

    @Test
    public void editUserBadId() throws Exception {
        when(userApi.getUser(any(Long.class))).thenReturn(user);
        when(user.getId()).thenReturn(2L);

        this.mvc.perform(MockMvcRequestBuilders
                .put("/user/1").content("{\"password\":\"haha\"} ").contentType("application/json"))
                .andExpect(status().isForbidden());
    }
}