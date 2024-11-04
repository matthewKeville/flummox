package com.keville.ReBoggled.controllers.web.api;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.keville.ReBoggled.DTO.LobbyNewMessageDTO;
import com.keville.ReBoggled.DTO.LobbySummaryDTO;
import com.keville.ReBoggled.DTO.LobbyUpdateDTO;
import com.keville.ReBoggled.sse.LobbyMessageSseDispatcher;
import com.keville.ReBoggled.sse.LobbySseDispatcher;
import com.keville.ReBoggled.sse.context.LobbyContext;
import com.keville.ReBoggled.sse.context.LobbyMessageContext;
import com.keville.ReBoggled.model.lobby.LobbyUpdate;
import com.keville.ReBoggled.model.lobby.Lobby;
import com.keville.ReBoggled.service.exceptions.BadRequest;
import com.keville.ReBoggled.service.lobbyService.LobbyService;
import com.keville.ReBoggled.service.userService.UserService;

import jakarta.servlet.http.HttpSession;
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

  @GetMapping("")
  public Iterable<Lobby> getLobbies(HttpSession session) {
    return lobbyService.getLobbies();
  }

  @GetMapping("/{id}")
  public Lobby getLobby(
      @PathVariable("id") Integer id) {
    return lobbyService.getLobby(id);
  }

  // TODO : Check if the user participates in the lobby
  @GetMapping("/{id}/messages/sse")
  public SseEmitter getLobbyMessageSSE(
      @PathVariable("id") Integer id,
      @Autowired HttpSession session) {

    Integer userId = getSessionUserId(session);
    LobbyMessageContext context = new LobbyMessageContext(userId, id);
    return lobbyMessageSseDispatcher.register(context);

  }

  // TODO : Check if the user participates in the lobby
  @GetMapping("/{id}/summary/sse")
  public SseEmitter getLobbySSE(
      @PathVariable("id") Integer id,
      @Autowired HttpSession session) {

    Integer userId = getSessionUserId(session);
    LobbyContext context = new LobbyContext(userId, id);
    return lobbySseDispatcher.register(context);

  }

  @PostMapping("/{id}/messages")
  public Lobby addLobbyMessage(
      @PathVariable("id") Integer id,
      @Valid @RequestBody LobbyNewMessageDTO lobbyNewMessageDTO,
      @Autowired HttpSession session,
      @Autowired BindingResult bindingResult) {

    if (bindingResult.hasErrors()) {
      LOG.info(String.format("Invalid Request Body"));
      throw new BadRequest("Invalid Message Body");
    }

    Integer userId = getSessionUserId(session);
    return lobbyService.addMessageToLobby(lobbyNewMessageDTO, id, userId);

  }

  @GetMapping("/summary")
  public Iterable<LobbySummaryDTO> getLobbyViews(
      @RequestParam(required = false, name = "publicOnly") boolean publicOnly) {
    return lobbyService.getLobbySummaryDTOs();
  }

  @GetMapping("/{id}/summary")
  public LobbySummaryDTO getLobbyView(@PathVariable("id") Integer id,
      HttpSession session) {
    return lobbyService.getLobbySummaryDTO(id);
  }

  @GetMapping("/invite")
  public String getInvite(HttpSession session) {

    Integer userId = getSessionUserId(session);
    return lobbyService.getUserInviteLink(userId);

  }

  @PostMapping("/{id}/join")
  public Lobby joinLobby(
      @PathVariable("id") Integer id,
      @Autowired HttpSession session,
      @RequestParam(required = false, name = "token") String token) {

    Integer userId = getSessionUserId(session);
    return lobbyService.addUserToLobby(userId, id, token == null ? Optional.empty() : Optional.of(token));

  }

  @PostMapping("/{id}/kick/{userId}")
  public Optional<Lobby> kickPlayer(
      @PathVariable("id") Integer id,
      @PathVariable("userId") Integer userId, // To be kicked
      @Autowired HttpSession session) {

    return lobbyService.removeUserFromLobby(userId, id, true);

  }

  @PostMapping("/{id}/promote/{userId}")
  public Lobby promotePlayer(
      @PathVariable("id") Integer id,
      @PathVariable("userId") Integer userId, // To be kicked
      @Autowired HttpSession session) {

    return lobbyService.transferLobbyOwnership(id, userId);

  }

  @PostMapping("/{id}/leave")
  public Optional<Lobby> leaveLobby(
      @PathVariable("id") Integer id,
      @Autowired HttpSession session) {

    Integer userId = getSessionUserId(session);
    return lobbyService.removeUserFromLobby(userId, id, false);

  }

  @PostMapping("/{id}/update")
  public Lobby updateLobby(
      @PathVariable("id") Integer id,
      @Valid @RequestBody LobbyUpdateDTO lobbyUpdateDTO,
      @Autowired HttpSession session,
      @Autowired BindingResult bindingResult) {

    if (bindingResult.hasErrors()) {
      LOG.info(String.format("Invalid Request Body"));
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "WRONG_BODY");
    }
    Integer userId = getSessionUserId(session);
    LobbyUpdate lobbyUpdate = new LobbyUpdate(id, lobbyUpdateDTO);
    return lobbyService.update(lobbyUpdate);

  }

  @PostMapping("/{id}/start")
  public Lobby startGame(
      @PathVariable("id") Integer id) {

    return lobbyService.startGame(id);

  }

  @PostMapping("/create")
  public Lobby createLobby(
      @Autowired HttpSession session) {

    Integer userId = getSessionUserId(session);
    return lobbyService.createNew(userId);

  }

  @DeleteMapping("/{id}")
  public boolean deleteLobby(
      @PathVariable("id") Integer id,
      @Autowired HttpSession session) {

    return lobbyService.delete(id);
  }

  private int getSessionUserId(HttpSession session) throws InternalError {
    Integer userId = (Integer) session.getAttribute("userId");
    if (userId == null) {
      LOG.warn("unable to identify the userId of the current Session");
      throw new InternalError("no userId attribute in session");
    }
    return userId;
  }

}
