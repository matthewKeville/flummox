package com.keville.ReBoggled.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.stereotype.Component;

import com.keville.ReBoggled.DTO.UpdateLobbyDTO;
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

    public Integer getLobbyOwnerId(int id) {
      Optional<Lobby> optLobby = lobbies.findById(id);
      if ( optLobby.isEmpty() ) {
        return null;
      }
      return optLobby.get().owner.getId();
    }

    public void addLobby(Lobby lobby) {
      lobbies.save(lobby);
    }

    public AddUserToLobbyResponse addUserToLobby(Integer userId,Integer lobbyId) {

      Optional<Lobby> optLobby = lobbies.findById(lobbyId);
      if ( !optLobby.isPresent() ) {
        LOG.error(String.format("Can't add user : %d to lobby : %d, because lobby does not exist",userId,lobbyId));
        return AddUserToLobbyResponse.ERROR;
      }
      Lobby lobby = optLobby.get();

      Optional<User> optUser = users.findById(userId);
      if ( !optUser.isPresent() ) {
        LOG.error(String.format("Can't add user : %d to lobby : %d, because user does not exist",userId,lobbyId));
        return AddUserToLobbyResponse.ERROR;
      }
      User user = optUser.get();

      if( lobby.isPrivate && !lobby.owner.getId().equals(userId) ) {
        LOG.warn(String.format("Can't add user : %d to lobby : %d, because it's private",userId,lobbyId));
        return AddUserToLobbyResponse.LOBBY_PRIVATE;
      }

      if( lobby.users.size() == lobby.capacity ) {
        LOG.warn(String.format("can't add user : %d to lobby : %d, because it's at capacity",userId,lobbyId));
        return AddUserToLobbyResponse.LOBBY_FULL;
      }

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

      LOG.info(String.format("added user : %d to lobby : %d",userId,lobbyId));
      return AddUserToLobbyResponse.SUCCESS;

    }

    public RemoveUserFromLobbyResponse removeUserFromLobby(Integer userId,Integer lobbyId) {

      Optional<User> optUser = users.findById(userId);
      if ( !optUser.isPresent() ) {
        LOG.error(String.format("Can't remove user : %d from lobby : %d because user does not exist",userId,lobbyId));
        return RemoveUserFromLobbyResponse.ERROR;
      }

      User user = optUser.get();

      if ( user.lobby == null ) {
        LOG.error(String.format("Can't remove user : %d from lobby %d because user does not belong to a lobby",user.id));
        return RemoveUserFromLobbyResponse.ERROR;
      }

      return removeUserFromLobby(user,lobbyId);
    }

    private RemoveUserFromLobbyResponse removeUserFromLobby(User user,Integer lobbyId) {

      LobbyUserReference userRef = new LobbyUserReference(AggregateReference.to(user.id));
        
      Optional<Lobby> optLobby = lobbies.findById(user.lobby.getId());
      if ( !optLobby.isPresent() ) {
        LOG.error(String.format("Can't remove user : %d from lobby : %d because lobby does not exist",user.id,lobbyId));
        return RemoveUserFromLobbyResponse.ERROR;
      }

      Lobby oldLobby = optLobby.get();
      if (!oldLobby.users.remove(userRef)) {
        LOG.warn(String.format("Can't remove user %d from lobby %d because they don't belong to it",user.id,lobbyId));
        return RemoveUserFromLobbyResponse.USER_NOT_IN_LOBBY;
      }

      oldLobby.users.remove(userRef);
      lobbies.save(oldLobby);

      return RemoveUserFromLobbyResponse.SUCCESS;

    }

    public UpdateLobbyResponse update(Integer lobbyId,UpdateLobbyDTO dto) {

      Optional<Lobby>  optLobby = lobbies.findById(lobbyId);
      if ( optLobby.isEmpty() ) {
        LOG.warn(String.format("Can't find lobby %d to update",lobbyId));
        return UpdateLobbyResponse.ERROR;
      }

      Lobby lobby = optLobby.get();
     
      lobby.name = dto.name;
      lobby.isPrivate = dto.isPrivate;
      lobby.gameSettings = dto.gameSettings;

      if ( lobby.users.size() <= dto.capacity ) {

        lobby.capacity = dto.capacity;

      } else {

        LOG.warn(String.format("ignoring request to diminish lobby : %d's capacity because it's current users wont' fit",lobbyId));
        return UpdateLobbyResponse.CAPACITY_SHORTENING;
      }

      lobbies.save(lobby);
      return UpdateLobbyResponse.SUCCESS;
      
    }

    public enum AddUserToLobbyResponse {
      SUCCESS,
      ERROR,              //internal
      LOBBY_FULL,
      LOBBY_PRIVATE,
      GUEST_NOT_IMPLEMENT
    }

    public enum RemoveUserFromLobbyResponse {
      SUCCESS,
      ERROR,              //internal
      USER_NOT_IN_LOBBY
    }

    public enum UpdateLobbyResponse {
      SUCCESS,
      ERROR,              //internal
      CAPACITY_SHORTENING,
    }

}
