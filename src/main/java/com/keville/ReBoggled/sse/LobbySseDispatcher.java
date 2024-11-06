package com.keville.ReBoggled.sse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.relational.core.mapping.event.AfterSaveEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;

import com.keville.ReBoggled.DTO.LobbyDTO;
import com.keville.ReBoggled.events.GameEndEvent;
import com.keville.ReBoggled.events.StartLobbyEvent;
import com.keville.ReBoggled.model.lobby.Lobby;
import com.keville.ReBoggled.service.exceptions.EntityNotFound;
import com.keville.ReBoggled.service.lobbyService.LobbyService;
import com.keville.ReBoggled.sse.context.LobbyContext;

@Component
public class LobbySseDispatcher extends SseDispatcher<LobbyContext> {

    private static final Logger LOG = LoggerFactory.getLogger(LobbySseDispatcher.class);
    private LobbyService lobbyService;

    public LobbySseDispatcher(@Autowired LobbyService lobbyService) {
      this.lobbyService = lobbyService;
    }

    @Override
    protected void sendInitialPayload(SseEmitter emitter,LobbyContext context) {
      try {

        LobbyDTO lobbySummary = lobbyService.getLobbyDTO(context.lobbyId);

        SseEventBuilder sseEvent = SseEmitter.event()
          .id(String.valueOf(0))
          .name("init")
          .data(lobbySummary);

        tryEmitEvent(emitter, sseEvent);

      } catch (EntityNotFound e) {

        LOG.error(String.format("Caught error dispatching init payload for lobby %d",context.lobbyId));
        LOG.error(e.getMessage());

      }
    }
  
    @EventListener
    public void handleLobbyUpdate(AfterSaveEvent<Object> event) {

      if ( !event.getType().equals(Lobby.class) ) {
        return;
      }

      Lobby lobby = (Lobby) event.getEntity();

      try {

        LobbyDTO lobbySummary = lobbyService.getLobbyDTO(lobby.id);

        SseEventBuilder sseEvent = SseEmitter.event()
          .id(String.valueOf(lobby.id))
          .name("update")
          .data(lobbySummary);

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

        LobbyDTO lobbySummary = lobbyService.getLobbyDTO(event.lobbyId);

        SseEventBuilder sseEvent = SseEmitter.event()
          .id(String.valueOf(event.lobbyId))
          .name("game_start")
          .data(lobbySummary);

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

        LobbyDTO lobbySummary = lobbyService.getLobbyDTO(event.lobbyId);

        SseEventBuilder sseEvent = SseEmitter.event()
          .id(String.valueOf(event.lobbyId))
          .name("game_end")
          .data(lobbySummary);

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
