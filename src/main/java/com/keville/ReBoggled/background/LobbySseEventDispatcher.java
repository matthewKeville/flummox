package com.keville.ReBoggled.background;

import java.util.Collection;
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

import com.keville.ReBoggled.DTO.LobbyViewDTO;
import com.keville.ReBoggled.model.lobby.Lobby;
import com.keville.ReBoggled.service.lobbyService.LobbyGameStartEvent;
import com.keville.ReBoggled.service.lobbyService.LobbyService;
import com.keville.ReBoggled.service.view.LobbyViewService;
import com.keville.ReBoggled.service.view.LobbyViewService.LobbyViewServiceException;

@Component
public class LobbySseEventDispatcher {

    private static final Logger LOG = LoggerFactory.getLogger(LobbySseEventDispatcher.class);
    private Map<Integer,Set<SseEmitter>> lobbyEmitters = new HashMap<Integer,Set<SseEmitter>>();
    private Map<Integer,Lobby> lobbyCache = new HashMap<Integer,Lobby>();

    private LobbyService lobbyService;
    private LobbyViewService lobbyViewService;

    public LobbySseEventDispatcher(@Autowired LobbyService lobbyService,@Autowired LobbyViewService lobbyViewService) {
      this.lobbyService = lobbyService;
      this.lobbyViewService = lobbyViewService;
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

      LOG.error(String.format("registered new emitter for lobby %d",lobbyId));

    }

  @EventListener
  public void handleLobbyStartEvent(LobbyGameStartEvent event) {

    LOG.info("caught LobbyGameStartEvent lobby.id " + event.getLobbyId());
    Integer lobbyId = event.getLobbyId();
    if ( !lobbyEmitters.containsKey( lobbyId ) ) {
      return;
    }

    Set<SseEmitter> emitters = lobbyEmitters.get(lobbyId);
    LOG.info("found " + emitters.size() + " emitters for lobby " + lobbyId);

    SseEventBuilder lobbyStartEvent = SseEmitter.event()
      .id(String.valueOf(lobbyId))
      .name("lobby_start")
      .data(""); //breaks if I provide nothing

    String failMessage = "Couldn't send lobby_start for lobby : "  + lobbyId;
    tryEmitEvents(emitters, lobbyStartEvent, failMessage);

  }
  
  @EventListener
  public void handleLobbyUpdateEvent(AfterSaveEvent<Object> event) {

    if ( !event.getType().equals(Lobby.class) ) {
      return;
    }
    Lobby lobby = (Lobby) event.getEntity();
    LOG.info(String.format("caught lobby after save event lobby.id is %d",lobby.id));

    if ( !lobbyEmitters.containsKey( lobby.id ) ) {
      return;
    }
    Set<SseEmitter> emitters = lobbyEmitters.get(lobby.id);
    LOG.info("found " + emitters.size() + " emitters for lobby " + lobby.id);

    try {

      LobbyViewDTO lobbyView = lobbyViewService.getLobbyViewDTO(lobby.id);

      SseEventBuilder updateEventBuilder = SseEmitter.event()
        .id(String.valueOf(lobby.id))
        .name("lobby_change")
        .data(lobbyView);

      String failMessage = "Couldn't send lobby_change for lobby : "  + lobby.id;
      tryEmitEvents(emitters, updateEventBuilder, failMessage);

    } catch (LobbyViewServiceException e) {
      LOG.error(String.format("unable to create SSEs for lobby %d 's update event",lobby.id));
      LOG.error(e.getMessage());
    }

  }

  private void emitEvent(SseEmitter sseEmitter,SseEventBuilder sseEvent) {

    try {

      LOG.info("dispatching event " + sseEvent.toString() + " to emitter " + sseEmitter.toString());
      sseEmitter.send(sseEvent);

    } catch (Exception e) {

      LOG.error(String.format("unexpected error dispatching events for emitter " + sseEmitter.toString()));
      LOG.error(e.getMessage());
      sseEmitter.completeWithError(e);

    }
  }

  private void tryEmitEvent(SseEmitter sseEmitter,SseEventBuilder sseEvent,String pulseFailMessage) {
    if (EventDispatchUtil.ssePulseCheck(sseEmitter,pulseFailMessage)) {
      emitEvent(sseEmitter,sseEvent);
    }
  }

  private void tryEmitEvents(Collection<SseEmitter> sseEmitters,SseEventBuilder sseEvent,String pulseFailMessage) {
    sseEmitters.forEach( emitter -> {
      tryEmitEvent(emitter, sseEvent,pulseFailMessage);
    });
  }

}
