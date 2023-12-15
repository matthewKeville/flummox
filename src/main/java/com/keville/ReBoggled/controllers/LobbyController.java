package com.keville.ReBoggled.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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


@RestController
@RequestMapping("/api/lobby")
public class LobbyController {

  public static final String baseRoute = "/api/lobby";
  private static final Logger LOG = LoggerFactory.getLogger(LobbyController.class);

  private LobbyService lobbyService;
  private UserService userService;

  public LobbyController(@Autowired LobbyService lobbyService,
      @Autowired UserService userService) {
    this.lobbyService = lobbyService;
    this.userService = userService;
  }

  @GetMapping("")
  public ResponseEntity<?> getLobbies(
      @RequestParam(required = false, name = "publicOnly") boolean publicOnly,
      HttpSession session) {

    logReq("get","");

    try {
      List<LobbyDTO> lobbyDTOs = lobbyService.getLobbyDTOs();
      return new ResponseEntity<List<LobbyDTO>>(lobbyDTOs,HttpStatus.OK);
    } catch (LobbyServiceException e) {
      return handleLobbyServiceException(e);
    }


  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getLobby(
      @PathVariable("id") Integer id,
      @Autowired HttpSession session) {

    logReq("get","/"+id);

    try {  
      //Lobby lobby = lobbyService.getLobby(id);
      //return new ResponseEntity<LobbyDTO>(lobbyToLobbyDTO(lobby),HttpStatus.OK);
      LobbyDTO lobbyDTO = lobbyService.getLobbyDTO(id);
      return new ResponseEntity<LobbyDTO>(lobbyDTO,HttpStatus.OK);
    } catch (LobbyServiceException e) {
      return handleLobbyServiceException(e);
    }


  }

  @PostMapping("/{id}/join")
  public ResponseEntity<?> joinLobby (
      @PathVariable("id") Integer id,
      @Autowired HttpSession session) {

    logReq("post",String.format("/%d/join",id));

    Integer userId = (Integer) session.getAttribute("userId");
    if (userId == null) {
      LOG.warn("unable to identify the userId of the current Session");
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR");
    }

    try {
      Lobby lobby = lobbyService.addUserToLobby(userId, id);
      return new ResponseEntity<Lobby>(lobby,HttpStatus.OK);
    } catch (LobbyServiceException e)  {
      handleLobbyServiceException(e);
      return ResponseEntity.internalServerError().build();
    }


  }

  @PostMapping("/{id}/kick/{userId}")
  public ResponseEntity<?> kickPlayer(
      @PathVariable("id") Integer id,
      @PathVariable("userId") Integer userId, //To be kicked
      @Autowired HttpSession session) {

    logReq("post",String.format("/%d/kick/%d",id,userId));

    try {
      Integer requesterId = (Integer) session.getAttribute("userId");
      verifyLobbyOwner(id,requesterId);

      Lobby response = lobbyService.removeUserFromLobby(userId, id);
      return ResponseEntity.ok().build();
    } catch (LobbyServiceException e) {
      return handleLobbyServiceException(e);
    }

  }

  @PostMapping("/{id}/promote/{userId}")
  public ResponseEntity<?> promotePlayer(
      @PathVariable("id") Integer id,
      @PathVariable("userId") Integer userId, //To be kicked
      @Autowired HttpSession session) {

    logReq("post",String.format("/%d/promote/%d",id,userId));

    try {
      Integer requesterId = (Integer) session.getAttribute("userId");
      verifyLobbyOwner(id,requesterId);

      Lobby lobby = lobbyService.transferLobbyOwnership(id,requesterId,userId);
      return new ResponseEntity<Lobby>(lobby,HttpStatus.OK);
    } catch (LobbyServiceException e) {
      return handleLobbyServiceException(e);
    }

  }

  @PostMapping("/{id}/leave")
  public ResponseEntity<?> leaveLobby(
      @PathVariable("id") Integer id,
      @Autowired HttpSession session) {

    logReq("post",String.format("/%d/leave",id));

    Integer userId = (Integer) session.getAttribute("userId");

    try { 
      Lobby lobby = lobbyService.removeUserFromLobby(userId, id);
      return new ResponseEntity<Lobby>(lobby,HttpStatus.OK);
    } catch (LobbyServiceException e) {
      return handleLobbyServiceException(e);
    }

  }

  @PostMapping("/{id}/update")
    public ResponseEntity<?> updateLobby(
        @PathVariable("id") Integer id,
        @Valid @RequestBody UpdateLobbyDTO updateLobbyDTO,
        @Autowired HttpSession session,
        @Autowired BindingResult bindingResult) {

      logReq("post",String.format("/%d/update",id));

      if ( bindingResult.hasErrors() ) {
          LOG.info(String.format("Invalid Request Body"));
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "WRONG_BODY");
      }

      try {
        Integer userId = (Integer) session.getAttribute("userId");
        verifyLobbyOwner(id,userId);

        Lobby lobby = lobbyService.update(id,updateLobbyDTO);
        return new ResponseEntity<Lobby>(lobby,HttpStatus.OK);
      } catch (LobbyServiceException e) {
        return handleLobbyServiceException(e);
      }

    }

