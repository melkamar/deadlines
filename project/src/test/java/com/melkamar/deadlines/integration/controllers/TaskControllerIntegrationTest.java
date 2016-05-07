/*
 * Copyright (c) 2016 Martin Melka
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.melkamar.deadlines.integration.controllers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.melkamar.deadlines.DeadlinesApplication;
import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.offer.GroupTaskSharingOffer;
import com.melkamar.deadlines.model.offer.MembershipOffer;
import com.melkamar.deadlines.model.offer.UserTaskSharingOffer;
import com.melkamar.deadlines.model.task.Priority;
import com.melkamar.deadlines.model.task.Task;
import com.melkamar.deadlines.model.task.TaskRole;
import com.melkamar.deadlines.services.api.GroupApi;
import com.melkamar.deadlines.services.api.SharingApi;
import com.melkamar.deadlines.services.api.TaskApi;
import com.melkamar.deadlines.services.api.UserApi;
import com.melkamar.deadlines.services.helpers.TaskParticipantHelper;
import com.melkamar.deadlines.utils.BasicAuthHeaderBuilder;
import com.melkamar.deadlines.utils.JsonPrettyPrinter;
import com.melkamar.deadlines.utils.RandomString;
import org.junit.Assert;
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

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 07.05.2016 12:34
 */
@SuppressWarnings("Duplicates")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DeadlinesApplication.class)
@WebAppConfiguration
public class TaskControllerIntegrationTest {
    @Autowired
    private WebApplicationContext webApplicationContext;

    MockMvc mvc;
    @Autowired
    private UserApi userApi;
    @Autowired
    private GroupApi groupApi;
    @Autowired
    private TaskApi taskApi;
    @Autowired
    private SharingApi sharingApi;

    User user1;
    User user2;
    User user3;
    User user4;

    Group group1;
    Group group2;
    Group group3;
    Group group4;

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

    UserTaskSharingOffer taskSharingOfferTask4ToUser2;
    UserTaskSharingOffer taskSharingOfferTask6ToUser3;

    GroupTaskSharingOffer taskSharingOfferTask7ToGroup1;
    GroupTaskSharingOffer taskSharingOfferTask8ToGroup1;

    MembershipOffer membershipOfferGroup1ToUser4;
    MembershipOffer membershipOfferGroup2ToUser2;

    @Autowired
    private TaskParticipantHelper participantHelper;

