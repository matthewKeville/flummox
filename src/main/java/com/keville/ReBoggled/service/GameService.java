package com.keville.ReBoggled.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.keville.ReBoggled.model.game.Game;
import com.keville.ReBoggled.model.lobby.Lobby;
import com.keville.ReBoggled.repository.GameRepository;
import com.keville.ReBoggled.service.exceptions.GameServiceException;
import com.keville.ReBoggled.service.exceptions.GameServiceException.GameServiceError;

@Component
public class GameService {

    private static final Logger LOG = LoggerFactory.getLogger(GameService.class);
    private GameRepository games;

    public GameService(@Autowired GameRepository games) {
      this.games = games;
    }

    public Game getGame(int id) throws GameServiceException {
      Game game = findGameById(id);
      return game;
    }

    public Iterable<Game> getGames() {
      return games.findAll();
    }

    public Game createGame(Lobby lobby) throws GameServiceException {
      Game game = new Game(lobby.gameSettings);
      games.save(game);
      return game;
    }

    private Game findGameById(Integer gameId) throws GameServiceException {

      Optional<Game>  optGame = games.findById(gameId);
      if ( optGame.isEmpty() ) {
        LOG.warn(String.format("No such game %d",gameId));
        throw new GameServiceException(GameServiceError.GAME_NOT_FOUND);
      }
      return optGame.get();
    }

}
