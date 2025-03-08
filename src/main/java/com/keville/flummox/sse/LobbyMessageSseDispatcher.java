package com.keville.flummox.sse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.data.relational.core.mapping.event.AfterSaveEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;

import com.keville.flummox.model.lobby.LobbyMessage;
import com.keville.flummox.service.exceptions.EntityNotFound;
import com.keville.flummox.sse.context.LobbyMessageContext;


@Component
public class LobbyMessageSseDispatcher extends SseDispatcher<LobbyMessageContext> {

    private static final Logger LOG = LoggerFactory.getLogger(LobbyMessageSseDispatcher.class);

    public LobbyMessageSseDispatcher() {}

    @EventListener
    public void handleLobbyMessageSave(AfterSaveEvent<Object> event) {

      if ( !event.getType().equals(LobbyMessage.class) ) {
        return;
      }

      LobbyMessage message = (LobbyMessage) event.getEntity();

      try {

        SseEventBuilder sseEvent = SseEmitter.event()
          .id(String.valueOf(message.id))
          .name("update")
          .data(1); //breaks if I don't send data, not sure why...

        sseMap
          .entrySet()
          .stream()
          .filter( entry -> { return entry.getKey().lobbyId == message.lobby.getId(); })
          .map( entry -> entry.getValue() )
          .forEach( emitter -> tryEmitEvent(emitter, sseEvent) );


      } catch (EntityNotFound e) {

        LOG.error(String.format("unable to create SSEs for lobby %d 's message event",message.lobby.getId()));
        LOG.error(e.getMessage());

      }

    }

}
