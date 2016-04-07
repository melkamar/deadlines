package com.melkamar.deadlines.controllers;

import com.melkamar.deadlines.exceptions.WrongParameterException;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.task.Priority;
import com.melkamar.deadlines.model.task.Task;
import com.melkamar.deadlines.services.api.TaskAPI;
import com.melkamar.deadlines.services.api.UserAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 07.04.2016 21:09
 */
@RestController
public class UserController {


    @Autowired
    private UserAPI userAPI;
    @Autowired
    private TaskAPI taskAPI;


    @RequestMapping("/user")
    public ArrayList<User> hello(){
        try {
            ArrayList<User> users = new ArrayList<>();

            User newUser = userAPI.createUser("Sample user"+ new Random().nextInt(), "pwd", "name", "email");
            Task task = taskAPI.createTask(newUser, "NewTask", "Some description", Priority.NORMAL, 10, 10);
            User newUser2 = userAPI.createUser("ANOTHER USER"+ new Random().nextInt(), "pwd", "name", "email");

            users.add(newUser);
            users.add(newUser2);
            return users;
        } catch (WrongParameterException e) {
            e.printStackTrace();
        }

        return null;
    }
}
