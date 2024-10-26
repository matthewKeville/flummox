package com.keville.ReBoggled.sse;

import java.util.HashMap;
import java.util.HashSet;
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

import com.keville.ReBoggled.DTO.LobbySummaryDTO;
import com.keville.ReBoggled.events.GameEndEvent;
import com.keville.ReBoggled.events.StartLobbyEvent;
import com.keville.ReBoggled.model.lobby.Lobby;
import com.keville.ReBoggled.service.lobbyService.LobbyService;
import com.keville.ReBoggled.service.lobbyService.LobbyServiceException;

@Component
public class LobbySseEventDispatcher extends SseEventDispatcher {

    private static final Logger LOG = LoggerFactory.getLogger(LobbySseEventDispatcher.class);
    private Map<Integer,Set<SseEmitter>> lobbyEmitters = new HashMap<Integer,Set<SseEmitter>>();
    private LobbyService lobbyService;

    public LobbySseEventDispatcher(@Autowired LobbyService lobbyService) {
      this.lobbyService = lobbyService;
    }

    private void sendInitPayload(int lobbyId,SseEmitter emitter) {
      try {

        LobbySummaryDTO lobbySummary = lobbyService.getLobbySummaryDTO(lobbyId);

        SseEventBuilder newMessageEventBuilder = SseEmitter.event()
          .id(String.valueOf(0))
          .name("init")
          .data(lobbySummary);

        String failMessage = "Couldn't send initial data for lobby : " + lobbyId;
        tryEmitEvent(emitter, newMessageEventBuilder, failMessage);

      } catch (LobbyServiceException e) {

        LOG.error("Couldn't send initial data for lobby : " + lobbyId);
        LOG.error(e.getMessage());

      }
    }

    public void unregister(Integer lobbyId,SseEmitter emitter) {

      if ( !lobbyEmitters.containsKey(lobbyId) ) {
        LOG.error(String.format("can't unregister emitter, not set for lobby %d",lobbyId));
        return;
      }

      Set<SseEmitter> emitters = lobbyEmitters.get(lobbyId);

      if ( emitters.contains(emitter) ) {

        emitters.remove(emitter);

        if ( emitters.isEmpty() ) {
          lobbyEmitters.remove(lobbyId);
        }

      } else {

        LOG.error("can't remove emitter because it's not in the set ");

      }

    }

    public void register(Integer lobbyId,SseEmitter emitter) {

      Set<SseEmitter> emitters;

      if ( lobbyEmitters.containsKey(lobbyId) ) {

        emitters = lobbyEmitters.get(lobbyId);

      } else {

        if (!lobbyService.exists(lobbyId)) {
          LOG.error(String.format("unable to register emitter, lobby %d doesn't exist",lobbyId));
          return;
        }

        emitters = new HashSet<SseEmitter>();
        lobbyEmitters.put(lobbyId,emitters);

      }

      emitters.add(emitter);
      sendInitPayload(lobbyId, emitter);

      LOG.error(String.format("registered new emitter for lobby %d",lobbyId));

    }
  
  @EventListener
  public void handleLobbyUpdateEvent(AfterSaveEvent<Object> event) {

    if ( !event.getType().equals(Lobby.class) ) {
      return;
    }
    Lobby lobby = (Lobby) event.getEntity();
    LOG.info(String.format("caught lobby after save event lobby.id is %d",lobby.id));

    if ( !lobbyEmitters.containsKey( lobby.id ) ) {
      LOG.info("lobby emitters doesn't have key : " + lobby.id);
      return;
    }
    Set<SseEmitter> emitters = lobbyEmitters.get(lobby.id);
    LOG.info("found " + emitters.size() + " emitters for lobby " + lobby.id);

    try {

      LobbySummaryDTO lobbySummary = lobbyService.getLobbySummaryDTO(lobby.id);

      SseEventBuilder updateEventBuilder = SseEmitter.event()
        .id(String.valueOf(lobby.id))
        .name("update")
        .data(lobbySummary);

      String failMessage = "Couldn't send update for lobby : "  + lobby.id;
      tryEmitEvents(emitters, updateEventBuilder, failMessage);

    } catch (LobbyServiceException e) {
      LOG.error(String.format("unable to create SSEs for lobby %d 's update",lobby.id));
      LOG.error(e.getMessage());
    }

  }

  @EventListener
  public void handleLobbyStart(StartLobbyEvent event) {

    LOG.info(String.format("caught lobby start event lobby id %d",event.lobbyId));

    if ( !lobbyEmitters.containsKey( event.lobbyId ) ) {
      LOG.info("lobby emitters doesn't have key : " + event.lobbyId);
      return;
    }
    Set<SseEmitter> emitters = lobbyEmitters.get(event.lobbyId);
    LOG.info("found " + emitters.size() + " emitters for lobby " + event.lobbyId);

    try {

      LobbySummaryDTO lobbySummary = lobbyService.getLobbySummaryDTO(event.lobbyId);

      SseEventBuilder updateEventBuilder = SseEmitter.event()
        .id(String.valueOf(event.lobbyId))
        .name("game_start")
        .data(lobbySummary);

      String failMessage = "Couldn't send lobby_change for lobby : "  + event.lobbyId;
      tryEmitEvents(emitters, updateEventBuilder, failMessage);

    } catch (LobbyServiceException e) {
      LOG.error(String.format("unable to create SSEs for lobby %d 's update event",event.lobbyId));
      LOG.error(e.getMessage());
    }
  }

  @EventListener
  public void handleGameEnd(GameEndEvent event) {

    LOG.info(String.format("caught game end event for lobby id %d",event.lobbyId));

    if ( !lobbyEmitters.containsKey( event.lobbyId ) ) {
      LOG.info("lobby emitters doesn't have key : " + event.lobbyId);
      return;
    }
    Set<SseEmitter> emitters = lobbyEmitters.get(event.lobbyId);
    LOG.info("found " + emitters.size() + " emitters for lobby " + event.lobbyId);

    try {

      LobbySummaryDTO lobbySummary = lobbyService.getLobbySummaryDTO(event.lobbyId);

      SseEventBuilder updateEventBuilder = SseEmitter.event()
        .id(String.valueOf(event.lobbyId))
        .name("game_end")
        .data(lobbySummary);

      String failMessage = "Couldn't send lobby_change for lobby : "  + event.lobbyId;
      tryEmitEvents(emitters, updateEventBuilder, failMessage);

    } catch (LobbyServiceException e) {
      LOG.error(String.format("unable to create SSEs for lobby %d 's update event",event.lobbyId));
      LOG.error(e.getMessage());
    }
  }

}
