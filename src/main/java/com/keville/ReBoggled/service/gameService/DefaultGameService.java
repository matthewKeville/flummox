package com.keville.ReBoggled.service.gameService;

import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.keville.ReBoggled.model.game.Game;
import com.keville.ReBoggled.model.game.GameAnswer;
import com.keville.ReBoggled.model.game.GameFactory;
import com.keville.ReBoggled.model.lobby.Lobby;
import com.keville.ReBoggled.model.user.User;
import com.keville.ReBoggled.repository.GameRepository;
import com.keville.ReBoggled.repository.UserRepository;
import com.keville.ReBoggled.service.answerService.AnswerService;
import com.keville.ReBoggled.service.gameService.GameServiceException.GameServiceError;

@Component
public class DefaultGameService implements GameService {

    private static final Logger LOG = LoggerFactory.getLogger(GameService.class);
    private GameRepository games;
    private UserRepository users;
    private GameFactory gameFactory;

    private AnswerService answerService;

    public DefaultGameService(@Autowired GameRepository games,
        @Autowired UserRepository users,
        @Autowired AnswerService answerService,
        @Autowired GameFactory gameFactory) {
      this.games = games;
      this.users = users;
      this.gameFactory = gameFactory;
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
      Game game = gameFactory.getGame(lobby.gameSettings);
      games.save(game);
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

      //TODO : For record keeping purposes it makes more sense to always record player entries regardless of correctness
      //or adherence to the rule. This allows us to field for guessing, or cheating. For now we do this online implementation
      //of these rules, but more correct is storing everything and computing the results at the end.
      //
      //The  downside of above is the need to create a system that knows when a game has ended to calculate the final result.
      //This could probably be achieved with a background service that watches when games end, and computes a game summary.
      //This allows us to still be passive and detached in our design. The service fulfills one purpose, creating game summaries.

      //GameCompletionWatcher . startGame register to GCW . GCW will watch until games end ..

      //Apply find rule
      switch ( game.gameSettings.findRule ) {
        case ANY:
          break;
        case FIRST:
          if ( ! game.answers.stream().anyMatch( ga -> { return ga.answer.equalsIgnoreCase(answer); } ) ) {
            LOG.trace(String.format(" answer %s has already been found by another user",answer));
            throw new GameServiceException(GameServiceError.ANSWER_ALREADY_FOUND);
          }
          break;
        case UNIQUE:
          if ( game.answers.stream().anyMatch( ga -> { return ga.answer.equalsIgnoreCase(answer); } ) ) {
            LOG.trace(String.format(" answer %s has already been found by another user",answer));
            // remove this answer from others players
            game.answers.removeIf( ga -> { return ga.answer.equalsIgnoreCase(answer); });
            throw new GameServiceException(GameServiceError.ANSWER_ALREADY_FOUND);
          }
          break;
      }

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
