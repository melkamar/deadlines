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

package com.melkamar.deadlines;

import com.melkamar.deadlines.exceptions.*;
import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.offer.UserTaskSharingOffer;
import com.melkamar.deadlines.model.task.Priority;
import com.melkamar.deadlines.model.task.Task;
import com.melkamar.deadlines.model.task.TaskRole;
import com.melkamar.deadlines.services.api.GroupApi;
import com.melkamar.deadlines.services.api.SharingApi;
import com.melkamar.deadlines.services.api.TaskApi;
import com.melkamar.deadlines.services.api.UserApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.Properties;

@SpringBootApplication
public class DeadlinesApplication extends SpringBootServletInitializer {
    @Autowired
    private TaskApi taskApi;
    @Autowired
    private SharingApi sharingApi;
    @Autowired
    private GroupApi groupApi;

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(DeadlinesApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(DeadlinesApplication.class).properties(getProperties());
    }

    static Properties getProperties() {
        Properties props = new Properties();
        props.put("spring.config.location", "classpath:deadlines/");
        return props;
    }

    @Autowired
    private UserApi userApi;

    @PostConstruct
    public void doStuff() {
        boolean fillSampleData = false;
//        boolean fillSampleData = true;


        if (!fillSampleData) return;
        try {
            User user = userApi.createUser("abc", "heya", "dummy user", null);
            Task task = taskApi.createTask(user, "Something", "Other", Priority.NORMAL, 10, LocalDateTime.now().plusDays(2));
            taskApi.setTaskRole(user, task, TaskRole.WORKER);
            Group group = groupApi.createGroup("testgroup", user, "nothing");


            User user2 = userApi.createUser("abcbbb", "heya", "dummy user", null);
            User user3 = userApi.createUser("nope", "heya", "dummy user", null);
            Task task2 = taskApi.createTask(user, "SomethingA", "Other", Priority.NORMAL, 10, LocalDateTime.now().plusDays(2));
            Task task3 = taskApi.createTask(user, "SomethingB", "Other", Priority.NORMAL, 10, LocalDateTime.now().plusDays(2));
            Task task4 = taskApi.createTask(user, "SomethingC", "Other", Priority.NORMAL, 10, LocalDateTime.now().plusDays(2));
            Task task5 = taskApi.createTask(user, "SomethingD", "Other", Priority.NORMAL, 10, 1/60d);
            Task task6 = taskApi.createTask(user, "SomethingE", "Other", Priority.NORMAL, 10, 1/120d);

            sharingApi.offerTaskSharing(user, task2, user2);
            sharingApi.offerTaskSharing(user, task4, user2);
            sharingApi.offerTaskSharing(user, task3, user3);

            sharingApi.offerMembership(user, group, user2);
            sharingApi.offerTaskSharing(user, task3, group);

            taskApi.setTaskRole(user, task5, TaskRole.WORKER);

            UserTaskSharingOffer offer = sharingApi.getUserTaskSharingOffer(5L);
            System.out.println(offer);
        } catch (WrongParameterException e) {
            e.printStackTrace();
        } catch (AlreadyExistsException e) {
            e.printStackTrace();
        } catch (NotMemberOfException e) {
            e.printStackTrace();
        } catch (GroupPermissionException e) {
            e.printStackTrace();
        } catch (DoesNotExistException e) {
            System.out.println("DOES NOT EXIST!");
            e.printStackTrace();
        }
    }

}
