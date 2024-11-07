package com.keville.ReBoggled.service.lobbyService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import com.keville.ReBoggled.events.GameEndEvent;
import com.keville.ReBoggled.events.StartLobbyEvent;
import com.keville.ReBoggled.DTO.LobbyDTO;
import com.keville.ReBoggled.DTO.LobbyMessageRequestDTO;
import com.keville.ReBoggled.DTO.LobbySummaryDTO;
import com.keville.ReBoggled.DTO.LobbyUpdateRequestDTO;
import com.keville.ReBoggled.model.game.Game;
import com.keville.ReBoggled.model.game.GameSettings;
import com.keville.ReBoggled.model.lobby.Lobby;
import com.keville.ReBoggled.model.lobby.LobbyMessage;
import com.keville.ReBoggled.model.lobby.LobbyUserReference;
import com.keville.ReBoggled.model.user.User;
import com.keville.ReBoggled.repository.GameRepository;
import com.keville.ReBoggled.repository.LobbyMessageRepository;
import com.keville.ReBoggled.repository.LobbyRepository;
import com.keville.ReBoggled.repository.UserRepository;
import com.keville.ReBoggled.service.exceptions.BadRequest;
import com.keville.ReBoggled.service.exceptions.EntityNotFound;
import com.keville.ReBoggled.service.exceptions.NotAuthorized;
import com.keville.ReBoggled.service.gameService.GameService;
import com.keville.ReBoggled.service.gameService.board.BoardGenerationException;
import com.keville.ReBoggled.service.utils.ServiceUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Component
public class DefaultLobbyService implements LobbyService {

    private static final Logger LOG = LoggerFactory.getLogger(LobbyService.class);

    private GameRepository games;
    private LobbyRepository lobbies;
    private LobbyMessageRepository lobbyMessages;
    private UserRepository users;
    private GameService gameService;
    private LobbyTokenService tokenService;
    private ApplicationEventPublisher applicationEventPublisher;
    private TaskScheduler taskScheduler;

