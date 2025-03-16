package com.keville.flummox.security;

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
import com.keville.flummox.session.SessionAuthenticationMap;

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

    public static final String KEY_INDENTITY = "NOTSURE"; //not sure what this is

    public GuestUserAnonymousAuthenticationFilter() {
        super(KEY_INDENTITY);
    }

    // The procedure to create session guests, requires that every request is bound
    // to a session. Thus, in the security config we assume the SessionManagementCustomizer
    // has set the SessionCreationPolicy to ALWAYS, otherwise the anonymous filter
    // won't have a session to bind the guest to, and it has been determined that
    // we can't create sessions in this filter as req.getSession(true) throws
    // an error on session creation because the response has already been submitted
    //
    // i.e. Session is created first, then Authentication process

    @Override
    protected Authentication createAuthentication(HttpServletRequest req) {

 
        HttpSession session = req.getSession(false);
        User guest;
        AnonymousAuthenticationToken token;
        
        if ( session == null ) {
          throw new RuntimeException("GuestUserAnonymousAuthenticationFilter assumes SessionCreationPolicy is ALWAYS");
        } 

        if ( SessionAuthenticationMap.hasSession(session) ) {

          Integer guestId = SessionAuthenticationMap.GetSessionUserId(session);
          Optional<User> optUser = users.findById(guestId);

          if ( optUser.isEmpty() ) {
            LOG.warn("critical error, session exists in UserSession map, but it's userId does not map to a real User database entry");
            return null;
          }
          guest = optUser.get();

        } else {

          guest = guestCreator.createGuest();
          guest = users.save(guest);

          SessionAuthenticationMap.addUserSession(guest.id,session);

        }

        return new AnonymousAuthenticationToken(KEY_INDENTITY, guest, AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));

    }

}
