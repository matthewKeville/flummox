package com.keville.ReBoggled.service.gameService;

import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.keville.ReBoggled.model.game.BoardGenerationException;
import com.keville.ReBoggled.model.game.Game;
import com.keville.ReBoggled.model.game.GameAnswer;
import com.keville.ReBoggled.model.game.GameSettings;
import com.keville.ReBoggled.model.lobby.Lobby;
import com.keville.ReBoggled.model.user.User;
import com.keville.ReBoggled.repository.GameRepository;
import com.keville.ReBoggled.repository.UserRepository;
import com.keville.ReBoggled.service.answerService.AnswerService;
import com.keville.ReBoggled.service.boardGenerationService.BoardGenerationService;
import com.keville.ReBoggled.service.gameService.GameServiceException.GameServiceError;

@Component
public class DefaultGameService implements GameService {

    private static final Logger LOG = LoggerFactory.getLogger(GameService.class);
    private GameRepository games;
    private UserRepository users;

    private BoardGenerationService boardGenerationService;

    private AnswerService answerService;

    public DefaultGameService(@Autowired GameRepository games,
        @Autowired UserRepository users,
        @Autowired AnswerService answerService,
        @Autowired BoardGenerationService boardGenerationService) {
      this.games = games;
      this.users = users;
      this.boardGenerationService = boardGenerationService; 
      this.answerService = answerService;
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

      GameSettings gameSettings = lobby.gameSettings;
      Game game = new Game();

      game.findRule = gameSettings.findRule;
      game.duration = gameSettings.duration;

      try {

        game.board = boardGenerationService.generate(gameSettings.boardSize,gameSettings.boardTopology,gameSettings.tileRotation);

        game.start = LocalDateTime.now();
        game.end = game.start.plusSeconds(gameSettings.duration);

        games.save(game);

      } catch ( BoardGenerationException bge ) {
        LOG.error(" Board generation failed for game " + game.id );
        throw new GameServiceException(GameServiceError.BOARD_GENERATION_FAILURE);
      }

      return game;

    }

    public Game addGameAnswer(Integer gameId, Integer userId, String userAnswer) throws GameServiceException {

      final String answer = userAnswer.toUpperCase();

      Game game = findGameById(gameId);
      User user = findUserById(userId);

      //TODO : does the user belong to this game? : requires associating users to Game table
      
      //Is the game ongoing?
      if ( LocalDateTime.now().isAfter(game.end) ) {
        LOG.warn(String.format("user %d trying to submit answer for finished game %d",userId,gameId));
        throw new GameServiceException(GameServiceError.GAME_OVER);
      }
      
      //Does this word exist in the solution space?
      if ( !answerService.isValidWord(answer,game) ) {
        LOG.trace(String.format(" answer %s is not correct for game %d",answer,game.id));
        throw new GameServiceException(GameServiceError.INVALID_ANSWER);
      }
     
      //Did this player already find this word?
      if ( game.answers.contains(new GameAnswer(userId,answer))) {
        LOG.trace(String.format(" answer %s is already found for user %d",answer,user.id));
        throw new GameServiceException(GameServiceError.ANSWER_ALREADY_FOUND);
      }

      game.answers.add(new GameAnswer(userId,answer));
      games.save(game);
      return game;

    }


    //FIXME : duplicate code for LobbyService.isOutated, candidate for refactor
    public boolean isOutdated(Integer gameId,LocalDateTime lastTime) throws GameServiceException {

      // do query
      Game game = findGameById(gameId);
      return game.lastModifiedDate.isAfter(lastTime);

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


}
