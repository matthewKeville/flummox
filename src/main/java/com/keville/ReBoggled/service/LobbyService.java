package com.keville.ReBoggled.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.stereotype.Component;

import com.keville.ReBoggled.DTO.LobbyDTO;
import com.keville.ReBoggled.DTO.LobbyUserDTO;
import com.keville.ReBoggled.DTO.UpdateLobbyDTO;
import com.keville.ReBoggled.model.Lobby;
import com.keville.ReBoggled.model.LobbyUserReference;
import com.keville.ReBoggled.model.User;
import com.keville.ReBoggled.repository.LobbyRepository;
import com.keville.ReBoggled.repository.UserRepository;
import com.keville.ReBoggled.util.Conversions;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    // FIXME This is wrong, my use of the model and this DTO in inconsistent
    public List<LobbyDTO> getLobbyDTOs() throws LobbyServiceException {
      Iterable<Lobby> allLobbies = lobbies.findAll();
      List<LobbyDTO> allLobbiesList = new LinkedList<LobbyDTO>();
      for ( Lobby lobby : allLobbies ) {
        allLobbiesList.add(lobbyToLobbyDTO(lobby));
      }
      return allLobbiesList;
    }

    public Lobby getLobby(int id) throws LobbyServiceException {
      Lobby lobby = findLobbyById(id);
      return lobby;
    }

    // FIXME This is wrong, my use of the model and this DTO in inconsistent
    public LobbyDTO getLobbyDTO(int id) throws LobbyServiceException {
      Lobby lobby = findLobbyById(id);
      return lobbyToLobbyDTO(lobby);
    }

    public Integer getLobbyOwnerId(int id) throws LobbyServiceException {
      Lobby lobby = findLobbyById(id);
      return lobby.owner.getId();
    }

    public void addLobby(Lobby lobby) {
      lobbies.save(lobby);
    }

    public Lobby addUserToLobby(Integer userId,Integer lobbyId) throws LobbyServiceException {

      // find entities

      Lobby lobby = findLobbyById(lobbyId);
      User user = findUserById(userId);


      // can user join?

      if( lobby.isPrivate && !lobby.owner.getId().equals(userId) ) {
        LOG.warn(String.format("Can't add user : %d to lobby : %d, because it's private",userId,lobbyId));
        throw new LobbyServiceException(LobbyServiceError.LOBBY_PRIVATE);
      }

      if( lobby.users.size() == lobby.capacity ) {
        LOG.warn(String.format("can't add user : %d to lobby : %d, because it's at capacity",userId,lobbyId));
        throw new LobbyServiceException(LobbyServiceError.LOBBY_FULL);
      }

      // remove the user from there previous lobby (if any)

      Optional<Lobby> optUserLobby = lobbies.findUserLobby(userId);
      if ( optUserLobby.isPresent()) {
        removeUserFromLobby(user,optUserLobby.get());
      }

      // add user to new lobby

      LobbyUserReference userRef = new LobbyUserReference(AggregateReference.to(userId));
      lobby.users.add(userRef);
      lobby = lobbies.save(lobby);

      LOG.info(String.format("added user : %d to lobby : %d",userId,lobbyId));

      return lobby;

    }

    public Lobby removeUserFromLobby(Integer userId,Integer lobbyId) throws LobbyServiceException {

      // Find entities

      User user = findUserById(userId);
      Lobby lobby = findLobbyById(lobbyId);

      // Remove User

      lobby = removeUserFromLobby(user,lobby);

      LOG.info(String.format("removed user : %d from lobby : %d",userId,lobbyId));

      return lobby;
    }


    public Lobby transferLobbyOwnership(Integer lobbyId,Integer ownerId, Integer userId) throws LobbyServiceException {

      // Find entities

      Lobby lobby = findLobbyById(lobbyId);
      verifyUserExists(userId);

      // Transfer Lobby

      lobby.owner = AggregateReference.to(userId);
      lobby = lobbies.save(lobby);

      LOG.info(String.format("transfered lobby %d ownership from %d to %d",lobbyId,ownerId,userId));

      return lobby;

    }

    public Lobby update(Integer lobbyId,UpdateLobbyDTO dto) throws LobbyServiceException {

      // Find entities

      Lobby lobby = findLobbyById(lobbyId);

      // Update lobby
     
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

    public Boolean delete(Integer lobbyId) throws LobbyServiceException {

      // Find Entity

      Lobby lobby = findLobbyById(lobbyId);
      
      // Delete lobby

      lobbies.delete(lobby);

      return true;
      
    }

    public Lobby createNew(Integer userId) throws LobbyServiceException {

      // Find entities

      User user = findUserById(userId);

      Optional<Lobby> optOwnedLobby = lobbies.findOwnedLobby(userId);
      if ( optOwnedLobby.isPresent() ) {
        LOG.warn(String.format("User %d is trying to create a new lobby, but they already have one  %d",userId,optOwnedLobby.get().id));
        throw new LobbyServiceException(LobbyServiceError.LOBBY_ALREADY_OWNED);
      }

      Optional<Lobby> optUserLobby = lobbies.findUserLobby(userId);

      //Add User To Lobby

      if ( optUserLobby.isPresent() ) {
        removeUserFromLobby(userId,optUserLobby.get().id);
      }

      Lobby lobby = lobbies.save(new Lobby(user.username+"\'s lobby",6,false,AggregateReference.to(userId)));
      LobbyUserReference userRef = new LobbyUserReference(AggregateReference.to(userId));
      lobby.users.add(userRef);

      //Set Lobby's Owner
      
      lobby.owner = AggregateReference.to(user.id);
      lobbies.save(lobby);

      return lobby;
      
    }

    /* FIXME : I don't like this at all */
    private LobbyDTO lobbyToLobbyDTO(Lobby lobby) throws LobbyServiceException {

      LobbyDTO lobbyDto = new LobbyDTO(lobby);
      //User owner = userService.getUser(lobby.owner.getId());
      Optional<User> ownerOpt = users.findById(lobby.owner.getId());
      if ( ownerOpt.isEmpty() ) {
        throw new LobbyServiceException(LobbyServiceError.USER_NOT_FOUND);
      }
      User owner = ownerOpt.get();

      List<Integer> userIds = lobby.users.stream()
        .map( x -> x.user.getId() )
        .collect(Collectors.toList());
      Iterable<User> lobbyUsers =  users.findAllById(userIds);
      List<User> lobbyUsersList = Conversions.iterableToList(lobbyUsers);

      List<LobbyUserDTO> userDtos = lobbyUsersList.stream().
        map( x -> new LobbyUserDTO(x))
        .collect(Collectors.toList());

      lobbyDto.owner = new LobbyUserDTO(owner);
      lobbyDto.users = userDtos;

      return lobbyDto;

    }


    private Lobby findLobbyById(Integer lobbyId) throws LobbyServiceException {

      Optional<Lobby>  optLobby = lobbies.findById(lobbyId);
      if ( optLobby.isEmpty() ) {
        LOG.warn(String.format("No such lobby %d",lobbyId));
        throw new LobbyServiceException(LobbyServiceError.LOBBY_NOT_FOUND);
      }
      return optLobby.get();
    }

    // Throw if user doesn't exist
    private void verifyUserExists(Integer userId) throws LobbyServiceException {
      if (!users.existsById(userId)) {
        LOG.error(String.format("No such user %d exists",userId));
        throw new LobbyServiceException(LobbyServiceError.ERROR);
      }
    }

    private User findUserById(Integer userId) throws LobbyServiceException {
      Optional<User> optUser = users.findById(userId);
      if ( !optUser.isPresent() ) {
        LOG.error(String.format("No such user %d",userId));
        throw new LobbyServiceException(LobbyServiceError.ERROR);
      }
      return optUser.get();
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
