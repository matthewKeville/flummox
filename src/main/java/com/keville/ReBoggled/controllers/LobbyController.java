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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import com.keville.ReBoggled.DTO.LobbyDTO;
import com.keville.ReBoggled.DTO.LobbyUserDTO;
import com.keville.ReBoggled.DTO.UpdateLobbyDTO;
import com.keville.ReBoggled.model.Lobby;
import com.keville.ReBoggled.model.User;
import com.keville.ReBoggled.repository.LobbyRepository;
import com.keville.ReBoggled.service.LobbyService;
import com.keville.ReBoggled.service.UserService;
import com.keville.ReBoggled.service.LobbyService.AddUserToLobbyResponse;
import com.keville.ReBoggled.service.LobbyService.RemoveUserFromLobbyResponse;
import com.keville.ReBoggled.service.LobbyService.UpdateLobbyResponse;
import com.keville.ReBoggled.util.Conversions;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@RestController
public class LobbyController {

    private static final Logger LOG = LoggerFactory.getLogger(LobbyController.class);

    @Autowired
    private LobbyService lobbyService;

    @Autowired
    private LobbyRepository lobbies; ;

    @Autowired
    private UserService userService;

    public LobbyController(@Autowired LobbyService lobbies) {
      this.lobbyService = lobbies;
    }

    @GetMapping(value = {"/lobby", "/"})
    public ModelAndView view() {
      ModelAndView modelAndView = new ModelAndView();
      modelAndView.setViewName("lobby");
      return modelAndView;
    }


    @GetMapping("/api/lobby")
    //public Iterable<Lobby> test(
    public Iterable<LobbyDTO> getLobbies(
        @RequestParam(required = false, name = "publicOnly") boolean publicOnly,
        HttpSession session) {

      LOG.info("hit /api/lobby");

      Iterable<Lobby> lobbies = lobbyService.getLobbies();
      List<LobbyDTO> lobbyDTOs = Conversions.iterableToList(lobbies).stream()
        .map( lobby -> lobbyToLobbyDTO(lobby ))
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
    public <T> ResponseEntity<T> joinLobby(
        @PathVariable("id") Integer id,
        @Autowired HttpSession session) {


      LOG.info("hit /api/lobby/"+id+"/join");
      Integer userId = (Integer) session.getAttribute("userId");
      if ( userId == null ) {
          LOG.warn("unable to identify the userId of the current Session");
          throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR");
      }

      AddUserToLobbyResponse response = lobbyService.addUserToLobby(userId,id);

      switch ( response ) {

        case LOBBY_FULL:
          throw new ResponseStatusException(HttpStatus.CONFLICT, "LOBBY_IS_FULL");
        case LOBBY_PRIVATE:
          throw new ResponseStatusException(HttpStatus.CONFLICT, "LOBBY_IS_PRIVATE");
        case GUEST_NOT_IMPLEMENT:
          throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, "GUEST_NOT_IMPLEMENTED");
        case SUCCESS:
          return ResponseEntity.ok().build();
        case ERROR:
          throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR");
        default:
          throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR");

      }

    }

    @PostMapping("/api/lobby/{id}/leave")
    public <T> ResponseEntity<T> leaveLobby(
        @PathVariable("id") Integer id,
        @Autowired HttpSession session) {

      LOG.info("hit /api/lobby/"+id+"/leave");

      Integer userId = (Integer) session.getAttribute("userId");
      RemoveUserFromLobbyResponse response = lobbyService.removeUserFromLobby(userId,id);

      switch ( response ) {
        case SUCCESS:
          return ResponseEntity.ok().build();
        case USER_NOT_IN_LOBBY:
          throw new ResponseStatusException(HttpStatus.CONFLICT, "NOT_IN_LOBBY");
        case ERROR:
        default:
          throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR");
      }
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

      Integer ownerId = lobbyService.getLobbyOwnerId(id);
      if ( ownerId == null ) {
          throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR");
      }
      if ( !ownerId.equals(userId) ) {
          LOG.info(String.format("Non Lobby owner %d is trying to update lobby %d",userId,id));
          throw new ResponseStatusException(HttpStatus.FORBIDDEN, "NOT_AUTHORIZED");
      }

      UpdateLobbyResponse response = lobbyService.update(id,updateLobbyDTO);

      switch ( response ) {
        case SUCCESS:
          return ResponseEntity.ok().build();
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

}
