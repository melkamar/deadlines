package com.melkamar.deadlines;

import com.melkamar.deadlines.exceptions.*;
import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.offer.UserTaskSharingOffer;
import com.melkamar.deadlines.model.task.Priority;
import com.melkamar.deadlines.model.task.Task;
import com.melkamar.deadlines.model.task.TaskRole;
import com.melkamar.deadlines.services.api.GroupAPI;
import com.melkamar.deadlines.services.api.SharingAPI;
import com.melkamar.deadlines.services.api.TaskAPI;
import com.melkamar.deadlines.services.api.UserAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;

@SpringBootApplication
public class DeadlinesApplication {
    @Autowired
    private TaskAPI taskAPI;
    @Autowired
    private SharingAPI sharingAPI;
    @Autowired
    private GroupAPI groupAPI;

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(DeadlinesApplication.class, args);

        for (String beanname : ctx.getBeanDefinitionNames()) {
            System.out.println("BEAN: " + beanname);
        }
    }


    @Autowired
    private UserAPI userAPI;

    @PostConstruct
    public void doStuff() {
        boolean fillSampleData = false;
//        boolean fillSampleData = true;


        if (!fillSampleData) return;
        try {
            User user = userAPI.createUser("abc", "heya", "dummy user", null);
            Task task = taskAPI.createTask(user, "Something", "Other", Priority.NORMAL, 10, LocalDateTime.now().plusDays(2));
            taskAPI.setTaskRole(user, task, TaskRole.WORKER);
            Group group = groupAPI.createGroup("testgroup", user, "nothing");


            User user2 = userAPI.createUser("abcbbb", "heya", "dummy user", null);
            User user3 = userAPI.createUser("nope", "heya", "dummy user", null);
            Task task2 = taskAPI.createTask(user, "SomethingA", "Other", Priority.NORMAL, 10, LocalDateTime.now().plusDays(2));
            Task task3 = taskAPI.createTask(user, "SomethingB", "Other", Priority.NORMAL, 10, LocalDateTime.now().plusDays(2));
            Task task4 = taskAPI.createTask(user, "SomethingC", "Other", Priority.NORMAL, 10, LocalDateTime.now().plusDays(2));
            Task task5 = taskAPI.createTask(user, "SomethingD", "Other", Priority.NORMAL, 10, 1/60d);
            Task task6 = taskAPI.createTask(user, "SomethingE", "Other", Priority.NORMAL, 10, 1/120d);

            sharingAPI.offerTaskSharing(user, task2, user2);
            sharingAPI.offerTaskSharing(user, task4, user2);
            sharingAPI.offerTaskSharing(user, task3, user3);

            sharingAPI.offerMembership(user, group, user2);
            sharingAPI.offerTaskSharing(user, task3, group);

            taskAPI.setTaskRole(user, task5, TaskRole.WORKER);

            UserTaskSharingOffer offer = sharingAPI.getUserTaskSharingOffer(5L);
            System.out.println(offer);
        } catch (WrongParameterException e) {
            e.printStackTrace();
        } catch (UserAlreadyExistsException e) {
            e.printStackTrace();
        } catch (NotMemberOfException e) {
            e.printStackTrace();
        } catch (AlreadyExistsException e) {
            e.printStackTrace();
        } catch (GroupPermissionException e) {
            e.printStackTrace();
        } catch (DoesNotExistException e) {
            System.out.println("DOES NOT EXIST!");
            e.printStackTrace();
        }
    }

}
