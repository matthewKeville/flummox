package com.keville.ReBoggled.sse;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.keville.ReBoggled.service.lobbyService.LobbyService;
import com.keville.ReBoggled.service.lobbyService.LobbyServiceException;

//code duplication w/ LobbySseEventDispatcher

@Component
public class LobbyMessageSseDispatcher extends SseEventDispatcher {

    private static final Logger LOG = LoggerFactory.getLogger(LobbyMessageSseDispatcher.class);
    private Map<Integer,Set<SseEmitter>> lobbyMessageEmitters = new HashMap<Integer,Set<SseEmitter>>();
    private LobbyService lobbyService;

    public LobbyMessageSseDispatcher(@Autowired LobbyService lobbyService) {
      this.lobbyService = lobbyService;
    }

    private void sendInitPayload(int lobbyId,SseEmitter emitter) {
      try {

        List<LobbyMessageDTO> messages = lobbyService.getLobbyMessageDTOs(lobbyId);

        SseEventBuilder newMessageEventBuilder = SseEmitter.event()
          .id(String.valueOf(0))
          .name("init")
          .data(messages);

        String failMessage = "Couldn't send initial messages for lobby : " + lobbyId;
        tryEmitEvent(emitter, newMessageEventBuilder, failMessage);

      } catch (LobbyServiceException e) {

        LOG.error("Couldn't send initial messages for lobby : " + lobbyId);
        LOG.error(e.getMessage());

      }
    }

    public void unregister(Integer lobbyId,SseEmitter emitter) {

      if ( !lobbyMessageEmitters.containsKey(lobbyId) ) {
        LOG.error(String.format("can't unregister emitter, not set for lobby message %d",lobbyId));
        return;
      }

      Set<SseEmitter> emitters = lobbyMessageEmitters.get(lobbyId);

      if ( emitters.contains(emitter) ) {

        emitters.remove(emitter);

        if ( emitters.isEmpty() ) {
          lobbyMessageEmitters.remove(lobbyId);
        }

      } else {

        LOG.error("can't remove emitter because it's not in the set ");

      }

    }

    public void register(Integer lobbyId,SseEmitter emitter) {

      Set<SseEmitter> emitters;

      if ( lobbyMessageEmitters.containsKey(lobbyId) ) {

        emitters = lobbyMessageEmitters.get(lobbyId);

      } else {

        if (!lobbyService.exists(lobbyId)) {
          LOG.error(String.format("unable to register emitter, lobby %d doesn't exist",lobbyId));
          return;
        }

        emitters = new HashSet<SseEmitter>();
        lobbyMessageEmitters.put(lobbyId,emitters);

      }

      emitters.add(emitter);
      sendInitPayload(lobbyId, emitter);

      LOG.error(String.format("registered new emitter for lobby messages %d",lobbyId));

    }
 
  //Listen for new lobby messages
  @EventListener
  public void handleLobbyMessageSave(AfterSaveEvent<Object> event) {

    if ( !event.getType().equals(LobbyMessage.class) ) {
      return;
    }

    LobbyMessage message = (LobbyMessage) event.getEntity();

    if ( !lobbyMessageEmitters.containsKey( message.lobby.getId() ) ) {
      return;
    }

    Set<SseEmitter> emitters = lobbyMessageEmitters.get(message.lobby.getId());

    try {

      List<LobbyMessageDTO> messages = lobbyService.getLobbyMessageDTOs(message.lobby.getId());

      SseEventBuilder newMessageEventBuilder = SseEmitter.event()
        .id(String.valueOf(message.id))
        .name("update")
        .data(messages);

      String failMessage = "Couldn't send update for lobby : "  + message.id;
      tryEmitEvents(emitters, newMessageEventBuilder, failMessage);

    } catch (LobbyServiceException e) {
      LOG.error(String.format("unable to create SSEs for lobby %d 's message event",message.lobby.getId()));
      LOG.error(e.getMessage());
    }

  }

}
