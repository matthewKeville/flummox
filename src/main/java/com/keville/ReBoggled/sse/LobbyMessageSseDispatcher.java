package com.keville.ReBoggled.sse;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.relational.core.mapping.event.AfterSaveEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;

import com.keville.ReBoggled.DTO.LobbyMessageDTO;
import com.keville.ReBoggled.model.lobby.LobbyMessage;
import com.keville.ReBoggled.service.exceptions.EntityNotFound;
import com.keville.ReBoggled.service.lobbyService.LobbyService;
import com.keville.ReBoggled.sse.context.LobbyMessageContext;


@Component
public class LobbyMessageSseDispatcher extends SseDispatcher<LobbyMessageContext> {

    private static final Logger LOG = LoggerFactory.getLogger(LobbyMessageSseDispatcher.class);
    private LobbyService lobbyService;

    public LobbyMessageSseDispatcher(@Autowired LobbyService lobbyService) {
      this.lobbyService = lobbyService;
    }

    @Override
    protected void sendInitialPayload(SseEmitter emitter,LobbyMessageContext context) {
      try {

        List<LobbyMessageDTO> messages = lobbyService.getLobbyMessageDTOs(context.lobbyId);

        SseEventBuilder sseEvent = SseEmitter.event()
          .id(String.valueOf(0))
          .name("init")
          .data(messages);

        tryEmitEvent(emitter, sseEvent);

      } catch (EntityNotFound e) {

        LOG.error(String.format("Caught error dispatching init payload for lobby message %d",context.lobbyId));
        LOG.error(e.getMessage());

      }
    }
 
    @EventListener
    public void handleLobbyMessageSave(AfterSaveEvent<Object> event) {

      if ( !event.getType().equals(LobbyMessage.class) ) {
        return;
      }

      LobbyMessage message = (LobbyMessage) event.getEntity();

      try {

        List<LobbyMessageDTO> messages = lobbyService.getLobbyMessageDTOs(message.lobby.getId());

        SseEventBuilder sseEvent = SseEmitter.event()
          .id(String.valueOf(message.id))
          .name("update")
          .data(messages);

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
