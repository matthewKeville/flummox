package com.keville.ReBoggled.service.lobbyService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import com.keville.ReBoggled.DTO.LobbyUserDTO;
import com.keville.ReBoggled.events.GameEndEvent;
import com.keville.ReBoggled.events.StartLobbyEvent;
import com.keville.ReBoggled.DTO.LobbySummaryDTO;
import com.keville.ReBoggled.model.game.Game;
import com.keville.ReBoggled.model.game.GameSettings;
import com.keville.ReBoggled.model.lobby.Lobby;
import com.keville.ReBoggled.model.lobby.LobbyUpdate;
import com.keville.ReBoggled.model.lobby.LobbyUserReference;
import com.keville.ReBoggled.model.user.User;
import com.keville.ReBoggled.repository.GameRepository;
import com.keville.ReBoggled.repository.LobbyRepository;
import com.keville.ReBoggled.repository.UserRepository;
import com.keville.ReBoggled.service.gameService.GameService;
import com.keville.ReBoggled.service.gameService.GameServiceException;
import com.keville.ReBoggled.service.lobbyService.LobbyServiceException.LobbyServiceError;
import com.keville.ReBoggled.util.Conversions;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class DefaultLobbyService implements LobbyService {

    private static final Logger LOG = LoggerFactory.getLogger(LobbyService.class);

    private GameRepository games;
    private LobbyRepository lobbies;
    private UserRepository users;
    private GameService gameService;
    private LobbyTokenService tokenService;
    private ApplicationEventPublisher applicationEventPublisher;
    private TaskScheduler taskScheduler;

    public DefaultLobbyService(
        @Autowired LobbyRepository lobbies,
        @Autowired UserRepository users, 
        @Autowired GameRepository games,
        @Autowired GameService gameService,
        @Autowired LobbyTokenService tokenService,
        @Autowired ApplicationEventPublisher applicationEventPublisher,
        @Autowired TaskScheduler taskScheduler) {

      this.lobbies = lobbies;
      this.users = users;
      this.games = games;
      this.gameService = gameService;
      this.tokenService = tokenService;
      this.applicationEventPublisher = applicationEventPublisher;
      this.taskScheduler = taskScheduler;
    }

    public Iterable<Lobby> getLobbies() {
      return lobbies.findAll();
    }

    public Lobby getLobby(int id) throws LobbyServiceException {
      Lobby lobby = findLobbyById(id);
      return lobby;
    }

    public Integer getLobbyOwnerId(int id) throws LobbyServiceException {
      Lobby lobby = findLobbyById(id);
      return lobby.owner.getId();
    }

    public Integer getUserLobbyId(int id) {

      Optional<Lobby> optUserLobby = lobbies.findUserLobby(id);
      if ( optUserLobby.isPresent()) {
        return optUserLobby.get().id;
      }

      return -1;

    }

    public String getUserInviteLink(Integer userId) throws LobbyServiceException {

      Optional<Lobby> optUserLobby = lobbies.findUserLobby(userId);
      if ( !optUserLobby.isPresent()) {
        throw new LobbyServiceException(LobbyServiceError.LOBBY_NOT_FOUND);
      }
      int lobbyId = optUserLobby.get().id;

      String token = tokenService.getLobbyToken(lobbyId);
      String url = "/join?id=" + lobbyId + "&token=" + token;

      return url;

    }

    public void addLobby(Lobby lobby) {
      lobbies.save(lobby);
    }

    public Lobby addUserToLobby(Integer userId,Integer lobbyId, Optional<String> token) throws LobbyServiceException {

      // find entities

      Lobby lobby = findLobbyById(lobbyId);
      User user = findUserById(userId);

      // can user join?

      /*
      if( lobby.isPrivate && !lobby.owner.getId().equals(userId) ) {
        LOG.warn(String.format("Can't add user : %d to lobby : %d, because it's private",userId,lobbyId));
        throw new LobbyServiceException(LobbyServiceError.LOBBY_PRIVATE);
      }
      */
      if( lobby.isPrivate ) {
        if ( token.isEmpty() && !lobby.owner.getId().equals(userId) ) {
          LOG.warn(String.format("Can't add user : %d to lobby : %d, because it's private and no token is provided",userId,lobbyId));
          throw new LobbyServiceException(LobbyServiceError.LOBBY_PRIVATE);
        }

        LOG.info("token recv : " + token.get());
        LOG.info("token act  : " + tokenService.getLobbyToken(lobbyId));
        if ( token.isPresent() && !tokenService.getLobbyToken(lobbyId).equals(token.get()) ) {
          LOG.warn(String.format("Can't add user : %d to lobby : %d, because the token is wrong",userId,lobbyId));
          throw new LobbyServiceException(LobbyServiceError.LOBBY_PRIVATE);
        }
      }

      if( lobby.users.size() == lobby.capacity ) {
        LOG.warn(String.format("can't add user : %d to lobby : %d, because it's at capacity",userId,lobbyId));
        throw new LobbyServiceException(LobbyServiceError.LOBBY_FULL);
      }

      // remove the user from there previous lobby (if any)

      Optional<Lobby> optUserLobby = lobbies.findUserLobby(userId);
      if ( optUserLobby.isPresent()) {
        if ( optUserLobby.get().id == lobbyId ) {
          return lobby;
        }
        removeUserFromLobby(user,optUserLobby.get());
      }

      // add user to new lobby

      LobbyUserReference userRef = new LobbyUserReference(AggregateReference.to(lobby.id),AggregateReference.to(userId));
      lobby.users.add(userRef);
      lobby = lobbies.save(lobby);

      LOG.info(String.format("added user : %d to lobby : %d",userId,lobbyId));

      return lobby;

    }

    public Optional<Lobby> removeUserFromLobby(Integer userId,Integer lobbyId) throws LobbyServiceException {

      // Find entities

      User user = findUserById(userId);
      Lobby lobby = findLobbyById(lobbyId);

      // Remove User

      return removeUserFromLobby(user,lobby);
    }


    public Lobby transferLobbyOwnership(Integer lobbyId,Integer userId) throws LobbyServiceException {

      // Find entities

      Lobby lobby = findLobbyById(lobbyId);
      verifyUserExists(userId);
      verifyUserNotGuest(userId);

      // Transfer Lobby

      lobby.owner = AggregateReference.to(userId);
      lobby = lobbies.save(lobby);

      LOG.info(String.format("transfered lobby %d ownership to %d",lobbyId,userId));

      return lobby;

    }

    public Lobby update(LobbyUpdate lobbyUpdate) throws LobbyServiceException {

      // Find entities

      Lobby lobby = findLobbyById(lobbyUpdate.id);

      // Update lobby
    
      if ( lobbyUpdate.name.isPresent() ) {
        lobby.name = lobbyUpdate.name.get();
      }

      if ( lobbyUpdate.isPrivate.isPresent() ) {
        lobby.isPrivate = lobbyUpdate.isPrivate.get();
      }

      if ( lobbyUpdate.gameSettings.isPresent() ) {

        GameSettings gameSettings = lobbyUpdate.gameSettings.get();

        if ( gameSettings.boardTopology != null ) {
          lobby.gameSettings.boardTopology = gameSettings.boardTopology;
        }

        if ( gameSettings.boardSize != null ) {
          lobby.gameSettings.boardSize = gameSettings.boardSize;
        }

        if ( gameSettings.tileRotation != null ) {
          lobby.gameSettings.tileRotation = gameSettings.tileRotation;
        }

        if ( gameSettings.findRule != null ) {
          lobby.gameSettings.findRule = gameSettings.findRule;
        }
          
        if ( gameSettings.duration != null ) {
          lobby.gameSettings.duration = gameSettings.duration;
        }

      }

      if ( lobbyUpdate.capacity.isPresent() ) {
        int newCap = lobbyUpdate.capacity.get();
        if ( lobby.users.size() <= newCap ) {
          lobby.capacity = newCap;
        } else {
          LOG.warn(String.format("ignoring request to diminish lobby : %d's capacity because it's current users wont' fit",lobbyUpdate.id));
          throw new LobbyServiceException(LobbyServiceError.CAPACITY_SHORTENING);
        }
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
      LobbyUserReference userRef = new LobbyUserReference(AggregateReference.to(lobby.id),AggregateReference.to(userId));
      lobby.users.add(userRef);

      //Set Lobby's Owner
      
      lobby.owner = AggregateReference.to(user.id);
      lobbies.save(lobby);

      return lobby;
      
    }

    // TODO : this could probably be stuffed further back into a db query to avoid pulling out unec. data
    public boolean isOutdated(Integer lobbyId,LocalDateTime lastTime) throws LobbyServiceException {

      // do query
      Lobby lobby = findLobbyById(lobbyId);
      return lobby.lastModifiedDate.isAfter(lastTime);

    }

    public Lobby startGame(Integer lobbyId) throws LobbyServiceException {

      Lobby lobby = findLobbyById(lobbyId);

      try {

        Game game = gameService.createGame(lobby);
        lobby.game = AggregateReference.to(game.id);
        lobbies.save(lobby);

        applicationEventPublisher.publishEvent(new StartLobbyEvent(lobby.id));
        taskScheduler.schedule(()->{
          applicationEventPublisher.publishEvent(new GameEndEvent(lobby.id));},
          Instant.now().plusSeconds(lobby.gameSettings.duration)
        );

        return lobby;

      } catch (GameServiceException exception) {
        throw new LobbyServiceException(LobbyServiceError.START_GAME_FAILURE);
      }

    }

    public boolean exists (Integer lobbyId) {
      return lobbies.existsById(lobbyId);
    }

    public List<LobbySummaryDTO> getLobbySummaryDTOs() throws LobbyServiceException {

      Iterable<Lobby> allLobbies = lobbies.findAll();
      List<LobbySummaryDTO> allLobbiesList = new LinkedList<LobbySummaryDTO>();

      for ( Lobby lobby : allLobbies ) {
        allLobbiesList.add(createLobbySummaryDTO(lobby));
      }
      return allLobbiesList;
    }

    public LobbySummaryDTO getLobbySummaryDTO(int id) throws LobbyServiceException {
      Optional<Lobby> optLobby = lobbies.findById(id);
      if (optLobby.isEmpty()) {
        throw new LobbyServiceException(LobbyServiceError.LOBBY_NOT_FOUND);
      }
      return createLobbySummaryDTO(optLobby.get());
    }

    private LobbySummaryDTO createLobbySummaryDTO(Lobby lobby) throws LobbyServiceException {

      LobbySummaryDTO lobbyDto = new LobbySummaryDTO(lobby);

      Optional<User> ownerOpt = users.findById(lobby.owner.getId());
      if ( ownerOpt.isEmpty() ) {
        throw new LobbyServiceException(LobbyServiceError.USER_NOT_FOUND);
      }

      User owner = ownerOpt.get();

      lobbyDto.owner = new LobbyUserDTO(owner);

      // lobby users

      List<Integer> userIds = lobby.users.stream()
        .map( x -> x.user.getId() )
        .collect(Collectors.toList());

      Iterable<User> lobbyUsers =  users.findAllById(userIds);
      List<User> lobbyUsersList = Conversions.iterableToList(lobbyUsers);

      List<LobbyUserDTO> userDtos = lobbyUsersList.stream().
        map( x -> new LobbyUserDTO(x))
        .collect(Collectors.toList());

      lobbyDto.users = userDtos;

      if ( lobby.game != null ) {

        Optional<Game> gameOpt = games.findById(lobby.game.getId());

        if ( gameOpt.isEmpty() ) {
          throw new LobbyServiceException(LobbyServiceError.GAME_NOT_FOUND);
        }
        Game game = gameOpt.get();

        lobbyDto.gameActive = LocalDateTime.now().isBefore(game.end);
        lobbyDto.gameId = game.id;

        // lobby game users
      
        List<Integer> gameUserIds = game.users.stream()
          .map( x -> x.user.getId() )
          .collect(Collectors.toList());

        Iterable<User> gameUsers =  users.findAllById(gameUserIds);
        List<User> gameUserList = Conversions.iterableToList(gameUsers);

        List<LobbyUserDTO> gameUserDtos = gameUserList.stream().
          map( x -> new LobbyUserDTO(x))
          .collect(Collectors.toList());

        lobbyDto.gameUsers = gameUserDtos;

      } else {

        lobbyDto.gameActive = false;
        lobbyDto.gameUsers = new ArrayList<LobbyUserDTO>();

      }


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

    //@Pre : users.exists(userId)
    private void verifyUserNotGuest(Integer userId) throws LobbyServiceException {
      Optional<User> optUser = users.findById(userId);
      User user = optUser.get();
      if (user.guest) {
        LOG.error(String.format("Guest user %d not applicable",userId));
        throw new LobbyServiceException(LobbyServiceError.GUEST_NOT_ALLOWED);
      }
    }

    //fixme : GameService employs a similar method, should each service have
    //this utility defined in there own way?.... Probably not
    private User findUserById(Integer userId) throws LobbyServiceException {
      Optional<User> optUser = users.findById(userId);
      if ( !optUser.isPresent() ) {
        LOG.error(String.format("No such user %d",userId));
        throw new LobbyServiceException(LobbyServiceError.ERROR);
      }
      return optUser.get();
    }

    private Optional<Lobby> removeUserFromLobby(User user,Lobby lobby) throws LobbyServiceException {

      LobbyUserReference userRef = new LobbyUserReference(AggregateReference.to(lobby.id),AggregateReference.to(user.id));
        
      if (!lobby.users.contains(userRef)) {
        LOG.warn(String.format("Can't remove user %d from lobby %d because they don't belong to it",user.id,lobby.id));
        throw new LobbyServiceException(LobbyServiceError.USER_NOT_IN_LOBBY);
      }

      lobby.users.remove(userRef);
      lobby = lobbies.save(lobby);

      LOG.info(String.format("removed user : %d from lobby : %d",user.id,lobby.id));

      if ( lobby.users.size() == 0 ) {
        lobbies.delete(lobby);
        return null;
      }

      return Optional.of(lobby);

    }

}
