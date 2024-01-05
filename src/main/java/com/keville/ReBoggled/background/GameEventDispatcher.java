package com.keville.ReBoggled.background;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;

import com.keville.ReBoggled.DTO.GameUserViewDTO;
import com.keville.ReBoggled.model.game.Game;
import com.keville.ReBoggled.service.GameService;
import com.keville.ReBoggled.service.exceptions.GameViewServiceException;
import com.keville.ReBoggled.service.view.GameViewService;

@Component
public class GameEventDispatcher implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(LobbyEventDispatcher.class);

    private Map<Integer,Map<Integer,SseEmitter>> gameEmitters = new HashMap<Integer,Map<Integer,SseEmitter>>();
    private Map<Integer,Game> gameCache = new HashMap<Integer,Game>();
    private GameService gameService;
    private GameViewService gameViewService;

    private TaskExecutor taskExecutor;

    public GameEventDispatcher(@Autowired GameService gameService,
        @Autowired GameViewService  gameViewService,
        @Autowired ThreadPoolTaskExecutor taskExecutor) {
      this.taskExecutor = taskExecutor;
      this.taskExecutor.execute(this);
      this.gameService = gameService;
      this.gameViewService = gameViewService;
    }

    public void register(Integer gameId,Integer userId,SseEmitter emitter) {

      Map<Integer,SseEmitter> gameUserEmitters;

      //is there a map for this game?
      
      if ( gameEmitters.containsKey(gameId) ) {

        gameUserEmitters = gameEmitters.get(gameId);

      } else {

        //create map for this game

        if (!gameService.exists(gameId)) {
          LOG.error(String.format("unable to register emitter, game %d doesn't exist",gameId));
          return;
        }

        gameUserEmitters = new HashMap<Integer,SseEmitter>();
        gameEmitters.put(gameId,gameUserEmitters);

      }
      
      gameUserEmitters.put(userId,emitter);

      LOG.info(String.format("registered new emitter for game %d for user %d",gameId,userId));

    }

    public void unregister(Integer gameId,Integer userId,SseEmitter emitter) {

      if ( !gameEmitters.containsKey(gameId) ) {
        LOG.error(String.format("can't unregister emitter, not set for game %d",gameId));
        return;
      }

      Map<Integer,SseEmitter> gameUserEmitters = gameEmitters.get(gameId);

      if ( gameUserEmitters.containsKey(userId) ) {

        LOG.info(String.format("unregistered emitter for user %d on game %d",userId,gameId));
        gameUserEmitters.remove(userId);

        if ( gameUserEmitters.keySet().size() == 0 ) {
          gameUserEmitters.remove(gameId);
          LOG.error(String.format("can't unregister emitter, not set for game %d",gameId));
        }

      } else {

        LOG.error("can't remove emitter because it's not in the map for game " + gameId);

      }

    }

    private void detect(Integer gameId) {

      Map<Integer,SseEmitter> gameUserEmitters = gameEmitters.get(gameId);

      LOG.trace(String.format("detecting events on game %d for %d emitters",gameId,gameUserEmitters.size()));

      Game prevGame = null;

      if ( gameCache.containsKey(gameId) ) {
        prevGame = gameCache.get(gameId);
      } 

      try {

        //has the game changed?
        LOG.trace("checking if game cache exists");

        if ( prevGame == null ) {
          Game game = gameService.getGame(gameId);
          gameCache.put(gameId,game);
          return;
        }

        LOG.trace("checking if game cache is current");

        if ( !gameService.isOutdated(gameId,prevGame.lastModifiedDate) ) {
          return;
        }

        LOG.trace("game cache is outdated");

        Game game = gameService.getGame(gameId);
        gameCache.put(gameId,game);

        gameUserEmitters.entrySet().forEach( entry -> {

          List<SseEventBuilder> events = new LinkedList<SseEventBuilder>();

          try { 

          GameUserViewDTO gameView = gameViewService.getGameUserViewDTO(gameId,entry.getKey());

          // update event

          LOG.trace("queueing game_change");
          events.add(SseEmitter.event()
            .id(String.valueOf(gameId))
            .name("game_change")
            .data(gameView));


          //dispatch events

          events.forEach( event -> {
            SseEmitter emitter = entry.getValue();
            try {

              emitter.send(event);
            } catch (Exception e) {

              LOG.error(String.format("encountered unexpected error dispatching events for game %d",gameId));
              emitter.completeWithError(e);
            }
          });

          } catch (Exception e) {
            LOG.error(String.format("unable update sse for game %d for user %d",gameId,entry.getKey()));
          }

        });


        // construct events


      } catch (Exception e) {

        LOG.warn("unable to detect game changes " + e.getMessage());
      } 

    }

    public void run() {

        LOG.info("GameEventDispatcher started...");

        boolean shouldRun = true;

        while ( shouldRun ) {

          try {
            for ( Integer gameId : gameEmitters.keySet() ) {

              //check pulses
              Map<Integer,SseEmitter> gameUserEmitters = gameEmitters.get(gameId);
              gameUserEmitters.entrySet().forEach( entry -> {
                EventDispatchUtil.ssePulseCheck(gameUserEmitters.get(entry.getKey()),
                  String.format("pulse check failed for user %d on game %d ",entry.getKey(),gameId));
              });

              //check events
              detect(gameId);

            }
            Thread.sleep(5000);
          } catch (Exception e) {
          }

        }

    }

}
