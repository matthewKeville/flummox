package com.keville.ReBoggled.security;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.stereotype.Component;

import com.keville.ReBoggled.model.user.GuestCreator;
import com.keville.ReBoggled.model.user.User;
import com.keville.ReBoggled.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;

/* 
 * AnonymousAuthenticationFilter runs whenever a request is made failing to authenticate
 * in the proper way ( a registered user account ). I'm using this to to tie an authenticated
 * session to a 'guest' user. As this filter is run after every (non authenticated) request,
 * the Authentication object gets destroyed and made new every time. To circumvent this,
 * we can track the session that spawned the guest user and recreate the Authentication
 * by hand (every request)
 */

@Component
public class GuestUserAnonymousAuthenticationFilter extends AnonymousAuthenticationFilter {

    private static final Logger LOG = LoggerFactory.getLogger(GuestUserAnonymousAuthenticationFilter.class);

    @Autowired
    private GuestCreator guestCreator;
    @Autowired
    private UserRepository users;

    //FIXME : This implementation doesn't remove expired session entries in the map
    //this is wrong...
    private Map<String,Integer> sessionGuests = new HashMap<String,Integer>();

    //this is some security thing that i'm not well versed in.
    public static final String KEY_INDENTITY = "NOTSURE";

    public GuestUserAnonymousAuthenticationFilter() {
        super(KEY_INDENTITY);
    }

    @Override
    protected Authentication createAuthentication(HttpServletRequest req) {

        String sessionId = req.getSession().getId();
        User guest;

        if ( sessionGuests.containsKey(sessionId) ) {
          int guestId = sessionGuests.get(sessionId);
          Optional<User> optUser = users.findById(guestId);

          if (optUser.isEmpty()) {
            LOG.warn(" guest user id " + guestId + " in sessionGuests map, but doesn't exist in the database");
            return null;
          }

          guest = optUser.get();

        } else {

          guest = guestCreator.createGuest();
          guest = users.save(guest);
          sessionGuests.put(sessionId,guest.id);

          LOG.info("new session guest");
          LOG.info("session id " + sessionId);
          LOG.info("guest user id : " + guest.username);

        }

        req.getSession().setAttribute("userId",guest.id);
        AnonymousAuthenticationToken token = new AnonymousAuthenticationToken(KEY_INDENTITY, guest, AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));
        return token;

    }
}
