package com.keville.flummox.sse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.data.relational.core.mapping.event.AfterSaveEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;

import com.keville.flummox.events.GameEndEvent;
import com.keville.flummox.events.StartLobbyEvent;
import com.keville.flummox.model.lobby.Lobby;
import com.keville.flummox.service.exceptions.EntityNotFound;
import com.keville.flummox.sse.context.LobbyContext;

@Component
public class LobbySseDispatcher extends SseDispatcher<LobbyContext> {

    private static final Logger LOG = LoggerFactory.getLogger(LobbySseDispatcher.class);

    public LobbySseDispatcher() {}

    //deprecated
    @Override
    protected void sendInitialPayload(SseEmitter emitter,LobbyContext context) {
    }
  
    @EventListener
    public void handleLobbyUpdate(AfterSaveEvent<Object> event) {

      if ( !event.getType().equals(Lobby.class) ) {
        return;
      }

      Lobby lobby = (Lobby) event.getEntity();

      try {

        SseEventBuilder sseEvent = SseEmitter.event()
          .id(String.valueOf(lobby.id))
          .name("update")
          .data(0);

        sseMap
          .entrySet()
          .stream()
          .filter( entry -> { return entry.getKey().lobbyId == lobby.id; })
          .map( entry -> entry.getValue() )
          .forEach( emitter -> tryEmitEvent(emitter, sseEvent) );

      } catch (EntityNotFound e) {

        LOG.error(String.format("Caught error dispatching events for lobby %d's update",lobby.id));
        LOG.error(e.getMessage());

      }

    }

    @EventListener
    public void handleLobbyStart(StartLobbyEvent event) {

      try {

        SseEventBuilder sseEvent = SseEmitter.event()
          .id(String.valueOf(event.lobbyId))
          .name("game_start")
          .data(0);

        sseMap
          .entrySet()
          .stream()
          .filter( entry -> { return entry.getKey().lobbyId == event.lobbyId; })
          .map( entry -> entry.getValue() )
          .forEach( emitter -> tryEmitEvent(emitter, sseEvent) );

      } catch (EntityNotFound e) {

        LOG.error(String.format("Caught error dispatching events for lobby %d's game_start event",event.lobbyId));
        LOG.error(e.getMessage());

      }
    }

    @EventListener
    public void handleGameEnd(GameEndEvent event) {

      try {

        SseEventBuilder sseEvent = SseEmitter.event()
          .id(String.valueOf(event.lobbyId))
          .name("game_end")
          .data(0);

        sseMap
          .entrySet()
          .stream()
          .filter( entry -> { return entry.getKey().lobbyId == event.lobbyId; })
          .map( entry -> entry.getValue() )
          .forEach( emitter -> tryEmitEvent(emitter, sseEvent) );

      } catch (EntityNotFound e) {

        LOG.error(String.format("Caught error dispatching events for lobby %d's game_end event",event.lobbyId));
        LOG.error(e.getMessage());

      }
    }
}
