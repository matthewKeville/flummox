package com.keville.ReBoggled.background;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;

import com.keville.ReBoggled.DTO.LobbyViewDTO;
import com.keville.ReBoggled.model.lobby.Lobby;
import com.keville.ReBoggled.service.LobbyService;
import com.keville.ReBoggled.service.view.LobbyViewService;

@Component
public class LobbyEventDispatcher implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(LobbyEventDispatcher.class);
    private Map<Integer,Set<SseEmitter>> lobbyEmitters = new HashMap<Integer,Set<SseEmitter>>();
    private Map<Integer,Lobby> lobbyCache = new HashMap<Integer,Lobby>();

    private LobbyService lobbyService;
    private LobbyViewService lobbyViewService;
    private TaskExecutor taskExecutor;

    public LobbyEventDispatcher(@Autowired LobbyService lobbyService,@Autowired LobbyViewService lobbyViewService,@Autowired ThreadPoolTaskExecutor taskExecutor) {
      this.lobbyService = lobbyService;
      this.lobbyViewService = lobbyViewService;
      this.taskExecutor = taskExecutor;
      this.taskExecutor.execute(this);
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

    public void run() {

        LOG.info("LobbyEventDispatcher started...");

        boolean shouldRun = true;

        while ( shouldRun ) {

          try {
            for ( Integer lobbyId : lobbyEmitters.keySet() ) {
              pulseCheck(lobbyId);
              //FIXME : In the future, this should be an active system
              //where this class subscribes to events from the EventService
              detect(lobbyId);
            }
            Thread.sleep(5000);
          } catch (Exception e) {
          }

        }

    }

    /* Calculate events & dispatch to all registered emitters 
     * @PreCondition emitters.containsKey(lobbyId)
    */
    private void detect(Integer lobbyId) {

      Set<SseEmitter> emitters = lobbyEmitters.get(lobbyId);

      LOG.trace(String.format("detecting events on lobby %d for %d emitters",lobbyId,emitters.size()));

      Lobby prevLobby = null;

      if ( lobbyCache.containsKey(lobbyId) ) {
        prevLobby = lobbyCache.get(lobbyId);
      } 

      try {

        if ( prevLobby == null ) {
          Lobby lobby = lobbyService.getLobby(lobbyId);
          lobbyCache.put(lobbyId,lobby);
          return;
        }

        if ( !lobbyService.isOutdated(lobbyId,prevLobby.lastModifiedDate) ) {
          return;
        }

        LOG.trace("lobby cache is outdated");

        Lobby lobby = lobbyService.getLobby(lobbyId);
        lobbyCache.put(lobbyId,lobby);

        // construct events

        List<SseEventBuilder> events = new LinkedList<SseEventBuilder>();

        LobbyViewDTO lobbyView = lobbyViewService.getLobbyViewDTO(lobbyId);

        // update event

        LOG.trace("queueing lobby_change");
        events.add(SseEmitter.event()
          .id(String.valueOf(lobbyId))
          .name("lobby_change")
          .data(lobbyView));


        //other events
       
        if ( 
              prevLobby.game != null && !prevLobby.game.getId().equals(lobby.game.getId()) ||
              prevLobby.game == null && lobby.game != null )
        {

          LOG.info("queueing lobby_start");
          events.add(SseEmitter.event()
            .id(String.valueOf(lobbyId))
            .name("lobby_start")
            .data("")); //breaks if I provide nothing
        }

        //dispatch events

        events.forEach( event -> {
          emitters.forEach( emitter -> {

            try {

              emitter.send(event);

            } catch (Exception e) {

              LOG.error(String.format("encountered unexpected error dispatching events for lobby %d",lobbyId));
              emitter.completeWithError(e);

            }

          });
        });


      } catch (Exception e) {

        LOG.warn("unable to detect lobby changes " + e.getMessage());

      }


    }

    /*
     * Send a pulse event to all emitters for a given lobby.
     * If they fail, we assume the client's event source is gone and we should dispose
     * of the emitter.
     */
    private void pulseCheck(Integer lobbyId) {

      Set<SseEmitter> emitters = lobbyEmitters.get(lobbyId);

      LOG.trace(String.format("detecting dead listeners on lobby %d",lobbyId));

      LOG.trace("queueing lobby_pulse");
      SseEventBuilder event = SseEmitter.event()
        .id(String.valueOf(lobbyId))
        .name("lobby_change");

      emitters.forEach ( emitter -> {

        try {
          emitter.send(event);
        } catch (IOException e) {
          LOG.info(String.format("pulse check failed for emitter on lobby %d",lobbyId));
          emitter.completeWithError(e);
        }

      });

    }

}
