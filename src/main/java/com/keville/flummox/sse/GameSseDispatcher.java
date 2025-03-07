package com.keville.flummox.sse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.data.relational.core.mapping.event.AfterSaveEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;

import com.keville.flummox.model.game.Game;
import com.keville.flummox.service.exceptions.EntityNotFound;
import com.keville.flummox.service.exceptions.NotAuthorized;
import com.keville.flummox.sse.context.GameContext;

@Component
public class GameSseDispatcher extends SseDispatcher<GameContext> {

    private static final Logger LOG = LoggerFactory.getLogger(GameSseDispatcher.class);

    public GameSseDispatcher() {}

    //depracated
    @Override
    protected void sendInitialPayload(SseEmitter emitter,GameContext context) {
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
            SseEventBuilder sseEvent = SseEmitter.event()
              .id(String.valueOf(game.id))
              .name("update")
              .data(1);
            tryEmitEvent(entry.getValue(),sseEvent);
          } catch (EntityNotFound|NotAuthorized e) {
            LOG.error(String.format("Caught error dispatching update for game %d for user %d",entry.getKey().gameId,entry.getKey().userId));
            LOG.error(e.getMessage());
          }
          
        });

    }

}
