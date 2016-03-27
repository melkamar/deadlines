package com.melkamar.deadlines;

import com.melkamar.deadlines.dao.user.UserDAO;
import com.melkamar.deadlines.exceptions.WrongParameterException;
import com.melkamar.deadlines.services.helpers.UserHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class DeadlinesApplication {

    @Autowired
    private UserDAO userDAO;
    @Autowired
    private UserHelper userHelper;


    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(DeadlinesApplication.class, args);

        for (String beanname : ctx.getBeanDefinitionNames()) {
            System.out.println("BEAN: " + beanname);

        }
    }



    @PostConstruct
    public void somemethod() throws WrongParameterException {
        userHelper.createUser("AHOJ", "psss", "name","mail");
    }
}
