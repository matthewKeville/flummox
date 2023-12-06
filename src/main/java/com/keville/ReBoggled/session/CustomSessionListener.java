package com.keville.ReBoggled.session;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.keville.ReBoggled.model.User;
import com.keville.ReBoggled.util.GuestCreator;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

@Component
public class CustomSessionListener implements HttpSessionListener {

    private static final Logger LOG= LoggerFactory.getLogger(CustomSessionListener.class);

    @Autowired
    private GuestCreator guestCreator;

    @Override
    public void sessionCreated(HttpSessionEvent se) {

        User guest = guestCreator.createGuest();
        se.getSession().setAttribute("userId",guest.id);
        LOG.info("started guest session \n" + guest);

    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
    }

}
