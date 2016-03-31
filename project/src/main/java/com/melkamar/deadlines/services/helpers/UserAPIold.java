package com.melkamar.deadlines.services.helpers;

import org.springframework.stereotype.Service;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 26.03.2016 14:49
 */
@Service
public class UserAPIold {
//    @Autowired
//    private StringConstants stringConstants;
//    @Autowired
//    private PasswordHashGenerator passwordHashGenerator;
//    @Autowired
//    private UserDAO userDAO;
//
//    public User createUser(String username, String password, String name, String email) throws WrongParameterException {
//        if (username == null || username.isEmpty()) {
//            throw new WrongParameterException(stringConstants.EXC_PARAM_USERNAME_EMPTY);
//        }
//
//        if (password == null || password.isEmpty()){
//            throw new NullPointerException(stringConstants.EXC_PARAM_PASSWORD_EMPTY);
//        }
//
//        String[] hashsalt = passwordHashGenerator.generatePasswordHash(password);
//
//        User newUser = new User(username, hashsalt[0], hashsalt[1]);
//        newUser.setName(name);
//        newUser.setEmail(email);
//
//        userDAO.save(newUser);
//
//        return newUser;
//    }
//
//    public boolean leaveTask(User user, Task task, Group group){
//        // TODO: 29.03.2016
//        throw new NotImplementedException();
//    }
}
