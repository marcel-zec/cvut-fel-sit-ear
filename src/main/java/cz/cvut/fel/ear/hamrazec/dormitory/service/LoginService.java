package cz.cvut.fel.ear.hamrazec.dormitory.service;

import cz.cvut.fel.ear.hamrazec.dormitory.security.DefaultAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoginService {

    private DefaultAuthenticationProvider provider;

    @Autowired
    public LoginService(DefaultAuthenticationProvider provider) {
        this.provider = provider;
    }


    @Transactional
    public void login(String username, String password) {

        final Authentication auth = new UsernamePasswordAuthenticationToken(username, password);
         SecurityContextHolder.getContext();
         provider.authenticate(auth);
    }


}
