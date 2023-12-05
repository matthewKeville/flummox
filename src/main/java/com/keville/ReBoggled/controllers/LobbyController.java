package com.keville.ReBoggled.controllers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.keville.ReBoggled.DTO.LobbyDTO;
import com.keville.ReBoggled.DTO.LobbyUserDTO;
import com.keville.ReBoggled.model.Lobby;
import com.keville.ReBoggled.model.User;
import com.keville.ReBoggled.service.LobbyService;
import com.keville.ReBoggled.service.UserService;
import com.keville.ReBoggled.util.Conversions;

import jakarta.servlet.http.HttpSession;

@RestController
public class LobbyController {

    private static final Logger LOG = LoggerFactory.getLogger(LobbyController.class);

    @Autowired
    private LobbyService lobbyService;

    @Autowired
    private UserService userService;

    public LobbyController(@Autowired LobbyService lobbies) {
      this.lobbyService = lobbies;
    }

    @GetMapping(value = {"/lobby", "/"})
    public ModelAndView test() {
      //return the lobby template 
      ModelAndView modelAndView = new ModelAndView();
      modelAndView.setViewName("lobby");
      return modelAndView;
    }

    private LobbyDTO lobbyToLobbyDTO(Lobby lobby) {

      LobbyDTO lobbyDto = new LobbyDTO(lobby);
      User owner = userService.getUser(lobby.getOwner().getId());

      List<Integer> userIds = lobby.getUsers().stream()
        .map( x -> x.id )
        .collect(Collectors.toList());
      List<User> users = userService.getUsers(userIds);

      List<LobbyUserDTO> userDtos = users.stream().
        map( x -> new LobbyUserDTO(x))
        .collect(Collectors.toList());

      lobbyDto.owner = new LobbyUserDTO(owner);
      lobbyDto.users = userDtos;

      return lobbyDto;
    }

    @GetMapping("/api/lobby")
    //public Iterable<Lobby> test(
    public Iterable<LobbyDTO> test(
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
    public LobbyDTO test(
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

}
