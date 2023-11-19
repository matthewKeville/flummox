/*
package com.keville.ReBoggled.session;

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

@Component
public class CustomSessionListener implements HttpSessionListener {

    private static final Logger LOG= LoggerFactory.getLogger(CustomSessionListener.class);

    private final AtomicInteger counter = new AtomicInteger();

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        LOG.info("New session is created. Adding Session to the counter.");
        se.getSession().setAttribute("sessionType","unknown");
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        LOG.info("Session destroyed. Removing the Session from the counter.");
    }

}
*/
