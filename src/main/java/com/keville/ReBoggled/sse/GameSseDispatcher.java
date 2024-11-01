package com.keville.ReBoggled.sse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.relational.core.mapping.event.AfterSaveEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;

import com.keville.ReBoggled.DTO.GameDTO;
import com.keville.ReBoggled.model.game.Game;
import com.keville.ReBoggled.service.gameService.GameService;
import com.keville.ReBoggled.service.gameService.GameServiceException;
import com.keville.ReBoggled.sse.context.GameContext;

@Component
public class GameSseDispatcher extends SseDispatcher<GameContext> {

    private static final Logger LOG = LoggerFactory.getLogger(GameSseDispatcher.class);
    private GameService gameService;

    public GameSseDispatcher(@Autowired GameService gameService) {
      this.gameService = gameService;
    }

    @Override
    protected void sendInitialPayload(SseEmitter emitter,GameContext context) {
      try {

        GameDTO gameDTO = gameService.getGameDTO(context.gameId,context.userId);

        SseEventBuilder sseEvent = SseEmitter.event()
          .id(String.valueOf(0))
          .name("init")
          .data(gameDTO);

        tryEmitEvent(emitter,sseEvent);

      } catch (GameServiceException e) {

        LOG.error(String.format("Caught error dispatching init payload for game %d for user %d",context.gameId,context.userId));
        LOG.error(e.getMessage());

      }
    }

    @EventListener
    public void handleGameUpdateEvent(AfterSaveEvent<Object> event) {

      if ( !event.getType().equals(Game.class) ) {
        return;
      }
      Game game = (Game) event.getEntity();

      sseMap
        .entrySet()
        .stream()
        .filter( entry -> { return entry.getKey().gameId == game.id; })
        .forEach ( entry -> {

          try { 

            GameDTO gameView = gameService.getGameDTO(game.id,entry.getKey().userId);

            SseEventBuilder sseEvent = SseEmitter.event()
              .id(String.valueOf(game.id))
              .name("update")
              .data(gameView);

            tryEmitEvent(entry.getValue(),sseEvent);

          } catch (GameServiceException e) {

            LOG.error(String.format("Caught error dispatching update for game %d for user %d",entry.getKey().gameId,entry.getKey().userId));
            LOG.error(e.getMessage());

          }
          
        });

    }

}
