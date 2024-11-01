package com.keville.ReBoggled.controllers.web.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.keville.ReBoggled.DTO.GameAnswerSubmissionDTO;
import com.keville.ReBoggled.DTO.PostGameDTO;
import com.keville.ReBoggled.sse.GameSseDispatcher;
import com.keville.ReBoggled.sse.context.GameContext;
import com.keville.ReBoggled.model.game.Game;
import com.keville.ReBoggled.service.gameService.GameService;
import com.keville.ReBoggled.service.gameService.GameServiceException;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/game")
public class GameController {

  public static final String baseRoute = "/api/game";
  private static final Logger LOG = LoggerFactory.getLogger(GameController.class);

  private GameService gameService;
  private GameSseDispatcher gameSseDispatcher;

  public GameController(@Autowired GameService gameService,
      @Autowired GameSseDispatcher gameSseDispatcher) {
    this.gameService = gameService;
    this.gameSseDispatcher = gameSseDispatcher;
  }

  @GetMapping("")
  public ResponseEntity<?> getGames(HttpSession session) {

    Iterable<Game> games = gameService.getGames();
    return new ResponseEntity<Iterable<Game>>(games,HttpStatus.OK);

  }

  @PostMapping("/{id}/answer")
  public ResponseEntity<?> answer (
      @PathVariable("id") Integer id,
      @Valid @RequestBody GameAnswerSubmissionDTO gameAnswerSubmissionDTO,
      @Autowired HttpSession session) {

    Integer userId = (Integer) session.getAttribute("userId");
    if (userId == null) {
      LOG.warn("unable to identify the userId of the current Session");
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR");
    }

    try {
      Game game = gameService.addGameAnswer(id, userId, gameAnswerSubmissionDTO.answer);
      return new ResponseEntity<Game>(game,HttpStatus.OK);
    } catch (GameServiceException e)  {
      handleGameServiceException(e);
      return ResponseEntity.internalServerError().build();
    }


  }

  @GetMapping("/{id}/post")
  public ResponseEntity<?> getPostGameDTO(@PathVariable("id") Integer id,
      HttpSession session) {

    Integer userId = (Integer) session.getAttribute("userId");
    if (userId == null) {
      LOG.warn("unable to identify the userId of the current Session");
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR");
    }

    try { 
      PostGameDTO postGameDTO = gameService.getPostGameDTO(id, userId);
      return new ResponseEntity<PostGameDTO>(postGameDTO,HttpStatus.OK);
    } catch (GameServiceException e) {
      return handleGameServiceException(e);
    }

  }

  @GetMapping("/{id}/sse")
  public SseEmitter getGameSSEForUser (
      @PathVariable("id") Integer id,
      @Autowired HttpSession session) {

    Integer userId = (Integer) session.getAttribute("userId");
    if (userId == null) {
      LOG.warn("unable to identify the userId of the current Session");
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR");
    }

    GameContext context = new GameContext(userId, id);
    return gameSseDispatcher.register(context);

  }

  public ResponseEntity<?> handleGameServiceException(GameServiceException e) {

    switch (e.error) {
      case GAME_NOT_FOUND:
        throw new ResponseStatusException(HttpStatus.CONFLICT, "UNKNOWN_GAME");
      case INVALID_ANSWER:
        throw new ResponseStatusException(HttpStatus.CONFLICT, "INVALID_ANSWER");
      case ANSWER_ALREADY_FOUND:
        throw new ResponseStatusException(HttpStatus.CONFLICT, "ANSWER_ALREADY_FOUND");
      case GAME_OVER:
        throw new ResponseStatusException(HttpStatus.CONFLICT, "GAME_OVER");
      case ERROR:
      default:
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR");
    }

  }

}
