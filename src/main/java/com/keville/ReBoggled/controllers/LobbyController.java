package com.keville.ReBoggled.controllers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import com.keville.ReBoggled.DTO.LobbyDTO;
import com.keville.ReBoggled.DTO.LobbyUserDTO;
import com.keville.ReBoggled.DTO.UpdateLobbyDTO;
import com.keville.ReBoggled.model.Lobby;
import com.keville.ReBoggled.model.User;
import com.keville.ReBoggled.service.LobbyService;
import com.keville.ReBoggled.service.LobbyService.LobbyServiceException;
import com.keville.ReBoggled.service.UserService;
import com.keville.ReBoggled.util.Conversions;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@RestControllerAdvice
@RestController
public class LobbyController {

  private static final Logger LOG = LoggerFactory.getLogger(LobbyController.class);

  private LobbyService lobbyService;
  private UserService userService;

  public LobbyController(@Autowired LobbyService lobbyService,
      @Autowired UserService userService) {
    this.lobbyService = lobbyService;
    this.userService = userService;
  }

  @GetMapping(value = { "/lobby", "/" })
  public ModelAndView view() {
    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("lobby");
    return modelAndView;
  }

  @GetMapping("/api/lobby")
  public Iterable<LobbyDTO> getLobbies(
      @RequestParam(required = false, name = "publicOnly") boolean publicOnly,
      HttpSession session) {

    LOG.info("hit /api/lobby");

    Iterable<Lobby> lobbies = lobbyService.getLobbies();
    List<LobbyDTO> lobbyDTOs = Conversions.iterableToList(lobbies).stream()
        .map(lobby -> lobbyToLobbyDTO(lobby))
        .collect(Collectors.toList());

    return lobbyDTOs;
  }

  @GetMapping("/api/lobby/{id}")
  public LobbyDTO getLobby(
      @PathVariable("id") Integer id,
      @Autowired HttpSession session) {

    LOG.info("hit /api/lobby/" + id);
    Optional<Lobby> lobby = lobbyService.getLobby(id);

    if (!lobby.isPresent()) {
      LOG.warn("attempted to access a non-existant lobby " + id);
      return null;
    }

    return lobbyToLobbyDTO(lobby.get());

  }

  @PostMapping("/api/lobby/{id}/join")
  public <T> ResponseEntity<T> joinLobby (
      @PathVariable("id") Integer id,
      @Autowired HttpSession session) {

    LOG.info("hit /api/lobby/" + id + "/join");
    Integer userId = (Integer) session.getAttribute("userId");
    if (userId == null) {
      LOG.warn("unable to identify the userId of the current Session");
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR");
    }

    try {
      Lobby response = lobbyService.addUserToLobby(userId, id);
    } catch (LobbyServiceException e)  {
      handleLobbyServiceException(e);
    }

    return ResponseEntity.ok().build();

  }

  @PostMapping("/api/lobby/{id}/kick/{userId}")
  public <T> ResponseEntity<T> kickPlayer(
      @PathVariable("id") Integer id,
      @PathVariable("userId") Integer userId, //To be kicked
      @Autowired HttpSession session) {

    LOG.info("hit /api/lobby/" + id + "/kick/" + userId);

    //Is the current user authorized to perform this action?
    Integer requesterId = (Integer) session.getAttribute("userId");
    verifyLobbyOwner(id,requesterId);

    try {
      Lobby response = lobbyService.removeUserFromLobby(userId, id);
    } catch (LobbyServiceException e) {
      handleLobbyServiceException(e);
    }
    return ResponseEntity.ok().build();

  }

  @PostMapping("/api/lobby/{id}/promote/{userId}")
  public <T> ResponseEntity<T> promotePlayer(
      @PathVariable("id") Integer id,
      @PathVariable("userId") Integer userId, //To be kicked
      @Autowired HttpSession session) {

    LOG.info("hit /api/lobby/" + id + "/promote/" + userId);

    //Is the current user authorized to perform this action?
    Integer requesterId = (Integer) session.getAttribute("userId");
    verifyLobbyOwner(id,requesterId);

    try {
      Lobby response = lobbyService.transferLobbyOwnership(id,requesterId,userId);
    } catch (LobbyServiceException e) {
      handleLobbyServiceException(e);
    }
    return ResponseEntity.ok().build();

  }

  @PostMapping("/api/lobby/{id}/leave")
  public <T> ResponseEntity<T> leaveLobby(
      @PathVariable("id") Integer id,
      @Autowired HttpSession session) {

    LOG.info("hit /api/lobby/" + id + "/leave");

    Integer userId = (Integer) session.getAttribute("userId");

    try { 
      Lobby response = lobbyService.removeUserFromLobby(userId, id);
    } catch (LobbyServiceException e) {
      handleLobbyServiceException(e);
    }
    
    return ResponseEntity.ok().build();

  }

