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
@SuppressWarnings("Duplicates")
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

    Group group1;
    Group group2;
    Group group3;

    Task task1;
    Task task2;
    Task task3;
    Task task4;
    Task task5;
    Task task6;
    Task task7;
    Task task8;
    Task task9;
    Task task10;
    Task task11;

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
        user2 = userAPI.createUser("User2", "pwd", RandomString.get("Name "), RandomString.get("Email "));
        user3 = userAPI.createUser("User3", "pwd", RandomString.get("Name "), RandomString.get("Email "));

        group1 = groupAPI.createGroup("Group1", user1, RandomString.get("Description "));
        group2 = groupAPI.createGroup("Group2", user3, RandomString.get("Description "));
        group3 = groupAPI.createGroup("Group3", user2, RandomString.get("Description "));


        task1 = taskAPI.createTask(user1, "Task1", RandomString.get("Descr "), Priority.NORMAL, 15, LocalDateTime.now().plusHours(10));
        task2 = taskAPI.createTask(user1, "Task2", RandomString.get("Descr "), Priority.NORMAL, 15, LocalDateTime.now().plusHours(10));
        task3 = taskAPI.createTask(user1, "Task3", RandomString.get("Descr "), Priority.NORMAL, 15, LocalDateTime.now().plusHours(10));
        task4 = taskAPI.createTask(user1, "Task4", RandomString.get("UserSharing "), Priority.NORMAL, 15, LocalDateTime.now().plusHours(10));
        task5 = taskAPI.createTask(user1, "Task5", RandomString.get("UserSharing "), Priority.NORMAL, 15, LocalDateTime.now().plusHours(10));
        task6 = taskAPI.createTask(user1, "Task6", RandomString.get("UserSharing "), Priority.NORMAL, 15, LocalDateTime.now().plusHours(10));
        task7 = taskAPI.createTask(user1, "Task7", RandomString.get("GroupSharing "), Priority.NORMAL, 15, LocalDateTime.now().plusHours(10));
        task8 = taskAPI.createTask(user2, "Task8", RandomString.get("GroupSharing "), Priority.NORMAL, 15, 45);
        task9 = taskAPI.createTask(user1, "Task9", RandomString.get("GroupTask "), Priority.NORMAL, 15, 45);
        task10 = taskAPI.createTask(user3, "Task10", RandomString.get("GroupTask "), Priority.NORMAL, 15, 45);
        task11 = taskAPI.createTask(user1, "Task11", RandomString.get("GroupTask "), Priority.NORMAL, 15, LocalDateTime.now().plusHours(10));

        participantHelper.editOrCreateTaskParticipant(user3, task3, TaskRole.WORKER, null, false);
        groupAPI.addTask(user3, group2, task3);

        sharingAPI.offerTaskSharing(user1, task4, user2);
        sharingAPI.offerTaskSharing(user1, task5, user3);
        sharingAPI.offerTaskSharing(user1, task6, user3);

        sharingAPI.offerTaskSharing(user1, task7, group1);
        sharingAPI.offerTaskSharing(user2, task8, group1);

        sharingAPI.offerMembership(user1, group1, user2);
        sharingAPI.offerMembership(user3, group2, user2);

        groupAPI.addTask(user1, group1, task9);
        groupAPI.addTask(user1, group1, task11);
        groupAPI.addTask(user3, group2, task10);

        groupAPI.addMember(user1, group1, user2);

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
     * Get all tasks of a group of the logged user.
     */
    @Transactional
    @Test
    public void taskGet_group() throws Exception {
        final String url = "/task?group="+group1.getId();

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
     * Get all groups in the system.
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

    /**
     * Get all groups of the logged user
     */
    @Transactional
    @Test
    public void groupGet_member() throws Exception {
        final String url = "/group?role=any";

        MvcResult result = mvc.perform(get(url)
                .header("Authorization", BasicAuthHeaderBuilder.buildAuthHeader(user1.getUsername(), "pwd"))
        )
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        System.out.println("CODE: " + result.getResponse().getStatus());
        System.out.println(JsonPrettyPrint.prettyPrint(response));
    }

    @Transactional
    @Test
    public void groupIdGet() throws Exception {
        final String url = "/group/"+group1.getId();

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
     * Get task sharing offers of the logged user.
     */
    @Transactional
    @Test
    public void offerTaskUserGet() throws Exception {
        final String url = "/offer/task/user";

        MvcResult result = mvc.perform(get(url)
                .header("Authorization", BasicAuthHeaderBuilder.buildAuthHeader(user3.getUsername(), "pwd"))
        )
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        System.out.println("CODE: " + result.getResponse().getStatus());
        System.out.println(JsonPrettyPrint.prettyPrint(response));
    }

    /**
     * Get task sharing offers of the logged user.
     */
    @Transactional
    @Test
    public void offerTaskGroupGet() throws Exception {

        final String url = "/offer/task/group/"+group1.getId();

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
     * Get membership offers of the logged user.
     */
    @Transactional
    @Test
    public void offerMembershipGet() throws Exception {
        final String url = "/offer/membership";

        MvcResult result = mvc.perform(get(url)
                .header("Authorization", BasicAuthHeaderBuilder.buildAuthHeader(user2.getUsername(), "pwd"))
        )
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        System.out.println("CODE: " + result.getResponse().getStatus());
        System.out.println(JsonPrettyPrint.prettyPrint(response));
    }


    /**
     * Get task sharing offers of the logged user.
     */
    @Transactional
    @Test
    public void taskIdGet() throws Exception {
        final String url = "/task/"+task1.getId();

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
