package cz.cvut.fel.ear.hamrazec.dormitory.service;

import cz.cvut.fel.ear.hamrazec.dormitory.exception.AlreadyExistsException;
import cz.cvut.fel.ear.hamrazec.dormitory.exception.AlreadyLoginException;
import cz.cvut.fel.ear.hamrazec.dormitory.security.DefaultAuthenticationProvider;
import cz.cvut.fel.ear.hamrazec.dormitory.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
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


    @Transactional(readOnly = true)
    public void loginStudent(String username, String password) throws AlreadyLoginException {

        if (SecurityUtils.getCurrentUserDetails() != null) throw new AlreadyLoginException();
        Authentication auth = new UsernamePasswordAuthenticationToken(username, password);
        provider.authenticate(auth, false);

    }

    @Transactional(readOnly = true)
    public void loginManager(String username, String password) throws AlreadyLoginException {

        if (SecurityUtils.getCurrentUserDetails() != null) throw new AlreadyLoginException();
        Authentication auth = new UsernamePasswordAuthenticationToken(username, password);
        provider.authenticate(auth, true);
    }


}