    @Transactional
    @Before
    public void setUp() throws Exception {
        mvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();


        user1 = userApi.createUser("User1", "pwd", RandomString.get("Name "), RandomString.getEmail());
        user2 = userApi.createUser("User2", "pwd", null, null);
        user3 = userApi.createUser("User3", "pwd", RandomString.get("Name "), RandomString.getEmail());
        user4 = userApi.createUser("User4", "pwd", RandomString.get("Name "), RandomString.getEmail());
//
        group1 = groupApi.createGroup("Group1", user1, RandomString.get("Description "));
        group4 = groupApi.createGroup("Group4", user1, RandomString.get("Description "));
        group2 = groupApi.createGroup("Group2", user3, RandomString.get("Description "));
        group3 = groupApi.createGroup("Group3", user2, RandomString.get("Description "));
//
//
        task1 = taskApi.createTask(user1, "Task1", RandomString.get("Descr "), Priority.NORMAL, 15, LocalDateTime.now().plusHours(10));
        task2 = taskApi.createTask(user1, "Task2", RandomString.get("Descr "), Priority.NORMAL, 15, LocalDateTime.now().plusHours(10));
        task3 = taskApi.createTask(user1, "Task3", RandomString.get("Descr "), Priority.NORMAL, 15, LocalDateTime.now().plusHours(10));
        task4 = taskApi.createTask(user1, "Task4", RandomString.get("UserSharing "), Priority.NORMAL, 15, LocalDateTime.now().plusHours(10));
        task5 = taskApi.createTask(user1, "Task5", RandomString.get("UserSharing "), Priority.NORMAL, 15, LocalDateTime.now().plusHours(10));
        task6 = taskApi.createTask(user1, "Task6", RandomString.get("UserSharing "), Priority.NORMAL, 15, LocalDateTime.now().plusHours(10));
        task7 = taskApi.createTask(user1, "Task7", RandomString.get("GroupSharing "), Priority.NORMAL, 15, LocalDateTime.now().plusHours(10));
        task8 = taskApi.createTask(user2, "Task8", RandomString.get("GroupSharing "), Priority.NORMAL, 15, 45);
        task9 = taskApi.createTask(user1, "Task9", RandomString.get("GroupTask "), Priority.NORMAL, 15, 45);
        task10 = taskApi.createTask(user3, "Task10", RandomString.get("GroupTask "), Priority.NORMAL, 15, 45);
        task11 = taskApi.createTask(user1, "Task11", RandomString.get("GroupTask "), Priority.NORMAL, 15, LocalDateTime.now().plusHours(10));

        participantHelper.editOrCreateTaskParticipant(user3, task3, TaskRole.WORKER, null, false);
        groupApi.addTask(user3, group2, task3);

        taskApi.setTaskRole(user1, task1, TaskRole.WORKER);

        taskSharingOfferTask4ToUser2 = sharingApi.offerTaskSharing(user1, task4, user2);
        taskSharingOfferTask6ToUser3 = sharingApi.offerTaskSharing(user1, task5, user3);
        sharingApi.offerTaskSharing(user1, task6, user3);

        taskSharingOfferTask7ToGroup1 = sharingApi.offerTaskSharing(user1, task7, group1);
        taskSharingOfferTask8ToGroup1 = sharingApi.offerTaskSharing(user2, task8, group1);

        membershipOfferGroup1ToUser4 = sharingApi.offerMembership(user1, group1, user4);
        membershipOfferGroup2ToUser2 = sharingApi.offerMembership(user3, group2, user2);

        groupApi.addTask(user1, group1, task9);
        groupApi.addTask(user1, group1, task11);
        groupApi.addTask(user3, group2, task10);

        groupApi.addMember(user1, group1, user2);
    }

