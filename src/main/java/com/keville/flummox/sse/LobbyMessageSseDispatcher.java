package com.keville.flummox.sse;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.relational.core.mapping.event.AfterSaveEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;

import com.keville.flummox.DTO.LobbyMessageDTO;
import com.keville.flummox.model.lobby.LobbyMessage;
import com.keville.flummox.model.user.User;
import com.keville.flummox.repository.LobbyMessageRepository;
import com.keville.flummox.repository.UserRepository;
import com.keville.flummox.service.exceptions.EntityNotFound;
import com.keville.flummox.sse.context.LobbyMessageContext;


@Component
public class LobbyMessageSseDispatcher extends SseDispatcher<LobbyMessageContext> {

    private static final Logger LOG = LoggerFactory.getLogger(LobbyMessageSseDispatcher.class);
    private LobbyMessageRepository lobbyMessages;
    private UserRepository users;

    public LobbyMessageSseDispatcher(
        @Autowired LobbyMessageRepository lobbyMessages,
        @Autowired UserRepository users) {
      this.lobbyMessages = lobbyMessages;
      this.users = users;
    }

    @Override
    protected void sendInitialPayload(SseEmitter emitter,LobbyMessageContext context) {
      try {

        List<LobbyMessageDTO> messages = getLobbyMessageDTOs(context.lobbyId);

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

        List<LobbyMessageDTO> messages = getLobbyMessageDTOs(message.lobby.getId());

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

    private List<LobbyMessageDTO> getLobbyMessageDTOs(int lobbyId) {
      List<LobbyMessageDTO> messages = new ArrayList<LobbyMessageDTO>();
      for ( LobbyMessage lm : lobbyMessages.findByLobby(lobbyId) ) {
        if ( lm.user == null ) {
          //system messages
          messages.add(new LobbyMessageDTO(lm));
        } else {
          User user = users.findById(lm.user.getId()).get();
          messages.add(new LobbyMessageDTO(lm, user.username));
        }
      }
      return messages;
    }

}
