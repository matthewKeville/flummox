package com.keville.flummox.security;

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

import com.keville.flummox.model.user.GuestCreator;
import com.keville.flummox.model.user.User;
import com.keville.flummox.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

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
    public static Map<String,Integer> SessionGuests = new HashMap<String,Integer>();

    //this is some security thing that i'm not well versed in.
    public static final String KEY_INDENTITY = "NOTSURE";

    public GuestUserAnonymousAuthenticationFilter() {
        super(KEY_INDENTITY);
    }

    @Override
    protected Authentication createAuthentication(HttpServletRequest req) {

        // The procedure to create session guests, requires that every request is bound
        // to a session. Thus, in the security config we assume the SessionManagementCustomizer
        // has set the SessionCreationPolicy to ALWAYS, otherwise the anonymous filter
        // won't have a session to bind the guest to, and it has been determined that
        // we can't create sessions in this filter as req.getSession(true) throws
        // an error on session creation because the response has already been submitted
 
        HttpSession session = req.getSession(false);
        User guest;
        AnonymousAuthenticationToken token;
        
        if ( session == null ) {
          throw new RuntimeException("GuestUserAnonymousAuthenticationFilter assumes SessionCreationPolicy is ALWAYS");
        } 

        if ( SessionGuests.containsKey(session.getId()) ) {
          int guestId = SessionGuests.get(session.getId());
          Optional<User> optUser = users.findById(guestId);

          if (optUser.isEmpty()) {
            LOG.warn(" guest user id " + guestId + " in sessionGuests map, but doesn't exist in the database");
            return null;
          }

          guest = optUser.get();

        } else {

          guest = guestCreator.createGuest();
          guest = users.save(guest);
          SessionGuests.put(session.getId(),guest.id);

          LOG.info("new session guest");
          LOG.info("session id " + session.getId());
          LOG.info("guest user id : " + guest.username);

        }

        return new AnonymousAuthenticationToken(KEY_INDENTITY, guest, AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));

    }

}
