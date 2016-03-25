package com.melkamar.deadlines;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class DeadlinesApplication {

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(DeadlinesApplication.class, args);

        for (String beanname : ctx.getBeanDefinitionNames()) {
            System.out.println("BEAN: " + beanname);

        }
    }
}
