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

import com.keville.ReBoggled.DTO.GameAnswerRequestDTO;
import com.keville.ReBoggled.DTO.GameAnswerResponseDTO;
import com.keville.ReBoggled.DTO.PostGameDTO;
import com.keville.ReBoggled.sse.GameSseDispatcher;
import com.keville.ReBoggled.sse.context.GameContext;
import com.keville.ReBoggled.service.gameService.GameService;

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

  @PostMapping("/{id}/answer/{userId}")
  public GameAnswerResponseDTO answer (
      @PathVariable("id") Integer id,
      @PathVariable("userId") Integer userId,
      @Valid @RequestBody GameAnswerRequestDTO gameAnswerSubmissionDTO) {

      GameAnswerResponseDTO response = gameService.submitGameAnswer(id,userId,gameAnswerSubmissionDTO.answer);
      return response;
  }

  @GetMapping("/{id}/post")
  public ResponseEntity<?> getPostGameDTO(@PathVariable("id") Integer id,
      HttpSession session) {

    Integer userId = (Integer) session.getAttribute("userId");
    if (userId == null) {
      LOG.warn("unable to identify the userId of the current Session");
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR");
    }

    PostGameDTO postGameDTO = gameService.getPostGameDTO(id, userId);
    return new ResponseEntity<PostGameDTO>(postGameDTO,HttpStatus.OK);

  }

  @GetMapping("/{id}/sse")
  public SseEmitter getGameSSEForUser (
      @PathVariable("id") Integer id,
      @Autowired HttpSession session) {

    Integer userId = (Integer) session.getAttribute("userId");
    GameContext context = new GameContext(userId, id);
    return gameSseDispatcher.register(context);

  }

}
