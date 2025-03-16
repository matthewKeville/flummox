/*
package com.keville.flummox.controllers.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.keville.flummox.repository.LobbyMessageRepository;
import com.keville.flummox.repository.LobbyRepository;
import com.keville.flummox.repository.UserRepository;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

@Component
public class SessionListener implements HttpSessionListener {


  private static final Logger LOG = LoggerFactory.getLogger(SessionListener.class);
  private UserRepository users;
  private LobbyRepository lobbies;
  private LobbyMessageRepository lobbyMessages;

  public SessionListener(@Autowired UserRepository users,
      @Autowired LobbyRepository lobbies,@Autowired LobbyMessageRepository lobbyMessages) {
      this.users = users;
      this.lobbies = lobbies;
      this.lobbyMessages = lobbyMessages;
  }


  @Override
  public void sessionCreated(HttpSessionEvent event) {
      System.out.println("session created");
      event.getSession().setMaxInactiveInterval(15);
  }

  //when a session is destroyed, if the session was a guest session, remove
  //the guest from any non-owned lobbies and promote other members in there own
  //lobby, or delete the lobby if no other members are active
  
  @Override
  public void sessionDestroyed(HttpSessionEvent event) {
      LOG.info("session destroyed");
      if (GuestUserAnonymousAuthenticationFilter.SessionGuests.containsKey(event.getSession().getId())) {
        Integer userId = GuestUserAnonymousAuthenticationFilter.SessionGuests.get(event.getSession().getId());
        Optional<User> optUser = users.findById(userId);
        if ( optUser.isEmpty() ) {
          LOG.warn(" broken : guest session found in SessionGuests, but doesn't map to a real User entry ");
        } else {

          User user = optUser.get();
          Optional<Lobby> optOwnedLobby = lobbies.findOwnedLobby(user.id);
          Optional<Lobby> optCurrentLobby = lobbies.findUserLobby(user.id);

          //promote or delete owned lobby
          if ( optOwnedLobby.isPresent() ) {
            //other users in lobby? promote
            Lobby lobby = optOwnedLobby.get();

            LobbyUserReference userRef = new LobbyUserReference(AggregateReference.to(lobby.id),AggregateReference.to(user.id));
            lobby.users.remove(userRef);

            if ( lobby.users.size() != 0 ) {
              //automatic promotion
              LobbyUserReference newOwnerRef = lobby.users.stream().findAny().get();
              User newOwner = ServiceUtils.findUserById(users,newOwnerRef.user.getId());
              lobby.owner = AggregateReference.to(newOwner.id);
              lobbyMessages.save(LobbyMessage.promoteLobbyMessage(AggregateReference.to(lobby.id), newOwner.username));
              lobbies.save(lobby);
            } else {
              lobbies.delete(lobby);
            }

          //leave current (not owned) lobby
          } else if ( optCurrentLobby.isPresent() ) {
            Lobby lobby = optCurrentLobby.get();
            LobbyUserReference userRef = new LobbyUserReference(AggregateReference.to(lobby.id),AggregateReference.to(user.id));
            lobby.users.remove(userRef);
            lobbies.save(lobby);
          }

          user.deactivated = true;
          users.save(user);

        }
        LOG.info("guest session destroyed");
      }
  }

}
*/
