package cz.cvut.fel.ear.hamrazec.dormitory.rest;

import cz.cvut.fel.ear.hamrazec.dormitory.exception.AlreadyExistsException;
import cz.cvut.fel.ear.hamrazec.dormitory.exception.AlreadyLoginException;
import cz.cvut.fel.ear.hamrazec.dormitory.model.User;
import cz.cvut.fel.ear.hamrazec.dormitory.security.DefaultAuthenticationProvider;
import cz.cvut.fel.ear.hamrazec.dormitory.service.security.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

@RestController
//@RequestMapping("/")
public class LoginController {

    private LoginService service;

    @Autowired
    public LoginController(LoginService service) {
         this.service = service;
    }

    @PostMapping(value = "/login",produces = MediaType.APPLICATION_JSON_VALUE)
    public void loginStudent(@RequestParam String username, @RequestParam String password) throws AlreadyLoginException {
        service.loginStudent(username,password);
    }

    @PostMapping(value = "/login_manager",produces = MediaType.APPLICATION_JSON_VALUE)
    public void loginManager(@RequestParam String username, @RequestParam String password) throws AlreadyLoginException {
        service.loginManager(username,password);
    }
}