    public DefaultLobbyService(
        @Autowired LobbyRepository lobbies,
        @Autowired LobbyMessageRepository lobbyMessages,
        @Autowired UserRepository users, 
        @Autowired GameRepository games,
        @Autowired GameService gameService,
        @Autowired LobbyTokenService tokenService,
        @Autowired ApplicationEventPublisher applicationEventPublisher,
        @Autowired TaskScheduler taskScheduler) {

      this.lobbies = lobbies;
      this.lobbyMessages = lobbyMessages;
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

    public Lobby getLobby(Integer id) throws EntityNotFound {
      Lobby lobby = ServiceUtils.findLobbyById(lobbies,id);
      return lobby;
    }

    public Integer getLobbyOwnerId(int id) throws EntityNotFound {
      Lobby lobby = ServiceUtils.findLobbyById(lobbies,id);
      return lobby.owner.getId();
    }

    public Integer getUserLobbyId(int id) {
      Optional<Lobby> optUserLobby = lobbies.findUserLobby(id);
      return optUserLobby.isPresent() ? optUserLobby.get().id : -1;
    }

    public String getInviteLink(Integer lobbyId) throws NotAuthorized {

      User principal = ServiceUtils.getPrincipal();
      Lobby lobby = ServiceUtils.findLobbyById(lobbies, lobbyId);

      if ( !lobby.users.stream().anyMatch( lur -> lur.user.getId().equals(principal.id)) ) {
        throw new NotAuthorized("User not in lobby, can't invite");
      }

      String token = tokenService.getLobbyToken(lobbyId);
      String url = "/#join?id=" + lobbyId + "&token=" + token;

      return url;

    }

    public void join(Integer lobbyId,Optional<String> token) throws EntityNotFound,BadRequest,NotAuthorized {

      Lobby lobby = ServiceUtils.findLobbyById(lobbies,lobbyId);
      User principal = ServiceUtils.getPrincipal();

      //Authorize

      if( lobby.isPrivate ) {
        if ( token.isEmpty() && !lobby.owner.getId().equals(principal.id) ) {
          throw new NotAuthorized("Lobby is private");
        }

        if ( token.isPresent() && !tokenService.getLobbyToken(lobbyId).equals(token.get()) ) {
          throw new NotAuthorized("Wrong Token");
        }
      }

      addUserToLobby(principal, lobby);

    }

    public void kick(Integer lobbyId,Integer userId) throws EntityNotFound,BadRequest,NotAuthorized {

      Lobby lobby = ServiceUtils.findLobbyById(lobbies,lobbyId);
      User user = ServiceUtils.findUserById(users,userId);
      User principal = ServiceUtils.getPrincipal();

      //Authorize

      if ( !lobby.owner.getId().equals(principal.id) ) {
        throw new NotAuthorized(String.format("user %d is not the lobby owner",principal.id));
      }

      removeUserFromLobby(user, lobby);
      lobbyMessages.save(LobbyMessage.kickLobbyMessage(AggregateReference.to(lobby.id), user.username));

    }

    public void leave(Integer lobbyId) throws EntityNotFound,BadRequest,NotAuthorized {

      Lobby lobby = ServiceUtils.findLobbyById(lobbies,lobbyId);
      User principal = ServiceUtils.getPrincipal();

      boolean deleted = removeUserFromLobby(principal, lobby);
      if ( deleted ) {
        lobbyMessages.save(LobbyMessage.leaveLobbyMessage(AggregateReference.to(lobby.id), principal.username));
      }

    }

    public Lobby addMessage(Integer lobbyId,LobbyMessageRequestDTO lobbyMessageRequestDTO) throws EntityNotFound,NotAuthorized {

      User principal  = ServiceUtils.getPrincipal();
      Lobby lobby = ServiceUtils.findLobbyById(lobbies,lobbyId);

      // Authorize

      if (!lobby.users.stream().anyMatch( (lur) -> lur.user.getId().equals(principal.id))) {
        throw new NotAuthorized(principal.id + " is not in lobby " + lobbyId);
      }

      // save chat message

      LobbyMessage message = new LobbyMessage();
      message.lobby = AggregateReference.to(lobbyId);
      message.sent = LocalDateTime.now();
      message.message = lobbyMessageRequestDTO.message();
      message.user = AggregateReference.to(principal.id);
      lobbyMessages.save(message);

      return lobby;

    }

    public void promote(Integer lobbyId,Integer userId) throws EntityNotFound,NotAuthorized,BadRequest {

      Lobby lobby = ServiceUtils.findLobbyById(lobbies,lobbyId);
      User user = ServiceUtils.findUserById(users,userId);
      User principal  = ServiceUtils.getPrincipal();

      if ( principal.id != lobby.owner.getId() ) {
        throw new NotAuthorized(String.format("user %d is not the lobby %d owner",principal.id,lobby.id));
      }

      transferLobbyOwnership(lobby, user);

    }


    public Lobby update(Integer lobbyId,LobbyUpdateRequestDTO lobbyUpdate) throws EntityNotFound,BadRequest {

      Lobby lobby = ServiceUtils.findLobbyById(lobbies,lobbyId);
      User principal = ServiceUtils.getPrincipal();

      // Authorize

      if ( !lobby.owner.getId().equals(principal.id) ) {
        throw new NotAuthorized(String.format("principal %d is not the lobby owner %d",principal.id,lobby.owner.getId()));
      }

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
          LOG.warn(String.format("ignoring request to diminish lobby : %d's capacity because it's current users wont' fit",lobbyId));
          throw new BadRequest("Capacity Shortening");
        }
      }

      lobby = lobbies.save(lobby);

