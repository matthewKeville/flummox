package com.keville.ReBoggled.security;

import java.io.IOException;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.keville.ReBoggled.model.User;
import com.keville.ReBoggled.repository.UserRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/* 
 * SavedRequestAwareAuthenticationSuccessHandler is a spring security built-in
 * that returns the user to the requested resource before authorization
 */

@Component
public class AuthenticationSuccessHandlerImpl extends SavedRequestAwareAuthenticationSuccessHandler {

    @Autowired
    private HttpSession session;

    @Autowired
    private UserRepository users;
    
    private static final Logger LOG= LoggerFactory.getLogger(AuthenticationSuccessHandlerImpl.class);

    public void onAuthenticationSuccess(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication
      ) throws IOException, ServletException {

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        LOG.info("Successfully authenticated : " + userDetails.getUsername());

        //Get matching (User)
        Iterator<User> iterator = users.findAll().iterator();
        while ( iterator.hasNext() ) {

          User user = iterator.next();
          if ( user.getEmail().equals(userDetails.getUsername())) {
            session.setAttribute("userId",user.id);
            LOG.info("Authenticated User Session started for \n" + user);
            break;
          } else if ( !iterator.hasNext() ){
            LOG.error("Could not find User data for authenticated user " + userDetails.getUsername() );
          }

        }

        super.onAuthenticationSuccess(request, response, authentication);

    }
}
