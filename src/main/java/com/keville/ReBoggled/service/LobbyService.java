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

    public Lobby addUserToLobby(Integer userId,Integer lobbyId) throws LobbyServiceException {

      Optional<Lobby> optLobby = lobbies.findById(lobbyId);
      if ( !optLobby.isPresent() ) {
        LOG.error(String.format("Can't add user : %d to lobby : %d, because lobby does not exist",userId,lobbyId));
        throw new LobbyServiceException(LobbyServiceError.ERROR);
      }
      Lobby lobby = optLobby.get();

      Optional<User> optUser = users.findById(userId);
      if ( !optUser.isPresent() ) {
        LOG.error(String.format("Can't add user : %d to lobby : %d, because user does not exist",userId,lobbyId));
        throw new LobbyServiceException(LobbyServiceError.ERROR);
      }
      User user = optUser.get();


      //can user join this lobby?

      if( lobby.isPrivate && !lobby.owner.getId().equals(userId) ) {
        LOG.warn(String.format("Can't add user : %d to lobby : %d, because it's private",userId,lobbyId));
        throw new LobbyServiceException(LobbyServiceError.LOBBY_PRIVATE);
      }

      if( lobby.users.size() == lobby.capacity ) {
        LOG.warn(String.format("can't add user : %d to lobby : %d, because it's at capacity",userId,lobbyId));
        throw new LobbyServiceException(LobbyServiceError.LOBBY_FULL);
      }

      // remove the user from there previous lobby
      Optional<Lobby> optUserLobby = lobbies.findUserLobby(userId);
      if ( optUserLobby.isPresent()) {
        removeUserFromLobby(user,optUserLobby.get()); /* previous */
      }

      // add user to new lobby
      LobbyUserReference userRef = new LobbyUserReference(AggregateReference.to(userId));
      lobby.users.add(userRef);
      lobby = lobbies.save(lobby);

      LOG.info(String.format("added user : %d to lobby : %d",userId,lobbyId));

      return lobby;

    }

    public Lobby removeUserFromLobby(Integer userId,Integer lobbyId) throws LobbyServiceException {

      Optional<User> optUser = users.findById(userId);
      if ( !optUser.isPresent() ) {
        LOG.error(String.format("Can't remove user : %d from lobby : %d because user does not exist",userId,lobbyId));
        throw new LobbyServiceException(LobbyServiceError.USER_NOT_FOUND);
      }

      Optional<Lobby> optLobby = lobbies.findById(lobbyId);
      if ( !optLobby.isPresent() ) {
        LOG.error(String.format("Can't remove user : %d from lobby : %d because lobby does not exist",userId,lobbyId));
        throw new LobbyServiceException(LobbyServiceError.ERROR);
      }

      Lobby lobby = removeUserFromLobby(optUser.get(),optLobby.get());
      return lobby;
    }

    private Lobby removeUserFromLobby(User user,Lobby lobby) throws LobbyServiceException {

      LobbyUserReference userRef = new LobbyUserReference(AggregateReference.to(user.id));
        
      if (!lobby.users.contains(userRef)) {
        LOG.warn(String.format("Can't remove user %d from lobby %d because they don't belong to it",user.id,lobby.id));
        throw new LobbyServiceException(LobbyServiceError.USER_NOT_IN_LOBBY);
      }

      lobby.users.remove(userRef);
      lobby = lobbies.save(lobby);

      return lobby;

    }

    public Lobby transferLobbyOwnership(Integer lobbyId,Integer ownerId, Integer userId) throws LobbyServiceException {

      Optional<Lobby> optLobby = lobbies.findById(lobbyId);
      if ( !optLobby.isPresent() ) {
        LOG.error(String.format("lobby %d not found",lobbyId));
        throw new LobbyServiceException(LobbyServiceError.LOBBY_NOT_FOUND);
      }
      Lobby lobby = optLobby.get();

      Optional<User> optOwner = users.findById(ownerId);
      if ( !optOwner.isPresent() ) {
        LOG.error(String.format("user (owner) %d not found",ownerId));
        throw new LobbyServiceException(LobbyServiceError.USER_NOT_FOUND);
      }
      User owner = optOwner.get();

      Optional<User> optUser = users.findById(userId);
      if ( !optUser.isPresent() ) {
        LOG.error(String.format("user %d not found",userId));
        throw new LobbyServiceException(LobbyServiceError.USER_NOT_FOUND);
      }
      User user = optUser.get();

      lobby.owner = AggregateReference.to(userId);
      lobby = lobbies.save(lobby);

      LOG.info(String.format("transfered lobby %d ownership from %d to %d",lobbyId,ownerId,userId));

      return lobby;

    }

    public Lobby update(Integer lobbyId,UpdateLobbyDTO dto) throws LobbyServiceException {

      Optional<Lobby>  optLobby = lobbies.findById(lobbyId);
      if ( optLobby.isEmpty() ) {
        LOG.warn(String.format("Can't find lobby %d to update",lobbyId));
        //return UpdateLobbyResponse.ERROR;
        throw new LobbyServiceException(LobbyServiceError.ERROR);
      }

      Lobby lobby = optLobby.get();
     
      lobby.name = dto.name;
      lobby.isPrivate = dto.isPrivate;
      lobby.gameSettings = dto.gameSettings;

      if ( lobby.users.size() <= dto.capacity ) {
        lobby.capacity = dto.capacity;
      } else {
        LOG.warn(String.format("ignoring request to diminish lobby : %d's capacity because it's current users wont' fit",lobbyId));
        throw new LobbyServiceException(LobbyServiceError.CAPACITY_SHORTENING);
      }

      lobby = lobbies.save(lobby);
      return lobby;
      
    }

    //delete the lobby and remove all user references to it
    public Boolean delete(Integer lobbyId) throws LobbyServiceException {

      Optional<Lobby>  optLobby = lobbies.findById(lobbyId);
      if ( optLobby.isEmpty() ) {
        LOG.warn(String.format("Can't find lobby %d to delete",lobbyId));
        throw new LobbyServiceException(LobbyServiceError.LOBBY_NOT_FOUND);
      }
      Lobby lobby = optLobby.get();

      lobbies.delete(lobby);

      return true;
      
    }

    public Lobby createNew(Integer userId) throws LobbyServiceException {

      if (!users.existsById(userId)) {
        LOG.warn(String.format("Can't create new lobby for user %d because they don't exist",userId));
        throw new LobbyServiceException(LobbyServiceError.USER_NOT_FOUND);
      }
      User user = (users.findById(userId)).get();

      Optional<Lobby> optOwnedLobby = lobbies.findOwnedLobby(userId);
      if ( optOwnedLobby.isPresent() ) {
        LOG.warn(String.format("User %d is trying to create a new lobby, but they already have one  %d",userId,optOwnedLobby.get().id));
        throw new LobbyServiceException(LobbyServiceError.LOBBY_ALREADY_OWNED);
      }

      //Is user in another lobby?
      Optional<Lobby> optUserLobby = lobbies.findOwnedLobby(userId);
      if ( optUserLobby.isPresent() ) {
        removeUserFromLobby(userId,optUserLobby.get().id);
      }


      //Add User To Lobby

      Lobby lobby = lobbies.save(new Lobby(user.username+"\'s lobby",6,false,AggregateReference.to(userId)));
      LobbyUserReference userRef = new LobbyUserReference(AggregateReference.to(userId));
      lobby.users.add(userRef);

      //Set Lobby's Owner
      
      lobby.owner = AggregateReference.to(user.id);
      lobbies.save(lobby);

      return lobby;
      
    }

    public enum LobbyServiceError {
      SUCCESS,
      ERROR,              //internal
      LOBBY_NOT_FOUND,
      USER_NOT_FOUND,
      LOBBY_FULL,
      LOBBY_PRIVATE,
      LOBBY_ALREADY_OWNED,
      GUEST_NOT_IMPLEMENT,
      USER_NOT_IN_LOBBY,
      CAPACITY_SHORTENING
    }

    public class LobbyServiceException extends Exception {

      public LobbyServiceError error;

      public LobbyServiceException(LobbyServiceError error) {
        this.error = error;
      }

      @Override
      public String getMessage() {
        return error.toString();
      }

    }



}
