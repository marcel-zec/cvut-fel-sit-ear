package cz.cvut.fel.ear.hamrazec.dormitory.security;

import cz.cvut.fel.ear.hamrazec.dormitory.model.Role;
import cz.cvut.fel.ear.hamrazec.dormitory.security.model.AuthenticationToken;
import cz.cvut.fel.ear.hamrazec.dormitory.security.model.UserDetails;
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


    @Autowired
    public DefaultAuthenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {

        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
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

    public Authentication authenticate(Authentication authentication, boolean manager) throws AuthenticationException {
        if (manager){
            UserDetails ud = (UserDetails) userDetailsService.loadUserByUsername(authentication.getPrincipal().toString());
            if (ud.getUser().getRole().equals(Role.STUDENT) || !passwordEncoder.matches(authentication.getCredentials().toString(), ud.getPassword())) {
                throw new BadCredentialsException("Not validated");
            }
            ud.eraseCredentials();
            return SecurityUtils.setCurrentUser(ud);
        } else {
            return authenticate(authentication);
        }
    }


    @Override
    public boolean supports(Class<?> aClass) {

        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(aClass) ||
                AuthenticationToken.class.isAssignableFrom(aClass);
    }
}
