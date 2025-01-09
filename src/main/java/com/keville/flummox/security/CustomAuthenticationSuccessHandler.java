package com.keville.flummox.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    public CustomAuthenticationSuccessHandler() {
        super();
        setRedirectStrategy(new NoRedirectStrategy());
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        super.onAuthenticationSuccess(request, response, authentication);
    }

    protected class NoRedirectStrategy implements RedirectStrategy {

        @Override
        public void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url)
                throws IOException {
        }

    }
}
