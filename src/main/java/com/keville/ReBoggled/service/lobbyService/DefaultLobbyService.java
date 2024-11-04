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
import com.keville.ReBoggled.DTO.LobbyMessageDTO;
import com.keville.ReBoggled.DTO.LobbyNewMessageDTO;
import com.keville.ReBoggled.DTO.LobbySummaryDTO;
import com.keville.ReBoggled.model.game.Game;
import com.keville.ReBoggled.model.game.GameSettings;
import com.keville.ReBoggled.model.lobby.Lobby;
import com.keville.ReBoggled.model.lobby.LobbyMessage;
import com.keville.ReBoggled.model.lobby.LobbyUpdate;
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

    public Lobby getLobby(int id) throws EntityNotFound {
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

    public String getUserInviteLink(Integer userId) throws NotAuthorized {

      Optional<Lobby> optUserLobby = lobbies.findUserLobby(userId);
      if ( !optUserLobby.isPresent()) {
        throw new NotAuthorized("User not in lobby, can't invite");
      }
      int lobbyId = optUserLobby.get().id;

      String token = tokenService.getLobbyToken(lobbyId);
      String url = "/#join?id=" + lobbyId + "&token=" + token;

      return url;

    }

    public Lobby addUserToLobby(Integer userId,Integer lobbyId, Optional<String> token) throws EntityNotFound,BadRequest,NotAuthorized {

      // find entities

      Lobby lobby = ServiceUtils.findLobbyById(lobbies,lobbyId);
      User user = ServiceUtils.findUserById(users,userId);

      // can user join?

      if( lobby.isPrivate ) {
        if ( token.isEmpty() && !lobby.owner.getId().equals(userId) ) {
          throw new NotAuthorized("Lobby is private");
        }

        if ( token.isPresent() && !tokenService.getLobbyToken(lobbyId).equals(token.get()) ) {
          throw new NotAuthorized("Wrong Token");
        }
      }

      if( lobby.users.size() == lobby.capacity ) {
        throw new BadRequest("Lobby is full");
      }

      // remove the user from there previous lobby (if any)

      Optional<Lobby> optUserLobby = lobbies.findUserLobby(userId);
      if ( optUserLobby.isPresent()) {
        if ( optUserLobby.get().id == lobbyId ) {
          return lobby;
        }
        removeUserFromLobby(user,optUserLobby.get(),false);
      }

      // add user to new lobby

      LobbyUserReference userRef = new LobbyUserReference(AggregateReference.to(lobby.id),AggregateReference.to(userId));
      lobby.users.add(userRef);
      lobby = lobbies.save(lobby);

      // spawn chat message

      lobbyMessages.save(LobbyMessage.joinLobbyMessage(AggregateReference.to(lobbyId), user.username));

      LOG.info(String.format("added user : %d to lobby : %d",userId,lobbyId));

      return lobby;

    }

    public Lobby addMessageToLobby(LobbyNewMessageDTO lobbyNewMessage,Integer lobbyId,Integer userId) throws EntityNotFound,NotAuthorized {

      // find entities

      User principal  = ServiceUtils.getPrincipal();
      Lobby lobby = ServiceUtils.findLobbyById(lobbies,lobbyId);

      // authorized?

      if (!lobby.users.stream().anyMatch( (lur) -> lur.user.getId().equals(userId))) {
        throw new NotAuthorized(principal.id + " tried to leave " + userId);
      }

      // save chat message

      LobbyMessage message = new LobbyMessage();
      message.lobby = AggregateReference.to(lobbyId);
      message.sent = LocalDateTime.now();
      message.message = lobbyNewMessage.message();
      message.user = AggregateReference.to(userId);
      lobbyMessages.save(message);

      LOG.info(String.format("added new lobby message : %s to lobby : %d",message.message,lobbyId));

      return lobby;

    }

    public Optional<Lobby> removeUserFromLobby(Integer userId,Integer lobbyId,boolean kicked) throws EntityNotFound,BadRequest,NotAuthorized{

      User principal  = ServiceUtils.getPrincipal();
      if ( principal.id != userId ) {
        throw new NotAuthorized(principal.id + " tried to leave " + userId);
      }

      // Find entities

      User user = ServiceUtils.findUserById(users,userId);
      Lobby lobby = ServiceUtils.findLobbyById(lobbies,lobbyId);

      // Remove User
      return removeUserFromLobby(user,lobby,kicked);

    }


    public Lobby transferLobbyOwnership(Integer lobbyId,Integer userId) throws EntityNotFound,NotAuthorized {

      User principal  = ServiceUtils.getPrincipal();
      Lobby lobby = ServiceUtils.findLobbyById(lobbies,lobbyId);
      User user = ServiceUtils.findUserById(users,userId);

      if ( principal.id != lobby.owner.getId() ) {
        throw new NotAuthorized(principal.id + " can't change " + userId + " lobby's owner");
      }


      // Transfer Lobby

      lobby.owner = AggregateReference.to(userId);
      lobby = lobbies.save(lobby);

      LOG.info(String.format("transfered lobby %d ownership to %d",lobbyId,userId));
      lobbyMessages.save(LobbyMessage.promoteLobbyMessage(AggregateReference.to(lobby.id), user.username));

      return lobby;

    }

    public Lobby update(LobbyUpdate lobbyUpdate) throws EntityNotFound,BadRequest {

      // Find entities

      Lobby lobby = ServiceUtils.findLobbyById(lobbies,lobbyUpdate.id);
      User principal = ServiceUtils.getPrincipal();
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
          LOG.warn(String.format("ignoring request to diminish lobby : %d's capacity because it's current users wont' fit",lobbyUpdate.id));
          throw new BadRequest("Capacity Shortening");
        }
      }

      lobby = lobbies.save(lobby);

      return lobby;
      
    }

    public Boolean delete(Integer lobbyId) throws EntityNotFound {

      // Find Entity

      Lobby lobby = ServiceUtils.findLobbyById(lobbies,lobbyId);
      
      // Delete lobby

      lobbies.delete(lobby);

      return true;
      
    }

    public Lobby createNew(Integer userId) throws EntityNotFound,BadRequest {

      // Find entities

      User user = ServiceUtils.findUserById(users, userId);

      Optional<Lobby> optOwnedLobby = lobbies.findOwnedLobby(userId);
      if ( optOwnedLobby.isPresent() ) {
        LOG.warn(String.format("User %d is trying to create a new lobby, but they already have one  %d",userId,optOwnedLobby.get().id));
        throw new BadRequest("User already owns a lobby");
      }

      Optional<Lobby> optUserLobby = lobbies.findUserLobby(userId);

      //Add User To Lobby

      if ( optUserLobby.isPresent() ) {
        removeUserFromLobby(userId,optUserLobby.get().id,false);
      }

      Lobby lobby = lobbies.save(new Lobby(user.username+"\'s lobby",6,false,AggregateReference.to(userId)));
      LobbyUserReference userRef = new LobbyUserReference(AggregateReference.to(lobby.id),AggregateReference.to(userId));
      lobby.users.add(userRef);

      //Set Lobby's Owner
      
      lobby.owner = AggregateReference.to(user.id);
      lobbies.save(lobby);

      return lobby;
      
    }

    public Lobby startGame(Integer lobbyId) throws EntityNotFound,NotAuthorized,BoardGenerationException {

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

    public List<LobbyMessageDTO> getLobbyMessageDTOs(int id) throws EntityNotFound {

      ServiceUtils.ensureExists(lobbies, id);

      List<LobbyMessageDTO> messages = new ArrayList<LobbyMessageDTO>();
      for ( LobbyMessage lm : lobbyMessages.findByLobby(id) ) {
        if ( lm.user == null ) {
          //system messages
          messages.add(new LobbyMessageDTO(lm));
        } else {
          User user = users.findById(lm.user.getId()).get();
          messages.add(new LobbyMessageDTO(lm, user.username));
        }
      }

      return messages;

    }

    public List<LobbySummaryDTO> getLobbySummaryDTOs() throws EntityNotFound {

      Iterable<Lobby> allLobbies = lobbies.findAll();
      List<LobbySummaryDTO> allLobbiesList = new LinkedList<LobbySummaryDTO>();

      for ( Lobby lobby : allLobbies ) {
        allLobbiesList.add(createLobbySummaryDTO(lobby));
      }
      return allLobbiesList;
    }

    public LobbySummaryDTO getLobbySummaryDTO(int id) throws EntityNotFound {
      Lobby lobby = ServiceUtils.findLobbyById(lobbies, id);
      return createLobbySummaryDTO(lobby);
    }

    //move to LobbySummarizer.java
    private LobbySummaryDTO createLobbySummaryDTO(Lobby lobby) throws EntityNotFound {

      User owner = ServiceUtils.findUserById(users, lobby.owner.getId());

      LobbySummaryDTO lobbyDto = new LobbySummaryDTO(lobby);
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

        Game game = ServiceUtils.findGameById(games, lobby.game.getId());

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

    /* this method is a little busy */
    private Optional<Lobby> removeUserFromLobby(User user,Lobby lobby,boolean kicked) throws EntityNotFound,BadRequest {

      LobbyUserReference userRef = new LobbyUserReference(AggregateReference.to(lobby.id),AggregateReference.to(user.id));
        
      if (!lobby.users.contains(userRef)) {
        LOG.warn(String.format("Can't remove user %d from lobby %d because they don't belong to it",user.id,lobby.id));
        throw new BadRequest("User does not participate in lobby");
      }

      lobby.users.remove(userRef);
      lobby = lobbies.save(lobby);

      LOG.info(String.format("removed user : %d from lobby : %d",user.id,lobby.id));

      if ( lobby.owner.getId() == user.id && lobby.users.size() != 0) {

        //promote

        LobbyUserReference newOwnerRef = lobby.users.stream().findAny().get();
        User newOwner = ServiceUtils.findUserById(users,newOwnerRef.user.getId());
        transferLobbyOwnership(lobby.id, newOwner.id);
        lobbyMessages.save(LobbyMessage.promoteLobbyMessage(AggregateReference.to(lobby.id), newOwner.username));

      } else if ( lobby.users.size() == 0 ) {

        //delete
        lobbies.delete(lobby);
        return Optional.empty();

      }

      if ( kicked ) {
        lobbyMessages.save(LobbyMessage.kickLobbyMessage(AggregateReference.to(lobby.id), user.username));
      } else {
        lobbyMessages.save(LobbyMessage.leaveLobbyMessage(AggregateReference.to(lobby.id), user.username));
      }


      return Optional.of(lobby);

    }

}
