package com.keville.ReBoggled.controllers.web.api;

import java.io.IOException;

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
import com.keville.ReBoggled.DTO.GameUserSummaryDTO;
import com.keville.ReBoggled.DTO.PostGameUserSummaryDTO;
import com.keville.ReBoggled.sse.GameSseEventDispatcher;
import com.keville.ReBoggled.controllers.util.RequestLogger;
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
  private RequestLogger rlog = new RequestLogger(baseRoute,LOG);

  private GameService gameService;
  private GameSseEventDispatcher gameSseEventDispatcher;

  public GameController(@Autowired GameService gameService,
      @Autowired GameSseEventDispatcher gameSseEventDispatcher) {
    this.gameService = gameService;
    this.gameSseEventDispatcher = gameSseEventDispatcher;
  }

  @GetMapping("")
  public ResponseEntity<?> getGames(HttpSession session) {

    rlog.log("get","");

    Iterable<Game> games = gameService.getGames();
    return new ResponseEntity<Iterable<Game>>(games,HttpStatus.OK);

  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getGame(
      @PathVariable("id") Integer id,
      @Autowired HttpSession session) {

    rlog.log("get","/"+id);

    try {  
      Game game = gameService.getGame(id);
      return new ResponseEntity<Game>(game,HttpStatus.OK);
    } catch (GameServiceException e) {
      return handleGameServiceException(e);
    }

  }


  @PostMapping("/{id}/answer")
  public ResponseEntity<?> answer (
      @PathVariable("id") Integer id,
      @Valid @RequestBody GameAnswerSubmissionDTO gameAnswerSubmissionDTO,
      @Autowired HttpSession session) {

    rlog.log("post",String.format("/%d/answer",id));

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

  /*
  @GetMapping("/{id}/view/user")
  public ResponseEntity<?> getUserView (
      @PathVariable("id") Integer id,
      @Autowired HttpSession session) {

    logReq("get","/"+id+"/view/user");

    Integer userId = (Integer) session.getAttribute("userId");
    if (userId == null) {
      LOG.warn("unable to identify the userId of the current Session");
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR");
    }

    try {  
      GameUserViewDTO gameUserViewDTO = gameViewService.getGameUserViewDTO(id, userId);
      return new ResponseEntity<GameUserViewDTO>(gameUserViewDTO,HttpStatus.OK);
    } catch (GameViewServiceException e) {
      return handleGameViewServiceException(e);
    } catch (GameServiceException e) {
      return handleGameServiceException(e);
    }
    
  }
  */

  @GetMapping("/{id}/summary")
  public ResponseEntity<?> getGameUserSummary (
      @PathVariable("id") Integer id,
      @Autowired HttpSession session) {

    logReq("get","/"+id+"/summary");

    Integer userId = (Integer) session.getAttribute("userId");
    if (userId == null) {
      LOG.warn("unable to identify the userId of the current Session");
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR");
    }

    try {  
      GameUserSummaryDTO gameUserSummaryDTO = gameService.getGameUserSummary(id, userId);
      return new ResponseEntity<GameUserSummaryDTO>(gameUserSummaryDTO,HttpStatus.OK);
    } catch (GameServiceException e) {
      return handleGameServiceException(e);
    }
    
  }

  @GetMapping("/{id}/summary/post")
  public ResponseEntity<?> getPostGameUserSummary(@PathVariable("id") Integer id,
      HttpSession session) {

    logReq("get",id+"/view/user/summary");

    Integer userId = (Integer) session.getAttribute("userId");
    if (userId == null) {
      LOG.warn("unable to identify the userId of the current Session");
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR");
    }

    try { 
      PostGameUserSummaryDTO postGameUserSummaryDTO = gameService.getPostGameUserSummary(id, userId);
      return new ResponseEntity<PostGameUserSummaryDTO>(postGameUserSummaryDTO,HttpStatus.OK);
    } catch (GameServiceException e) {
      return handleGameServiceException(e);
    }

  }

  /*
  @GetMapping("/{id}/view/user/summary")
  public ResponseEntity<?> getUserViewSummary(@PathVariable("id") Integer id,
      HttpSession session) {

    logReq("get",id+"/view/user/summary");

    Integer userId = (Integer) session.getAttribute("userId");
    if (userId == null) {
      LOG.warn("unable to identify the userId of the current Session");
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR");
    }

    try { 
      PostGameUserViewDTO postGameViewDTO = gameViewService.getPostGameUserViewDTO(id, userId);
      return new ResponseEntity<PostGameUserViewDTO>(postGameViewDTO,HttpStatus.OK);
    } catch (GameServiceException e) {
      return handleGameServiceException(e);
    }

  }
  */

  /* Register a Server Side Event Emitter to communicate changes to the game model for
   * the user registered. (In-Game) */
  @GetMapping("/{id}/summary/sse")
  public SseEmitter getGameSSEForUser (
      @PathVariable("id") Integer id,
      @Autowired HttpSession session) {

    logReq("get","/"+id+"/view/user/sse");

    Integer userId = (Integer) session.getAttribute("userId");
    if (userId == null) {
      LOG.warn("unable to identify the userId of the current Session");
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR");
    }

    //assemble emitter

    SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

    Runnable cleanup = () -> {
      LOG.info("cleaning up game sse emitter");
      gameSseEventDispatcher.unregister(id, userId, emitter);
    };

    emitter.onError(      (ex)  -> {
      LOG.info(id+"/view/user/sse error ");
      if ( ex instanceof IOException ) {
        LOG.info("IOException caught, likely client destroyed event source ...");
        LOG.info(ex.getMessage());
      } else {
        LOG.warn("Unexpected error ...");
        LOG.error(ex.getMessage());
      }
      cleanup.run();
    });

    emitter.onCompletion( ()    -> {
      LOG.info(id+"/view/user/sse completed");
      cleanup.run();
    });

    // wire to dispatcher
    gameSseEventDispatcher.register(id,userId,emitter);

    return emitter;

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

  private void logReq(String type,String route) {
    type = type.toUpperCase();
    LOG.info(type + "\t" + baseRoute + route);
  }

}