    @Transactional
    @Test
    public void taskPost() throws Exception {

        String request = "{\"name\": \"task name\"," +
                "\"description\":\"task description\"," +
                "\"priority\": \"LOW\"," +
                "\"workEstimate\": \"13\"," +
                "\"deadline\":\"2016-05-17 13:15\"," +
                "\"groupIds\":["+group1.getId()+"]}";

        MvcResult result = mvc.perform(post("/task")
                .header("Authorization", BasicAuthHeaderBuilder.buildAuthHeader(user1.getUsername(), "pwd"))
                .content(request)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        System.out.println("*****************************************************************************************");
        System.out.println("HTTP CODE:" + result.getResponse().getStatus());
        System.out.println(JsonPrettyPrinter.prettyPrint(result.getResponse().getContentAsString()));
        System.out.println("*****************************************************************************************");

        JsonParser parser = new JsonParser();
        JsonObject object = parser.parse(result.getResponse().getContentAsString()).getAsJsonObject();

        Assert.assertEquals(2, object.getAsJsonArray("participants").size());
        Assert.assertEquals(1, object.getAsJsonArray("groups").size());
    }

    @Transactional
    @Test
    public void taskIdGet() throws Exception {
        MvcResult result = mvc.perform(get("/task/" + task1.getId())
                .header("Authorization", BasicAuthHeaderBuilder.buildAuthHeader(user1.getUsername(), "pwd")))
                .andExpect(status().isOk())
                .andReturn();

        System.out.println("*****************************************************************************************");
        System.out.println("HTTP CODE:" + result.getResponse().getStatus());
        System.out.println(JsonPrettyPrinter.prettyPrint(result.getResponse().getContentAsString()));
        System.out.println("*****************************************************************************************");

        JsonParser parser = new JsonParser();
        JsonObject object = parser.parse(result.getResponse().getContentAsString()).getAsJsonObject();

        Assert.assertEquals("Task1", object.get("name").getAsString());
    }

    @Transactional
    @Test
    public void taskIdPut() throws Exception {
        Assert.assertNotEquals("abraka", task1.getDescription());
        String request = "{\"description\":\"abraka\"}";
        MvcResult result = mvc.perform(put("/task/" + task1.getId())
                .header("Authorization", BasicAuthHeaderBuilder.buildAuthHeader(user1.getUsername(), "pwd"))
                .content(request)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        System.out.println("*****************************************************************************************");
        System.out.println("HTTP CODE:" + result.getResponse().getStatus());
        System.out.println(JsonPrettyPrinter.prettyPrint(result.getResponse().getContentAsString()));
        System.out.println("*****************************************************************************************");

        JsonParser parser = new JsonParser();
        JsonObject object = parser.parse(result.getResponse().getContentAsString()).getAsJsonObject();

        Assert.assertEquals("abraka", object.get("description").getAsString());
    }

    @Transactional
    @Test
    public void taskShare() throws Exception {
        int user4offers = user4.getTaskOffers().size();
        int user3offers = user3.getTaskOffers().size();
        int group3offers = group3.getTaskOffers().size();

        String request = "{\"userIds\":[ " + user4.getId() + ", " + user3.getId() + " ], \"groupIds\":[" + group3.getId() + "]}";
        System.out.println("Request: " + request);
        MvcResult result = mvc.perform(post("/task/share/" + task1.getId())
                .header("Authorization", BasicAuthHeaderBuilder.buildAuthHeader(user1.getUsername(), "pwd"))
                .content(request)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(status().isOk())
                .andReturn();

//        System.out.println("*****************************************************************************************");
//        System.out.println("HTTP CODE:" + result.getResponse().getStatus());
//        System.out.println(JsonPrettyPrinter.prettyPrint(result.getResponse().getContentAsString()));
//        System.out.println("*****************************************************************************************");

        System.out.println("OUTPUT---------");
        System.out.println(result.getResponse().getContentAsString());
        System.out.println("---------OUTPUT");

        System.out.println(sharingApi.listTaskOffersOfGroup(user2, group3));
        System.out.println("User1 sharing offers: " + sharingApi.listTaskOffersOfUser(user1));
        System.out.println("User2 sharing offers: " + sharingApi.listTaskOffersOfUser(user2));
        System.out.println("User3 sharing offers: " + sharingApi.listTaskOffersOfUser(user3));
        System.out.println("User4 sharing offers: " + sharingApi.listTaskOffersOfUser(user4));

        Assert.assertEquals(user4offers + 1, user4.getTaskOffers().size());
        Assert.assertEquals(user3offers + 1, user3.getTaskOffers().size());
        Assert.assertEquals(group3offers + 1, group3.getTaskOffers().size());
    }

    @Transactional
    @Test
    public void taskLeave() throws Exception {
        Assert.assertTrue(user1.getTasksOfUser().contains(task1));

        MvcResult result = mvc.perform(post("/task/leave/" + task1.getId())
                .header("Authorization", BasicAuthHeaderBuilder.buildAuthHeader(user1.getUsername(), "pwd"))
        )
                .andReturn();


        Assert.assertFalse(user1.getTasksOfUser().contains(task1));

    }

    @Transactional
    @Test
    public void taskRoleOwn() throws Exception {
        Assert.assertTrue(taskApi.getTaskParticipant(user1, task1).getRole() == TaskRole.WORKER);

        MvcResult result = mvc.perform(post("/task/role/" + task1.getId() + "?newRole=watcher")
                .header("Authorization", BasicAuthHeaderBuilder.buildAuthHeader(user1.getUsername(), "pwd"))
        )
                .andExpect(status().isOk())
                .andReturn();

        System.out.println("*****************************************************************************************");
        System.out.println("HTTP CODE:" + result.getResponse().getStatus());
        System.out.println(JsonPrettyPrinter.prettyPrint(result.getResponse().getContentAsString()));
        System.out.println("*****************************************************************************************");

        Assert.assertTrue(taskApi.getTaskParticipant(user1, task1).getRole() == TaskRole.WATCHER);

        result = mvc.perform(post("/task/role/" + task1.getId() + "?newRole=worker")
                .header("Authorization", BasicAuthHeaderBuilder.buildAuthHeader(user1.getUsername(), "pwd"))
        )
                .andReturn();

        System.out.println("*****************************************************************************************");
        System.out.println("HTTP CODE:" + result.getResponse().getStatus());
        System.out.println(JsonPrettyPrinter.prettyPrint(result.getResponse().getContentAsString()));
        System.out.println("*****************************************************************************************");

        Assert.assertTrue(taskApi.getTaskParticipant(user1, task1).getRole() == TaskRole.WORKER);

    }

    @Transactional
    @Test
    public void taskRoleGroupMember() throws Exception {

//        group1, user2, task9
        Assert.assertTrue(taskApi.getTaskParticipant(user2, task9).getRole() == TaskRole.WATCHER);

        MvcResult result = mvc.perform(post("/task/role/" + task9.getId() + "?newRole=worker&targetUser=" + user2.getId() + "&targetGroup=" + group1.getId())
                .header("Authorization", BasicAuthHeaderBuilder.buildAuthHeader(user1.getUsername(), "pwd"))
        )
                .andExpect(status().isOk())
                .andReturn();

        System.out.println("*****************************************************************************************");
        System.out.println("HTTP CODE:" + result.getResponse().getStatus());
        System.out.println(JsonPrettyPrinter.prettyPrint(result.getResponse().getContentAsString()));
        System.out.println("*****************************************************************************************");

        Assert.assertTrue(taskApi.getTaskParticipant(user2, task9).getRole() == TaskRole.WORKER);

        result = mvc.perform(post("/task/role/" + task9.getId() + "?newRole=watcher&targetUser=" + user2.getId() + "&targetGroup=" + group1.getId())
                .header("Authorization", BasicAuthHeaderBuilder.buildAuthHeader(user1.getUsername(), "pwd"))
        )
                .andExpect(status().isOk())
                .andReturn();

        System.out.println("*****************************************************************************************");
        System.out.println("HTTP CODE:" + result.getResponse().getStatus());
        System.out.println(JsonPrettyPrinter.prettyPrint(result.getResponse().getContentAsString()));
        System.out.println("*****************************************************************************************");

        Assert.assertTrue(taskApi.getTaskParticipant(user2, task9).getRole() == TaskRole.WATCHER);

    }

    @Transactional
    @Test
    public void taskReportWork() throws Exception {
        double beginWork = task1.getManhoursWorked();
        double reportWorked = 10.2;

        MvcResult result = mvc.perform(post("/task/report/" + task1.getId() + "?worked=" + reportWorked)
                .header("Authorization", BasicAuthHeaderBuilder.buildAuthHeader(user1.getUsername(), "pwd"))
        )
                .andExpect(status().isOk())
                .andReturn();


        Assert.assertEquals(beginWork + reportWorked, task1.getManhoursWorked(), 0.001);

        result = mvc.perform(get("/task/" + task1.getId())
                .header("Authorization", BasicAuthHeaderBuilder.buildAuthHeader(user1.getUsername(), "pwd")))
                .andExpect(status().isOk())
                .andReturn();

        System.out.println("*****************************************************************************************");
        System.out.println("HTTP CODE:" + result.getResponse().getStatus());
        System.out.println(JsonPrettyPrinter.prettyPrint(result.getResponse().getContentAsString()));
        System.out.println("*****************************************************************************************");

    }
}
