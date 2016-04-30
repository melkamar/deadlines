package com.melkamar.deadlines.integration;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.melkamar.deadlines.DeadlinesApplication;
import com.melkamar.deadlines.config.ErrorCodes;
import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.task.Priority;
import com.melkamar.deadlines.model.task.Task;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Martin Melka
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
//
        group1 = groupApi.createGroup("Group1", user1, RandomString.get("Description "));
        group4 = groupApi.createGroup("Group4", user1, RandomString.get("Description "));
        group2 = groupApi.createGroup("Group2", user3, RandomString.get("Description "));
        group3 = groupApi.createGroup("Group3", user2, RandomString.get("Description "));
//
//
        task1 = taskApi.createTask(user1, "Task1", RandomString.get("Descr "), Priority.NORMAL, 15, LocalDateTime.now().plusHours(10));
//        task2 = taskApi.createTask(user1, "Task2", RandomString.get("Descr "), Priority.NORMAL, 15, LocalDateTime.now().plusHours(10));
//        task3 = taskApi.createTask(user1, "Task3", RandomString.get("Descr "), Priority.NORMAL, 15, LocalDateTime.now().plusHours(10));
//        task4 = taskApi.createTask(user1, "Task4", RandomString.get("UserSharing "), Priority.NORMAL, 15, LocalDateTime.now().plusHours(10));
//        task5 = taskApi.createTask(user1, "Task5", RandomString.get("UserSharing "), Priority.NORMAL, 15, LocalDateTime.now().plusHours(10));
//        task6 = taskApi.createTask(user1, "Task6", RandomString.get("UserSharing "), Priority.NORMAL, 15, LocalDateTime.now().plusHours(10));
//        task7 = taskApi.createTask(user1, "Task7", RandomString.get("GroupSharing "), Priority.NORMAL, 15, LocalDateTime.now().plusHours(10));
//        task8 = taskApi.createTask(user2, "Task8", RandomString.get("GroupSharing "), Priority.NORMAL, 15, 45);
//        task9 = taskApi.createTask(user1, "Task9", RandomString.get("GroupTask "), Priority.NORMAL, 15, 45);
//        task10 = taskApi.createTask(user3, "Task10", RandomString.get("GroupTask "), Priority.NORMAL, 15, 45);
//        task11 = taskApi.createTask(user1, "Task11", RandomString.get("GroupTask "), Priority.NORMAL, 15, LocalDateTime.now().plusHours(10));
//
//        participantHelper.editOrCreateTaskParticipant(user3, task3, TaskRole.WORKER, null, false);
//        groupApi.addTask(user3, group2, task3);
//
//        sharingApi.offerTaskSharing(user1, task4, user2);
//        sharingApi.offerTaskSharing(user1, task5, user3);
//        sharingApi.offerTaskSharing(user1, task6, user3);
//
//        sharingApi.offerTaskSharing(user1, task7, group1);
//        sharingApi.offerTaskSharing(user2, task8, group1);
//
//        sharingApi.offerMembership(user1, group1, user2);
//        sharingApi.offerMembership(user3, group2, user2);
//
//        groupApi.addTask(user1, group1, task9);
//        groupApi.addTask(user1, group1, task11);
//        groupApi.addTask(user3, group2, task10);
//
//        groupApi.addMember(user1, group1, user2);
    }

    @Test
    @Transactional
    public void userGet() throws Exception {
        MvcResult result = mvc.perform(get("/user"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        JsonParser parser = new JsonParser();
        JsonArray array = parser.parse(response).getAsJsonArray();

        Assert.assertEquals(3, array.size());

        JsonObject object = array.get(0).getAsJsonObject();
        Assert.assertNotNull(object.get("id"));
        Assert.assertNotNull(object.get("username"));
        Assert.assertNotNull(object.get("email"));
        Assert.assertNotNull(object.get("name"));

        JsonObject object2 = array.get(1).getAsJsonObject();
        Assert.assertNotNull(object2.get("id"));
        Assert.assertNotNull(object2.get("username"));
        Assert.assertTrue(object2.get("email").isJsonNull());
        Assert.assertTrue(object2.get("name").isJsonNull());

        System.out.println(JsonPrettyPrinter.prettyPrint(response));
    }

    @Transactional
    @Test
    public void userPost() throws Exception {
        int startUsers = userApi.listUsers().size();
        String request = "{\"username\":\"Created User\",\"password\":\"abraka\",\"email\":\"muj-email@neco.com\",\"name\":\"Name Of Created User\"}";

        MvcResult result = mvc.perform(post("/user")
                .content(request)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String response = result.getResponse().getContentAsString();

        JsonParser parser = new JsonParser();
        JsonObject object = parser.parse(response).getAsJsonObject();

        Assert.assertNotNull(object.get("id"));
        Assert.assertNotNull(object.get("username"));
        Assert.assertNotNull(object.get("email"));
        Assert.assertNotNull(object.get("name"));
        Assert.assertEquals(startUsers + 1, userApi.listUsers().size());

        System.out.println(JsonPrettyPrinter.prettyPrint(response));
    }

    @Transactional
    @Test
    public void userIdGet_noAuth() throws Exception {

        MvcResult result = mvc.perform(get("/user/" + user1.getId())
        )
                .andExpect(status().isUnauthorized())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        JsonParser parser = new JsonParser();
        JsonObject object = parser.parse(response).getAsJsonObject();

        Assert.assertEquals(ErrorCodes.INVALID_CREDENTIALS, object.get("errorCode").getAsInt());

        System.out.println("CODE: " + result.getResponse().getStatus());
        System.out.println(JsonPrettyPrinter.prettyPrint(response));
    }

    @Transactional
    @Test
    public void userIdGet_wrongAuth() throws Exception {

        MvcResult result = mvc.perform(get("/user/" + user1.getId())
                .header("Authorization", "somefoo")
        )
                .andExpect(status().isUnauthorized())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        System.out.println("CODE: " + result.getResponse().getStatus());
        System.out.println(JsonPrettyPrinter.prettyPrint(response));
    }

    @Transactional
    @Test
    public void userIdGet() throws Exception {

        MvcResult result = mvc.perform(get("/user/" + user1.getId())
                .header("Authorization", BasicAuthHeaderBuilder.buildAuthHeader(
                        user1.getUsername(), "pwd"))
        )
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        System.out.println("CODE: " + result.getResponse().getStatus());
        System.out.println(JsonPrettyPrinter.prettyPrint(response));

        JsonParser parser = new JsonParser();
        JsonObject object = parser.parse(response).getAsJsonObject();

        long id = user1.getId();
        Assert.assertEquals(id, object.get("id").getAsLong());
        Assert.assertEquals(user1.getUsername(), object.get("username").getAsString());
        Assert.assertEquals(user1.getEmail(), object.get("email").getAsString());
        Assert.assertEquals(user1.getName(), object.get("name").getAsString());


    }

    @Transactional
    @Test
    public void userIdPut() throws Exception {

        String newPwd = "dabra";
        String newEmail = "another-email@hello.com";
        String newName = "Brand new name";

        MvcResult result = mvc.perform(put("/user/" + user1.getId())
                .header("Authorization", BasicAuthHeaderBuilder.buildAuthHeader(user1.getUsername(), "pwd"))
                .content("{\"password\":\"" + newPwd
                        + "\",\"email\":\"" + newEmail
                        + "\",\"name\":\"" + newName + "\"}")
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        System.out.println("CODE: " + result.getResponse().getStatus());
        System.out.println(JsonPrettyPrinter.prettyPrint(response));

        JsonParser parser = new JsonParser();
        JsonObject object = parser.parse(response).getAsJsonObject();

        Assert.assertEquals(newEmail, object.get("email").getAsString());
        Assert.assertEquals(newName, object.get("name").getAsString());

        mvc.perform(get("/user/" + user1.getId())
                .header("Authorization", BasicAuthHeaderBuilder.buildAuthHeader(user1.getUsername(), "pwd"))
        )
                .andExpect(status().isUnauthorized());

        mvc.perform(get("/user/" + user1.getId())
                .header("Authorization", BasicAuthHeaderBuilder.buildAuthHeader(user1.getUsername(), newPwd))
        )
                .andExpect(status().isOk());
    }


    /**
     * Get all jobs of the logged user.
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
        System.out.println(JsonPrettyPrinter.prettyPrint(response));
    }

    @Transactional
    @Test
    public void taskGetWrongAuth() throws Exception {
        final String url = "/task";

        MvcResult result = mvc.perform(get(url)
                .header("Authorization", BasicAuthHeaderBuilder.buildAuthHeader(user1.getUsername(), "pwdwrong"))
        )
                .andExpect(status().isUnauthorized())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        System.out.println("CODE: " + result.getResponse().getStatus());
        System.out.println(JsonPrettyPrinter.prettyPrint(response));
    }

    @Transactional
    @Test
    public void taskPost() throws Exception {
        final String url = "/task";
        final String strcontent = "{\"name\":\"sometask\", \"description\":\"just stuff\",\"priority\":\"LOW\", \"workEstimate\":3,\"hoursToPeak\":12.5, \"groupIds\":[" + group1.getId() + "," + group4.getId() + "]}";

        MvcResult result = mvc.perform(post(url)
                .header("Authorization", BasicAuthHeaderBuilder.buildAuthHeader(user1.getUsername(), "pwd"))
                .content(strcontent)
                .contentType(MediaType.APPLICATION_JSON)
        )
//                .andExpect(status().isOk())
                .andReturn();

        for (String headername : result.getResponse().getHeaderNames()) {
            System.out.println(headername + ":" + result.getResponse().getHeader(headername));
        }
        String response = result.getResponse().getContentAsString();
        System.out.println("CODE: " + result.getResponse().getStatus());
        System.out.println(JsonPrettyPrinter.prettyPrint(response));

    }


//    /**
//     * Get all jobs of a group of the logged user.
//     */
//    @Transactional
//    @Test
//    public void taskGet_group() throws Exception {
//        final String url = "/task?group="+group1.getId();
//
//        MvcResult result = mvc.perform(get(url)
//                .header("Authorization", BasicAuthHeaderBuilder.buildAuthHeader(user1.getUsername(), "pwd"))
//        )
//                .andExpect(status().isOk())
//                .andReturn();
//
//        String response = result.getResponse().getContentAsString();
//        System.out.println("CODE: " + result.getResponse().getStatus());
//        System.out.println(JsonPrettyPrinter.prettyPrint(response));
//    }
//

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
        System.out.println(JsonPrettyPrinter.prettyPrint(response));
    }

    //
//    /**
//     * Get all groups of the logged user
//     */
//    @Transactional
//    @Test
//    public void groupGet_member() throws Exception {
//        final String url = "/group?role=any";
//
//        MvcResult result = mvc.perform(get(url)
//                .header("Authorization", BasicAuthHeaderBuilder.buildAuthHeader(user1.getUsername(), "pwd"))
//        )
//                .andExpect(status().isOk())
//                .andReturn();
//
//        String response = result.getResponse().getContentAsString();
//        System.out.println("CODE: " + result.getResponse().getStatus());
//        System.out.println(JsonPrettyPrinter.prettyPrint(response));
//    }
//
//    @Transactional
//    @Test
//    public void groupIdGet() throws Exception {
//        final String url = "/group/"+group1.getId();
//
//        MvcResult result = mvc.perform(get(url)
//                .header("Authorization", BasicAuthHeaderBuilder.buildAuthHeader(user1.getUsername(), "pwd"))
//        )
//                .andExpect(status().isOk())
//                .andReturn();
//
//        String response = result.getResponse().getContentAsString();
//        System.out.println("CODE: " + result.getResponse().getStatus());
//        System.out.println(JsonPrettyPrinter.prettyPrint(response));
//    }
//
//    /**
//     * Get task sharing offers of the logged user.
//     */
//    @Transactional
//    @Test
//    public void offerTaskUserGet() throws Exception {
//        final String url = "/offer/task/user";
//
//        MvcResult result = mvc.perform(get(url)
//                .header("Authorization", BasicAuthHeaderBuilder.buildAuthHeader(user3.getUsername(), "pwd"))
//        )
//                .andExpect(status().isOk())
//                .andReturn();
//
//        String response = result.getResponse().getContentAsString();
//        System.out.println("CODE: " + result.getResponse().getStatus());
//        System.out.println(JsonPrettyPrinter.prettyPrint(response));
//    }
//
//    /**
//     * Get task sharing offers of the logged user.
//     */
//    @Transactional
//    @Test
//    public void offerTaskGroupGet() throws Exception {
//
//        final String url = "/offer/task/group/"+group1.getId();
//
//        MvcResult result = mvc.perform(get(url)
//                .header("Authorization", BasicAuthHeaderBuilder.buildAuthHeader(user1.getUsername(), "pwd"))
//        )
//                .andExpect(status().isOk())
//                .andReturn();
//
//        String response = result.getResponse().getContentAsString();
//        System.out.println("CODE: " + result.getResponse().getStatus());
//        System.out.println(JsonPrettyPrinter.prettyPrint(response));
//    }
//
//    /**
//     * Get membership offers of the logged user.
//     */
//    @Transactional
//    @Test
//    public void offerMembershipGet() throws Exception {
//        final String url = "/offer/membership";
//
//        MvcResult result = mvc.perform(get(url)
//                .header("Authorization", BasicAuthHeaderBuilder.buildAuthHeader(user2.getUsername(), "pwd"))
//        )
//                .andExpect(status().isOk())
//                .andReturn();
//
//        String response = result.getResponse().getContentAsString();
//        System.out.println("CODE: " + result.getResponse().getStatus());
//        System.out.println(JsonPrettyPrinter.prettyPrint(response));
//    }
//
//
    @Transactional
    @Test
    public void taskIdGet() throws Exception {
        final String url = "/task/" + task1.getId();

        MvcResult result = mvc.perform(get(url)
                .header("Authorization", BasicAuthHeaderBuilder.buildAuthHeader(user1.getUsername(), "pwd"))
        )
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        System.out.println("CODE: " + result.getResponse().getStatus());
        System.out.println(JsonPrettyPrinter.prettyPrint(response));
    }

}
