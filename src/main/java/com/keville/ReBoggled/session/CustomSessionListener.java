package com.keville.ReBoggled.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

@Component
public class CustomSessionListener implements HttpSessionListener {

    private static final Logger LOG = LoggerFactory.getLogger(CustomSessionListener.class);

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        LOG.info("Session Created ");
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        LOG.info("Session ended ");
    }

}
