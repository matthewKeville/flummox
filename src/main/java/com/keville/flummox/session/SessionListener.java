package com.keville.flummox.session;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.stereotype.Component;

import com.keville.flummox.model.lobby.Lobby;
import com.keville.flummox.model.lobby.LobbyMessage;
import com.keville.flummox.model.lobby.LobbyUserReference;
import com.keville.flummox.model.user.User;
import com.keville.flummox.repository.LobbyMessageRepository;
import com.keville.flummox.repository.LobbyRepository;
import com.keville.flummox.repository.UserRepository;
import com.keville.flummox.service.utils.ServiceUtils;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

@Component
public class SessionListener implements HttpSessionListener {

  private Logger LOG = LoggerFactory.getLogger(SessionListener.class);
  private UserRepository users;
  private LobbyRepository lobbies;
  private LobbyMessageRepository lobbyMessages;
  private final long inactiveToleranceInSeconds = (1000) * 60; 
  private final long delay = (1000) * 60; 

  public SessionListener(@Autowired UserRepository users,
        @Autowired LobbyRepository lobbies,
        @Autowired LobbyMessageRepository lobbyMessages
        ) {
      this.users = users;
      this.lobbies = lobbies;
      this.lobbyMessages = lobbyMessages;
  }


  public void sessionDestroyed(HttpSessionEvent event) {
    HttpSession session = event.getSession();
    Integer userId = SessionAuthenticationMap.GetSessionUserId(session);
    LOG.info("session destroyed : user " + userId + " sessionID " + session.getId());
    handleInactiveUser(ServiceUtils.findUserById(users, userId));
  }


  private void handleInactiveUser(User user) {

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

    //stop tracking session auth
    SessionAuthenticationMap.removeUserSession(user.id);

    if ( user.guest ) {
      deleteGuest(user);
    }
    users.save(user);

  }

  private void deleteGuest(User user) {
    //"soft" delete guest
    user.deactivated = true;
  }


}