  @PostMapping("/api/lobby/{id}/update")
    public <T> ResponseEntity<T> updateLobby(
        @PathVariable("id") Integer id,
        @Valid @RequestBody UpdateLobbyDTO updateLobbyDTO,
        @Autowired HttpSession session,
        @Autowired BindingResult bindingResult) {

      LOG.info("hit : POST /api/lobby/"+id+"/update");

      if ( bindingResult.hasErrors() ) {
          LOG.info(String.format("Invalid Request Body"));
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "WRONG_BODY");
      }

      Integer userId = (Integer) session.getAttribute("userId");
      verifyLobbyOwner(id,userId);

      try {
        Lobby response = lobbyService.update(id,updateLobbyDTO);
      } catch (LobbyServiceException e) {
        handleLobbyServiceException(e);
      }

      return ResponseEntity.ok().build();

    }

  @PostMapping("/api/lobby/create")
    public Lobby  createLobby(
        @Autowired HttpSession session) {

      LOG.info("hit : POST /api/lobby/create");

      Integer userId = (Integer) session.getAttribute("userId");
      User user = userService.getUser(userId);
      if ( user.guest ) {
        LOG.warn(String.format("Guest %d is trying to create a lobby."));
        throw new ResponseStatusException(HttpStatus.CONFLICT, "GUEST_CANT_CREATE_LOBBY");
      }

      try {

        Lobby response = lobbyService.createNew(userId);
        return response;

      } catch (LobbyServiceException e) {

        handleLobbyServiceException(e);

      }

      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "");

    }

  @DeleteMapping("/api/lobby/{id}")
    public <T> ResponseEntity<T> deleteLobby(
        @PathVariable("id") Integer id,
        @Autowired HttpSession session) {

      LOG.info("hit : DELETE /api/lobby/"+id);

      Integer userId = (Integer) session.getAttribute("userId");
      verifyLobbyOwner(id,userId);

      try {
        Boolean response = lobbyService.delete(id);
      } catch (LobbyServiceException e) {
        handleLobbyServiceException(e);
      }

      return ResponseEntity.ok().build();

    }


  // I really wanted to allow my controller routes to throw LobbServiceExceptions
  // and transform them into rethrown ResponseStatusException so I can filter
  // and modify what errors get sent to the client without engineering a custom
  // error response. However, this can't work the way I want. The default mechanism
  // to handle ResponseStatusException (ResponseStatusExceptionHandler) listens on scope
  // of controller methods. 
  //
  // A method of class with @ExceptionHandler exists outside the scope, that is
  // a ResponseStatusException is thrown inside a custom @ExceptionHandler does not
  // get caught by the default mechanism.
  //
  // I could write my own custom errror response using ResponseEntity, however
  // I want to leverage springs (more so boot) builtin error response. So we
  // opt for try catch and explict handler invocation.
  //
  // We could also find a way to register the default implementation 
  // @ResponseStatusExceptionHandler as a @ExceptionHandler in the proper scope
  // but currently I am fed up with this task am moving on with the above solution.
  public ResponseEntity<?> handleLobbyServiceException(LobbyServiceException e) {

    switch (e.error) {
      case LOBBY_FULL:
        throw new ResponseStatusException(HttpStatus.CONFLICT, "LOBBY_IS_FULL");
      case LOBBY_PRIVATE:
        throw new ResponseStatusException(HttpStatus.CONFLICT, "LOBBY_IS_PRIVATE");
      case GUEST_NOT_IMPLEMENT:
        throw new ResponseStatusException(HttpStatus.CONFLICT, "GUEST_NOT_IMPLEMENT");
      case USER_NOT_IN_LOBBY:
        throw new ResponseStatusException(HttpStatus.CONFLICT, "NOT_IN_LOBBY");
      case CAPACITY_SHORTENING:
        throw new ResponseStatusException(HttpStatus.CONFLICT, "CAPACITY_SHORTENING_CONFLICT");
      case ERROR:
      default:
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR");
    }

  }

  private LobbyDTO lobbyToLobbyDTO(Lobby lobby) {

      LobbyDTO lobbyDto = new LobbyDTO(lobby);
      User owner = userService.getUser(lobby.owner.getId());

      List<Integer> userIds = lobby.users.stream()
        .map( x -> x.user.getId() )
        .collect(Collectors.toList());
      List<User> users = userService.getUsers(userIds);

      List<LobbyUserDTO> userDtos = users.stream().
        map( x -> new LobbyUserDTO(x))
        .collect(Collectors.toList());

      lobbyDto.owner = new LobbyUserDTO(owner);
      lobbyDto.users = userDtos;

      return lobbyDto;
    }


  /*
   * Verify that the user is the owner of this lobby,
   * if not throw an exception
   */
  private void verifyLobbyOwner(Integer lobbyId,Integer userId) {

      Integer ownerId = lobbyService.getLobbyOwnerId(lobbyId);

      if ( ownerId == null ) {
          throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR");
      }
      if ( !ownerId.equals(userId) ) {
          LOG.info(String.format("Non Lobby Owner %d is trying to manipulate lobby %d",userId,lobbyId));
          throw new ResponseStatusException(HttpStatus.FORBIDDEN, "NOT_AUTHORIZED");
      }

  }

}
