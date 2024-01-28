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

import com.keville.ReBoggled.model.user.User;
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

        //Down the line, I really should be home brewing a AuthenticationManager implementation
        //that returns User from getPrincipal ...

        //Default AuthenticationManger will return a UserDetails as principal
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        //My implementation of UserDetails has (model) User information
        if ( !(userDetails instanceof User) ) {
          LOG.error(" expected instance UserDetails of type com.keville.ReBoggled.model.User ");
          return;
        }
        User user = (User) userDetails;
        //The current architecture requires the user.id in the session. (atleast for inauthenticated users)
        //As guest accounts map to User objects, but we since we don't have authenticatoin we can't access User
        //variables directly through the authentication context. Currently we always check this session attribute,
        //even though it's not necessary when the user is already authenticated. 
        //
        //Hmm what happens when to the AuthenticationContext when the User changes its information, during the session? 
        //Does the information become stale?
        session.setAttribute("userId",user.id);
        LOG.info("Authenticated User Session started for \n" + user.getUsername());

        // forward the user to the home page
        response.setHeader("Location", "/");
        response.setStatus(302);

        super.onAuthenticationSuccess(request, response, authentication);

    }
}
