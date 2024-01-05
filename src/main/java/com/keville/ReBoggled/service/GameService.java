package com.keville.ReBoggled.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.keville.ReBoggled.model.game.Game;
import com.keville.ReBoggled.model.game.GameAnswer;
import com.keville.ReBoggled.model.lobby.Lobby;
import com.keville.ReBoggled.model.user.User;
import com.keville.ReBoggled.repository.GameRepository;
import com.keville.ReBoggled.repository.UserRepository;
import com.keville.ReBoggled.service.exceptions.GameServiceException;
import com.keville.ReBoggled.service.exceptions.GameServiceException.GameServiceError;

@Component
public class GameService {

    private static final Logger LOG = LoggerFactory.getLogger(GameService.class);
    private GameRepository games;
    private UserRepository users;

    public GameService(@Autowired GameRepository games,
        @Autowired UserRepository users) {
      this.games = games;
      this.users = users;
    }

    public Game getGame(int id) throws GameServiceException {
      Game game = findGameById(id);
      return game;
    }

    public Iterable<Game> getGames() {
      return games.findAll();
    }

    public boolean exists (Integer gameId) {
      return games.existsById(gameId);
    }

    public Game createGame(Lobby lobby) throws GameServiceException {
      Game game = new Game(lobby.gameSettings);
      games.save(game);
      return game;
    }

    public Game addGameAnswer(Integer gameId, Integer userId, String answer) throws GameServiceException {

      Game game = findGameById(gameId);
      User user = findUserById(userId);

      //TODO : implement game logic

      //does the user belong to this game?

      //is the game ongoing?

      //does this word exist in the solution space?
      
      //did this player already find this word?

      //is find rule UNIQUE, or FIRST?

      //FIXME : always add word for user

      game.answers.add(new GameAnswer(userId,answer));

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

    private User findUserById(Integer userId) throws GameServiceException {
      Optional<User> optUser = users.findById(userId);
      if ( !optUser.isPresent() ) {
        LOG.error(String.format("No such user %d",userId));
        throw new GameServiceException(GameServiceError.USER_NOT_FOUND);
      }
      return optUser.get();
    }

    //FIXME : duplicate code for LobbyService.isOutated, candidate for refactor
    public boolean isOutdated(Integer gameId,LocalDateTime lastTime) throws GameServiceException {

      // do query
      Game game = findGameById(gameId);
      return game.lastModifiedDate.isAfter(lastTime);

    }

}
