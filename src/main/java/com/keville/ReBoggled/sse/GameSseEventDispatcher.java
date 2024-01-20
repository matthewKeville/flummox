package com.keville.ReBoggled.sse;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.relational.core.mapping.event.AfterSaveEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;

import com.keville.ReBoggled.DTO.GameUserViewDTO;
import com.keville.ReBoggled.model.game.Game;
import com.keville.ReBoggled.service.gameService.GameService;
import com.keville.ReBoggled.service.gameService.GameServiceException;
import com.keville.ReBoggled.service.view.GameViewService;
import com.keville.ReBoggled.service.view.GameViewServiceException;

@Component
public class GameSseEventDispatcher extends SseEventDispatcher {

    private static final Logger LOG = LoggerFactory.getLogger(GameSseEventDispatcher.class);

    private Map<Integer,Map<Integer,SseEmitter>> gameEmitters = new HashMap<Integer,Map<Integer,SseEmitter>>();
    private GameService gameService;
    private GameViewService gameViewService;

    public GameSseEventDispatcher(@Autowired GameService gameService,
        @Autowired GameViewService  gameViewService) {
      this.gameService = gameService;
      this.gameViewService = gameViewService;
    }

    public void register(Integer gameId,Integer userId,SseEmitter emitter) {

      Map<Integer,SseEmitter> gameUserEmitters;

      if ( gameEmitters.containsKey(gameId) ) {

        gameUserEmitters = gameEmitters.get(gameId);

      } else {

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
          LOG.info(String.format("no more emitters registered for game %d",gameId));
        }

      } else {

        LOG.error("can't remove emitter because it's not in the map for game " + gameId);

      }

    }

    @EventListener
    public void handleGameUpdateEvent(AfterSaveEvent<Object> event) {

      if ( !event.getType().equals(Game.class) ) {
        return;
      }
      Game game = (Game) event.getEntity();
      LOG.info(String.format("caught game after save event game.id is %d",game.id));

      if ( !gameEmitters.containsKey( game.id ) ) {
        return;
      }

      Map<Integer,SseEmitter> emitters = gameEmitters.get(game.id);
      for ( Entry<Integer,SseEmitter> entry : emitters.entrySet() ) {
        try { 

          GameUserViewDTO gameView = gameViewService.getGameUserViewDTO(game.id,entry.getKey());
          SseEventBuilder updateEventBuilder = SseEmitter.event()
            .id(String.valueOf(game.id))
            .name("game_change")
            .data(gameView);

          String failMessage = "Couldn't send game_change for game : " + game.id + " for user " + entry.getKey();
          tryEmitEvent(entry.getValue(),updateEventBuilder,failMessage);

        } catch (GameViewServiceException | GameServiceException e) {
          LOG.error(String.format("unable to create SSEs for game %d 's update event for user %d",game.id,entry.getKey()));
          LOG.error(e.getMessage());
        }

      }

    }

}