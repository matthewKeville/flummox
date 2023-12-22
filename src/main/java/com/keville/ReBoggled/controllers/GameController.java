package com.keville.ReBoggled.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.keville.ReBoggled.controllers.util.RequestLogger;
import com.keville.ReBoggled.model.game.Game;
import com.keville.ReBoggled.service.GameService;
import com.keville.ReBoggled.service.exceptions.GameServiceException;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/game")
public class GameController {

  public static final String baseRoute = "/api/game";
  private static final Logger LOG = LoggerFactory.getLogger(GameController.class);
  private RequestLogger rlog = new RequestLogger(baseRoute,LOG);
  private GameService gameService;

  public GameController(@Autowired GameService gameService) {
    this.gameService = gameService;
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


  /*
  @PostMapping("/{id}/answer")
  public ResponseEntity<?> answer (
      @PathVariable("id") Integer id,
      @Autowired HttpSession session) {

    rlog.log("post",String.format("/%d/join",id));

    Integer userId = (Integer) session.getAttribute("userId");
    if (userId == null) {
      LOG.warn("unable to identify the userId of the current Session");
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR");
    }

    try {
      //Lobby lobby = lobbyService.addUserToLobby(userId, id);
      return new ResponseEntity<Lobby>(lobby,HttpStatus.OK);
    } catch (LobbyServiceException e)  {
      handleLobbyServiceException(e);
      return ResponseEntity.internalServerError().build();
    }


  }
  */

  //https://www.baeldung.com/spring-server-sent-events
  /*
  @GetMapping("/{id}/view/lobby/sse")
  public SseEmitter getLobbySSE(
      @PathVariable("id") Integer id,
      @Autowired HttpSession session) {

    logReq("get","/"+id+"/view/lobby/sse");

    SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

    //emitter.onCompletion( ()    -> LOG.info(id+"/view/lobby/sse completed") );
    emitter.onTimeout(    ()    -> LOG.info(id+"/view/lobby/sse timed out"));
    emitter.onError(      (ex)  -> {
      LOG.info(id+"/view/lobby/sse error ");
      if ( ex instanceof IOException ) {
        LOG.info("IOException caught, likely client destroyed event source ...");
      } else {
        LOG.warn("Unexpected error ...");
        LOG.error(ex.getMessage());
      }
    });

    ExecutorService  sseMvcExecutor = Executors.newSingleThreadExecutor();

    sseMvcExecutor.execute( () -> {

      try {

        boolean shouldRun = true;

        LobbyViewDTO lobby = lobbyViewService.getLobbyViewDTO(id);

        while ( shouldRun ) {

          boolean outdated = lobbyService.isOutdated(id,lobby.lastModifiedDate);

          if ( outdated ) {

            LOG.info("lobby " + id + " has updated, resending ");
            lobby = lobbyViewService.getLobbyViewDTO(id);

            SseEventBuilder event = SseEmitter.event()
              .id(String.valueOf(id))
              .name("lobby change")
              .data(lobby);
            emitter.send(event);
          }

          Thread.sleep(5000);

        }

        emitter.complete();

      } catch (Exception e) {
        emitter.completeWithError(e);
      }

    });

    return emitter;

  }
  */

  public ResponseEntity<?> handleGameServiceException(GameServiceException e) {

    switch (e.error) {
      case GAME_NOT_FOUND:
        throw new ResponseStatusException(HttpStatus.CONFLICT, "UNKNOWN_GAME");
      case ERROR:
      default:
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR");
    }

  }

}
