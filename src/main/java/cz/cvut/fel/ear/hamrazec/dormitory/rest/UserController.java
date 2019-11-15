package cz.cvut.fel.ear.hamrazec.dormitory.rest;

import cz.cvut.fel.ear.hamrazec.dormitory.dao.UserDao;
import cz.cvut.fel.ear.hamrazec.dormitory.model.UserRole;
import cz.cvut.fel.ear.hamrazec.dormitory.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class UserController {

    private UserDao userDao;

    @Autowired
    public UserController(UserDao userDao) {
        this.userDao = userDao;
    }

    @GetMapping(value = "/user")
    public UserRole getUser(){
        User user = userDao.find(Long.parseLong("2"));
        UserRole userRole = user.getUserRole();
        userRole.

    }

}
