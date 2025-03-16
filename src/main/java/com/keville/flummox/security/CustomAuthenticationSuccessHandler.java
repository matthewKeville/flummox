package com.keville.flummox.security;

import java.io.IOException;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.keville.flummox.model.user.User;
import com.keville.flummox.repository.UserRepository;
import com.keville.flummox.session.SessionAuthenticationMap;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

//Seems to be a seperate process than the Guest Anonymous Authentication sucess handler
//Code dupe for SessionAuthenticationMap
@Component
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final Logger LOG = LoggerFactory.getLogger(CustomAuthenticationSuccessHandler.class);

    @Autowired
    private UserRepository users;

    public CustomAuthenticationSuccessHandler() {
        super();
        setRedirectStrategy(new NoRedirectStrategy());
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        super.onAuthenticationSuccess(request, response, authentication);
        User user = (User) authentication.getPrincipal();

        HttpSession session = request.getSession();
        SessionAuthenticationMap.addUserSession(user.id,session);
    }

    protected class NoRedirectStrategy implements RedirectStrategy {

        @Override
        public void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url)
                throws IOException {
        }

    }
}