  @PostMapping("/create")
    public ResponseEntity<?>  createLobby(
        @Autowired HttpSession session) {

      logReq("post","/create");

      Integer userId = (Integer) session.getAttribute("userId");
      User user = userService.getUser(userId);
      if ( user.guest ) {
        LOG.warn(String.format("Guest %d is trying to create a lobby."));
        throw new ResponseStatusException(HttpStatus.CONFLICT, "GUEST_CANT_CREATE_LOBBY");
      }

      try {
        Lobby lobby = lobbyService.createNew(userId);
        return new ResponseEntity<Lobby>(lobby,HttpStatus.CREATED);
      } catch (LobbyServiceException e) {
        return handleLobbyServiceException(e);
      }

    }

  @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLobby(
        @PathVariable("id") Integer id,
        @Autowired HttpSession session) {

      logReq("delete","/"+id);

      try {
        Integer userId = (Integer) session.getAttribute("userId");
        verifyLobbyOwner(id,userId);

        lobbyService.delete(id);
        return ResponseEntity.ok().build();
      } catch (LobbyServiceException e) {
        return handleLobbyServiceException(e);
      }

    }


  /*
  I really wanted to allow my controller routes to throw LobbServiceExceptions
  and transform them into rethrown ResponseStatusException so I can filter
  and modify what errors get sent to the client without engineering a custom
  error response. However, this can't work the way I want. The default mechanism
  to handle ResponseStatusException (ResponseStatusExceptionHandler) listens on scope
  of controller methods. 

  A method of class with @ExceptionHandler exists outside the scope, that is
  a ResponseStatusException is thrown inside a custom @ExceptionHandler does not
  get caught by the default mechanism.

  I could write my own custom errror response using ResponseEntity, however
  I want to leverage springs (more so boot) builtin error response. So we
  opt for try catch and explict handler invocation.

  We could also find a way to register the default implementation 
  @ResponseStatusExceptionHandler as a @ExceptionHandler in the proper scope
  but currently I am fed up with this task am moving on with the above solution.

  FIXME : This is very hacky 

  The way I have controller methods returning
  a ResponseEntity<?> , but if they fail they catch and return 
  handleLobbyServiceException(e) with the false expectation of ever getting
  a ResponseEntity<?> when this method will always throw and be handled by 
  springs default ResponseStatusException handler.
  */
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

  /*
   * Verify that the user is the owner of this lobby,
   * if not throw an exception
   */
  private void verifyLobbyOwner(Integer lobbyId,Integer userId) throws LobbyServiceException {

      Integer ownerId = lobbyService.getLobbyOwnerId(lobbyId);

      if ( ownerId == null ) {
          throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR");
      }

      if ( !ownerId.equals(userId) ) {
          LOG.info(String.format("Non Lobby Owner %d is trying to manipulate lobby %d",userId,lobbyId));
          throw new ResponseStatusException(HttpStatus.FORBIDDEN, "NOT_AUTHORIZED");
      }

  }

  private void logReq(String type,String route) {
    type = type.toUpperCase();
    LOG.info(type + "\t" + baseRoute + route);
  }

}
