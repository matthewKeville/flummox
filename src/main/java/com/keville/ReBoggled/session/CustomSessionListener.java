package com.keville.ReBoggled.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.keville.ReBoggled.model.user.GuestCreator;
import com.keville.ReBoggled.model.user.User;
import com.keville.ReBoggled.repository.UserRepository;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

@Component
public class CustomSessionListener implements HttpSessionListener {

    private static final Logger LOG = LoggerFactory.getLogger(CustomSessionListener.class);

    @Autowired
    private GuestCreator guestCreator;
    @Autowired
    private UserRepository users;

    public CustomSessionListener() {
        LOG.info("session listener created");
    }

    @Override
    public void sessionCreated(HttpSessionEvent se) {

        User guest = guestCreator.createGuest();
        guest = users.save(guest);
        se.getSession().setAttribute("userId",guest.id);
        LOG.info("started guest session \n");

    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        LOG.info("Session ended ");
    }

}
