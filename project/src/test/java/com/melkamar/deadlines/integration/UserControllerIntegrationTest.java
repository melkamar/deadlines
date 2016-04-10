package com.melkamar.deadlines.integration;

import com.melkamar.deadlines.DeadlinesApplication;
import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.task.Priority;
import com.melkamar.deadlines.model.task.Task;
import com.melkamar.deadlines.model.task.TaskRole;
import com.melkamar.deadlines.services.api.GroupAPI;
import com.melkamar.deadlines.services.api.SharingAPI;
import com.melkamar.deadlines.services.api.TaskAPI;
import com.melkamar.deadlines.services.api.UserAPI;
import com.melkamar.deadlines.services.helpers.TaskParticipantHelper;
import com.melkamar.deadlines.utils.BasicAuthHeaderBuilder;
import com.melkamar.deadlines.utils.JsonPrettyPrint;
import com.melkamar.deadlines.utils.RandomString;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 10.04.2016 17:15
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DeadlinesApplication.class)
@WebAppConfiguration
public class UserControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    MockMvc mvc;
    @Autowired
    private UserAPI userAPI;
    @Autowired
    private GroupAPI groupAPI;
    @Autowired
    private TaskAPI taskAPI;
    @Autowired
    private SharingAPI sharingAPI;

    User user1;
    User user2;
    User user3;
    @Autowired
    private TaskParticipantHelper participantHelper;

    @Transactional
    @Before
    public void setUp() throws Exception {


        mvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();


        user1 = userAPI.createUser("User1", "pwd", RandomString.get("Name "), RandomString.get("Email "));
//        String user1encodedAuth =
        user2 = userAPI.createUser("User2", "pwd", RandomString.get("Name "), RandomString.get("Email "));
        user3 = userAPI.createUser("User3", "pwd", RandomString.get("Name "), RandomString.get("Email "));

        Group group1 = groupAPI.createGroup("Group1", user1, RandomString.get("Description "));
        Group group2 = groupAPI.createGroup("Group2", user3, RandomString.get("Description "));


        Task task1 = taskAPI.createTask(user1, "Task1", RandomString.get("Descr "), Priority.NORMAL, 15, LocalDateTime.now().plusHours(10));
        Task task2 = taskAPI.createTask(user1, "Task2", RandomString.get("Descr "), Priority.NORMAL, 15, LocalDateTime.now().plusHours(10));
        Task task3 = taskAPI.createTask(user1, "Task3", RandomString.get("Descr "), Priority.NORMAL, 15, LocalDateTime.now().plusHours(10));

        participantHelper.editOrCreateTaskParticipant(user3, task3, TaskRole.WORKER, null, false);
        groupAPI.addTask(user3, group2, task3);

        System.out.println("***** CREATED USERS *****");
        System.out.println(user1);
        System.out.println(user2);
        System.out.println(user3);
        System.out.println("*************************");
    }

    @Test
    @Transactional
    public void userGet() throws Exception {

        MvcResult result = mvc.perform(get("/user"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String response = result.getResponse().getContentAsString();

        System.out.println(JsonPrettyPrint.prettyPrint(response));
    }

    @Transactional
    @Test
    public void userPost() throws Exception {
        String request = "{\"username\":\"Created User\",\"password\":\"abraka\",\"email\":\"muj-email\",\"name\":\"Name Of Created User\"}";

        MvcResult result = mvc.perform(post("/user")
                .content(request)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String response = result.getResponse().getContentAsString();

        System.out.println(JsonPrettyPrint.prettyPrint(response));
    }

    @Transactional
    @Test
    public void userIdGet() throws Exception {

        MvcResult result = mvc.perform(get("/user/"+user1.getId())
                .header("Authorization", "somefoo")
        )
                .andReturn();

        String response = result.getResponse().getContentAsString();
        System.out.println("CODE: " + result.getResponse().getStatus());
        System.out.println(JsonPrettyPrint.prettyPrint(response));
    }

    @Transactional
    @Test
    public void userIdPut() throws Exception {


        MvcResult result = mvc.perform(put("/user/"+user1.getId())
                .header("Authorization", BasicAuthHeaderBuilder.buildAuthHeader(user1.getUsername(), "pwd"))
                .content("{\"password\":\"dabra\",\"email\":\"another-email\",\"name\":\"Brand New Name\"}")
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andReturn();

        String response = result.getResponse().getContentAsString();
        System.out.println("CODE: " + result.getResponse().getStatus());
        System.out.println(JsonPrettyPrint.prettyPrint(response));
    }

    /**
     * Get all tasks of the logged user.
     */
    @Transactional
    @Test
    public void taskGet() throws Exception {
        final String url = "/task";

        MvcResult result = mvc.perform(get(url)
                .header("Authorization", BasicAuthHeaderBuilder.buildAuthHeader(user1.getUsername(), "pwd"))
        )
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        System.out.println("CODE: " + result.getResponse().getStatus());
        System.out.println(JsonPrettyPrint.prettyPrint(response));
    }

    /**
     * Get all tasks of the logged user.
     */
    @Transactional
    @Test
    public void groupGet() throws Exception {
        final String url = "/group";

        MvcResult result = mvc.perform(get(url)
                .header("Authorization", BasicAuthHeaderBuilder.buildAuthHeader(user1.getUsername(), "pwd"))
        )
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        System.out.println("CODE: " + result.getResponse().getStatus());
        System.out.println(JsonPrettyPrint.prettyPrint(response));
    }


}
