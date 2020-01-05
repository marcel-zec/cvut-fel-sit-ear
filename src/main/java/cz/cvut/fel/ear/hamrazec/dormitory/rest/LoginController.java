package cz.cvut.fel.ear.hamrazec.dormitory.rest;

import cz.cvut.fel.ear.hamrazec.dormitory.exception.AlreadyExistsException;
import cz.cvut.fel.ear.hamrazec.dormitory.service.security.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
//@RequestMapping("/")
public class LoginController {

    private LoginService service;

    @Autowired
    public LoginController(LoginService service) {
         this.service = service;
    }

    @PostMapping(value = "/login",produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void login(@RequestParam(name = "username") String username, @RequestParam(name = "password") String password) throws AlreadyExistsException {
        service.loginStudent(username, password);
        //return new ResponseTransfer("Login sucessfull !!!");
    }
}
