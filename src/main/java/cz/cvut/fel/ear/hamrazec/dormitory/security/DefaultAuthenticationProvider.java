package cz.cvut.fel.ear.hamrazec.dormitory.security;

import cz.cvut.fel.ear.hamrazec.dormitory.model.Role;
import cz.cvut.fel.ear.hamrazec.dormitory.security.model.AuthenticationToken;
import cz.cvut.fel.ear.hamrazec.dormitory.security.model.UserDetails;
import cz.cvut.fel.ear.hamrazec.dormitory.service.security.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class DefaultAuthenticationProvider implements AuthenticationProvider {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultAuthenticationProvider.class);

    private final UserDetailsService userDetailsService;

    private final PasswordEncoder passwordEncoder;

    private final RoleService roleService;


    @Autowired
    public DefaultAuthenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder, RoleService roleService) {

        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
    }


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UserDetails ud = (UserDetails) userDetailsService.loadUserByUsername(authentication.getPrincipal().toString());
        if (!passwordEncoder.matches(authentication.getCredentials().toString(), ud.getPassword())) {
            throw new BadCredentialsException("Not validated");
        }
        ud.eraseCredentials();
        return SecurityUtils.setCurrentUser(ud);
    }

    /**
     * Used for two type of user verification. It is overloaded of regular authenticate method. Accept boolean parameter
     * for choosing type of user for different verification.
     * @param authentication
     * @param manager is true for manager and superuser, false for students
     * @return
     * @throws AuthenticationException
     */
    public Authentication authenticate(Authentication authentication, boolean manager) throws AuthenticationException {
        UserDetails ud = (UserDetails) userDetailsService.loadUserByUsername(authentication.getPrincipal().toString());
        if (manager){
            if (roleService.isStudent(ud.getUser())) throw new BadCredentialsException("Not validated");
        } else {
            if (roleService.isManager(ud.getUser())) throw new BadCredentialsException("Not validated");
        }
        if (!passwordEncoder.matches(authentication.getCredentials().toString(), ud.getPassword()))throw new BadCredentialsException("Not validated");
        System.out.println("Login success.");
        ud.eraseCredentials();
        System.out.println("Erase success.");
        return SecurityUtils.setCurrentUser(ud);
    }



    @Override
    public boolean supports(Class<?> aClass) {

        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(aClass) ||
                AuthenticationToken.class.isAssignableFrom(aClass);
    }
}
