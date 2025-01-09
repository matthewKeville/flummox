package com.keville.flummox.controllers.web.api;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.keville.flummox.DTO.LobbyMessageRequestDTO;
import com.keville.flummox.DTO.LobbySummaryDTO;
import com.keville.flummox.DTO.LobbyUpdateRequestDTO;
import com.keville.flummox.sse.LobbyMessageSseDispatcher;
import com.keville.flummox.sse.LobbySseDispatcher;
import com.keville.flummox.sse.context.LobbyContext;
import com.keville.flummox.sse.context.LobbyMessageContext;
import com.keville.flummox.model.user.User;
import com.keville.flummox.model.lobby.Lobby;
import com.keville.flummox.service.exceptions.BadRequest;
import com.keville.flummox.service.lobbyService.LobbyService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/lobby")
public class LobbyController {

  public static final String baseRoute = "/api/lobby";
  private static final Logger LOG = LoggerFactory.getLogger(LobbyController.class);

  private LobbyService lobbyService;

  private LobbySseDispatcher lobbySseDispatcher;
  private LobbyMessageSseDispatcher lobbyMessageSseDispatcher;

  public LobbyController(
      @Autowired LobbyService lobbyService,
      @Autowired LobbySseDispatcher lobbySseDispatcher,
      @Autowired LobbyMessageSseDispatcher lobbyMessageSseDispatcher) {

    this.lobbyService = lobbyService;
    this.lobbySseDispatcher = lobbySseDispatcher;
    this.lobbyMessageSseDispatcher = lobbyMessageSseDispatcher;
  }

  @GetMapping("/summary")
  public Iterable<LobbySummaryDTO> getLobbySummaries(
      @RequestParam(required = false, name = "publicOnly") boolean publicOnly) {
    return lobbyService.getLobbySummaryDTOs();
  }

  @GetMapping("/{id}/messages/sse")
  public SseEmitter getLobbyMessageSSE(@PathVariable("id") Integer id) {

    Integer userId = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).id;

    LobbyMessageContext context = new LobbyMessageContext(userId, id);
    return lobbyMessageSseDispatcher.register(context);

  }

  @GetMapping("/{id}/summary/sse")
  public SseEmitter getLobbySSE(@PathVariable("id") Integer id) {

    Integer userId = getUserId();
    LobbyContext context = new LobbyContext(userId, id);
    return lobbySseDispatcher.register(context);

  }

  @PostMapping("/{id}/messages")
  public Lobby addLobbyMessage(
      @PathVariable("id") Integer id,
      @Valid @RequestBody LobbyMessageRequestDTO lobbyMessageRequestDTO,
      @Autowired BindingResult bindingResult) {

    if (bindingResult.hasErrors()) {
      LOG.info(String.format("Invalid Request Body"));
      throw new BadRequest("Invalid Message Body");
    }

    return lobbyService.addMessage(id,lobbyMessageRequestDTO);

  }


  @GetMapping("/{id}/invite")
  public String getInvite(
      @PathVariable("id") Integer id) {
    return lobbyService.getInviteLink(id);
  }

  @PostMapping("/{id}/join")
  public void joinLobby(
      @PathVariable("id") Integer id,
      @RequestParam(required = false, name = "token") String token) {

    Integer userId = getUserId();
    lobbyService.join(id,token == null ? Optional.empty() : Optional.of(token));

  }

  @PostMapping("/{id}/kick/{userId}")
  public void kickPlayer(
      @PathVariable("id") Integer id,
      @PathVariable("userId") Integer userId) {
    lobbyService.kick(id, userId);
  }

  @PostMapping("/{id}/promote/{userId}")
  public void promotePlayer(
      @PathVariable("id") Integer id,
      @PathVariable("userId") Integer userId) {

    lobbyService.promote(id, userId);
  }

  @PostMapping("/{id}/leave")
  public void leaveLobby( @PathVariable("id") Integer id) {
    lobbyService.leave(id);
  }

  @PostMapping("/{id}/update")
  public Lobby updateLobby(
      @PathVariable("id") Integer id,
      @RequestBody LobbyUpdateRequestDTO lobbyUpdateRequestDTO
      ) {
    return lobbyService.update(id,lobbyUpdateRequestDTO);

  }

  @PostMapping("/{id}/start")
  public void startGame( @PathVariable("id") Integer id) {
    lobbyService.start(id);
  }

  @PostMapping("/create")
  public Integer createLobby() {
    return lobbyService.create().id;
  }

  @DeleteMapping("/{id}")
  public boolean deleteLobby(
      @PathVariable("id") Integer id) {

    return lobbyService.delete(id);
  }

  private int getUserId() {
    return ( (User) 
        SecurityContextHolder.getContext()
        .getAuthentication()
        .getPrincipal())
        .id;
  }


}
