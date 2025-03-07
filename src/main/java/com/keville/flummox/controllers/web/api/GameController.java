package com.keville.flummox.controllers.web.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.keville.flummox.DTO.GameAnswerRequestDTO;
import com.keville.flummox.DTO.GameAnswerResponseDTO;
import com.keville.flummox.DTO.GameDTO;
import com.keville.flummox.DTO.PostGameDTO;
import com.keville.flummox.model.user.User;
import com.keville.flummox.sse.GameSseDispatcher;
import com.keville.flummox.sse.context.GameContext;
import com.keville.flummox.service.gameService.GameService;

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

      GameAnswerResponseDTO response = gameService.submitGameAnswer(id,userId,gameAnswerSubmissionDTO);
      return response;
  }

  @GetMapping("/{id}/post-game/{userId}")
  public ResponseEntity<?> getPostGameDTO(
      @PathVariable("id") Integer id,
      @PathVariable("userId") Integer userId) {

    PostGameDTO postGameDTO = gameService.getPostGameDTO(id, userId);
    return new ResponseEntity<PostGameDTO>(postGameDTO,HttpStatus.OK);

  }

  @GetMapping("/{id}")
  public GameDTO  getGameDTO (
      @PathVariable("id") Integer id) {
      return gameService.getGameDTO(id);
  }

  @GetMapping("/{id}/sse/{userId}")
  public SseEmitter getSse (
      @PathVariable("id") Integer id) {

    Integer userId = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).id;
    GameContext context = new GameContext(userId, id);
    return gameSseDispatcher.register(context);

  }

}
