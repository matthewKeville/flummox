package com.keville.ReBoggled.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.stereotype.Component;

import com.keville.ReBoggled.model.Lobby;
import com.keville.ReBoggled.model.LobbyUserReference;
import com.keville.ReBoggled.model.User;
import com.keville.ReBoggled.repository.LobbyRepository;
import com.keville.ReBoggled.repository.UserRepository;

import java.util.Optional;

@Component
public class LobbyService {

    private static final Logger LOG = LoggerFactory.getLogger(LobbyService.class);

    private LobbyRepository lobbies;
    private UserRepository users;

    public LobbyService(@Autowired LobbyRepository lobbies,
        @Autowired UserRepository users) {
      this.lobbies = lobbies;
      this.users = users;
    }

    public Iterable<Lobby> getLobbies() {
      return lobbies.findAll();
    }

    public Optional<Lobby> getLobby(int id) {
      return lobbies.findById(id);
    }

    public void addLobby(Lobby lobby) {
      lobbies.save(lobby);
    }

    public void addUserToLobby(Integer userId,Integer lobbyId) {

      Optional<Lobby> optLobby = lobbies.findById(lobbyId);
      if ( !optLobby.isPresent() ) {
        LOG.error(String.format("Error adding userId: %d to lobbyId: %d, because lobby does not exist",userId,lobbyId));
        return;
      }
      Lobby lobby = optLobby.get();

      Optional<User> optUser = users.findById(userId);
      if ( !optUser.isPresent() ) {
        LOG.error(String.format("Error adding userId: %d to lobbyId: %d, because user does not exist",userId,lobbyId));
      }
      User user = optUser.get();

      if ( user.lobby != null ) {
        removeUserFromLobby(user,lobbyId);
      }

      // add user to new lobby
      LobbyUserReference userRef = new LobbyUserReference(AggregateReference.to(userId));
      lobby.users.add(userRef);
      lobbies.save(lobby);

      // update users's reference
      user.lobby = AggregateReference.to(lobby.id);
      users.save(user);

      LOG.info(String.format("successfully added userId: %d to lobbyId: %d",userId,lobbyId));

    }

    public void removeUserFromLobby(Integer userId,Integer lobbyId) {

      Optional<User> optUser = users.findById(userId);
      if ( !optUser.isPresent() ) {
        LOG.error(String.format("Error removing userId: %d to lobbyId: %d, because user does not exist",userId,lobbyId));
        return;
      }

      User user = optUser.get();

      if ( user.lobby == null ) {
        LOG.error(String.format("Error removing user : %d from lobby %d because user does not belong to a lobby",user.id));
        return;
      }

      removeUserFromLobby(user,lobbyId);
    }

    private void removeUserFromLobby(User user,Integer lobbyId) {

      LobbyUserReference userRef = new LobbyUserReference(AggregateReference.to(user.id));

        
      Optional<Lobby> optLobby = lobbies.findById(user.lobby.getId());
      if ( !optLobby.isPresent() ) {
        LOG.error(String.format("Error removing userId: %d to lobbyId: %d, because lobby does not exist",user.id,lobbyId));
        return;
      }

      Lobby oldLobby = optLobby.get();
      if (!oldLobby.users.remove(userRef)) {
        LOG.warn(String.format("you tried to remove user %d from lobby %d, but they don't belong to it",user.id,lobbyId));
      }
      lobbies.save(oldLobby);

    }

}
