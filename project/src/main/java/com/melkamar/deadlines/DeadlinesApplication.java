package com.melkamar.deadlines;

import com.melkamar.deadlines.exceptions.NotMemberOfException;
import com.melkamar.deadlines.exceptions.UserAlreadyExistsException;
import com.melkamar.deadlines.exceptions.WrongParameterException;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.task.Priority;
import com.melkamar.deadlines.model.task.Task;
import com.melkamar.deadlines.model.task.TaskRole;
import com.melkamar.deadlines.services.api.TaskAPI;
import com.melkamar.deadlines.services.api.UserAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;

@SpringBootApplication
public class DeadlinesApplication {
    @Autowired
    private TaskAPI taskAPI;

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
        try {
            User user = userAPI.createUser("abc", "heya", "dummy user", null);
            Task task = taskAPI.createTask(user, "Something", "Other", Priority.NORMAL, 10, LocalDateTime.now().plusDays(2));
            taskAPI.setTaskRole(user, task, TaskRole.WORKER);
        } catch (WrongParameterException e) {
            e.printStackTrace();
        } catch (UserAlreadyExistsException e) {
            e.printStackTrace();
        } catch (NotMemberOfException e) {
            e.printStackTrace();
        }
    }

}