      return lobby;
      
    }

    public Boolean delete(Integer lobbyId) throws EntityNotFound {

      Lobby lobby = ServiceUtils.findLobbyById(lobbies,lobbyId);
      lobbies.delete(lobby);
      return true;
      
    }

    public Lobby create() throws EntityNotFound,BadRequest {

      User principal = ServiceUtils.getPrincipal();
      Optional<Lobby> optOwnedLobby = lobbies.findOwnedLobby(principal.id);
      if ( optOwnedLobby.isPresent() ) {
        LOG.warn(String.format("User %d is trying to create a new lobby, but they already have one  %d",principal.id,optOwnedLobby.get().id));
        throw new BadRequest("User already owns a lobby");
      }

      Lobby lobby = lobbies.save(new Lobby(principal.username+"\'s lobby",6,false,AggregateReference.to(principal.id)));
      lobby.owner = AggregateReference.to(principal.id);
      addUserToLobby(principal,lobby);
      lobbies.save(lobby);

      return lobby;
      
    }

    public Lobby start(Integer lobbyId) throws EntityNotFound,NotAuthorized,BoardGenerationException {

      Lobby lobby = ServiceUtils.findLobbyById(lobbies,lobbyId);

      Game game = gameService.createGame(lobby);
      lobby.game = AggregateReference.to(game.id);
      lobbies.save(lobby);
      applicationEventPublisher.publishEvent(new StartLobbyEvent(lobby.id));
      taskScheduler.schedule(()->{
        applicationEventPublisher.publishEvent(new GameEndEvent(lobby.id));},
        Instant.now().plusSeconds(lobby.gameSettings.duration)
      );

      return lobby;

    }

    public List<LobbySummaryDTO> getLobbySummaryDTOs() throws EntityNotFound {

      List<LobbySummaryDTO> summaries = new LinkedList<LobbySummaryDTO>();
      lobbies.findAll().forEach( lobby -> {
        summaries.add(new LobbySummaryDTO(lobby));
      });

      return summaries;
    }

    public LobbyDTO getLobbyDTO(int lobbyId) throws EntityNotFound {

      Lobby lobby = ServiceUtils.findLobbyById(lobbies, lobbyId);
      User owner = ServiceUtils.findUserById(users, lobby.owner.getId());
      Game gameGame = null; //hack around lambda closure
      if ( lobby.game != null ) {
        gameGame = ServiceUtils.findGameById(games,lobby.game.getId());
      }
      final Game game = gameGame;

      LobbyDTO lobbyDto = new LobbyDTO();
      lobbyDto.id = lobby.id;
      lobbyDto.name = lobby.name;
      lobbyDto.capacity = lobby.capacity;
      lobbyDto.isPrivate = lobby.isPrivate;
      lobbyDto.gameSettings = lobby.gameSettings;
      lobbyDto.gameId = game == null ? null : game.id;
      lobbyDto.gameActive = game != null;

      // lobby owner 

      //truly a cursed syntax for inner class construction
      lobbyDto.owner = lobbyDto.new LobbyUserDTO(owner.id,owner.username,true);

      // lobby users
      
      lobbyDto.users = lobby.users.stream()
        .map( lur -> ServiceUtils.findUserById(users,lur.user.getId()) )
        .map( user -> {
          LobbyDTO.LobbyUserDTO dto = lobbyDto.new LobbyUserDTO();
          dto.id = user.id;
          dto.username = user.username;
          if ( game != null ) {
            dto.inGame = game.users.stream()
              .map( gur -> gur.user.getId() )
              .anyMatch( id -> id.equals(user.id));
          }
          return dto;
      }).toList();

      return lobbyDto;

    }

    private Lobby transferLobbyOwnership(Lobby lobby, User user) throws EntityNotFound,NotAuthorized,BadRequest {

      if ( lobbies.findOwnedLobby(user.id).isPresent() ) {
        throw new BadRequest(String.format("user %d can't be promoted to lobby owner because they already own a lobby",user.id));
      }

      lobby.owner = AggregateReference.to(user.id);
      lobby = lobbies.save(lobby);
      lobbyMessages.save(LobbyMessage.promoteLobbyMessage(AggregateReference.to(lobby.id), user.username));
      LOG.info(String.format("lobby %d was transferred to %d",lobby.id,user.id));

      return lobby;

    }

    // remove user and return whether the lobby still exists
    private boolean removeUserFromLobby(User user,Lobby lobby) throws EntityNotFound,BadRequest {

      LobbyUserReference userRef = new LobbyUserReference(AggregateReference.to(lobby.id),AggregateReference.to(user.id));
        
      if ( !lobby.users.stream().anyMatch( lur -> lur.user.getId().equals(user.id)) ) {
        throw new BadRequest(String.format("User %d does not participate in lobby %d",user.id,lobby.id));
      }

      lobby.users.remove(userRef);
      lobby = lobbies.save(lobby);

      if ( lobby.owner.getId() == user.id && lobby.users.size() != 0) {

        //automatic promotion
        LobbyUserReference newOwnerRef = lobby.users.stream().findAny().get();
        User newOwner = ServiceUtils.findUserById(users,newOwnerRef.user.getId());
        transferLobbyOwnership(lobby,newOwner);

      } else if ( lobby.users.size() == 0 ) {
        //automatic deletion
        lobbies.delete(lobby);
        LOG.info(String.format("lobby %d was deleted",lobby.id));
        return false;
      }

      return true;


    }

    /* add the user to the lobby, removing them from there previous lobby if any */
    private Lobby addUserToLobby(User user,Lobby lobby) throws BadRequest,EntityNotFound {

      if( lobby.users.size() == lobby.capacity ) {
        throw new BadRequest(String.format("Lobby %d is full",lobby.id));
      }

      // remove the user from there previous lobby (if any)

      Optional<Lobby> optUserLobby = lobbies.findUserLobby(user.id);
      if ( optUserLobby.isPresent()) {

        if ( optUserLobby.get().id == lobby.id ) {
          throw new BadRequest(String.format("user %d is already in lobby %d",user.id,lobby.id));
        } else {
          removeUserFromLobby(user,optUserLobby.get());
        }

      }

      // add user to new lobby

      LobbyUserReference userRef = new LobbyUserReference(AggregateReference.to(lobby.id),AggregateReference.to(user.id));
      lobby.users.add(userRef);
      lobby = lobbies.save(lobby);

      // spawn chat message

      lobbyMessages.save(LobbyMessage.joinLobbyMessage(AggregateReference.to(lobby.id), user.username));
      return lobby;

    }

}
