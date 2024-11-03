package com.keville.ReBoggled.service.gameService;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.stereotype.Component;

import com.keville.ReBoggled.DTO.GameAnswerDTO;
import com.keville.ReBoggled.DTO.GameDTO;
import com.keville.ReBoggled.DTO.GameWordDTO;
import com.keville.ReBoggled.DTO.PostGameDTO;
import com.keville.ReBoggled.model.game.Game;
import com.keville.ReBoggled.model.game.GameAnswer;
import com.keville.ReBoggled.model.game.GameSettings;
import com.keville.ReBoggled.model.game.GameUserReference;
import com.keville.ReBoggled.model.gameSummary.GameSummary;
import com.keville.ReBoggled.model.gameSummary.WordFinder;
import com.keville.ReBoggled.model.lobby.Lobby;
import com.keville.ReBoggled.model.lobby.LobbyUserReference;
import com.keville.ReBoggled.model.user.User;
import com.keville.ReBoggled.repository.GameRepository;
import com.keville.ReBoggled.repository.UserRepository;
import com.keville.ReBoggled.service.gameService.GameServiceException.GameServiceError;
import com.keville.ReBoggled.service.gameService.board.BoardGenerationException;
import com.keville.ReBoggled.service.gameService.board.BoardGenerator;
import com.keville.ReBoggled.service.gameService.solution.BoardSolver;
import com.keville.ReBoggled.service.gameService.solution.BoardSolver.BoardSolverException;

@Component
public class DefaultGameService implements GameService {

    private static final Logger LOG = LoggerFactory.getLogger(GameService.class);
    private GameRepository games;
    private UserRepository users;

    private GameSummarizer gameSummarizer;
    private BoardGenerator boardGenerator;
    private BoardSolver boardSolver;

    public DefaultGameService(@Autowired GameRepository games,
        @Autowired UserRepository users,
        @Autowired BoardGenerator boardGenerator,
        @Autowired BoardSolver boardSolver,
        @Autowired GameSummarizer gameSummarizer) {
      this.games = games;
      this.users = users;
      this.boardGenerator = boardGenerator; 
      this.boardSolver = boardSolver; 
      this.gameSummarizer = gameSummarizer; 
    }

    public Game getGame(int id) throws GameServiceException {
      Game game = findGameById(id);
      return game;
    }

    public Game createGame(Lobby lobby) throws GameServiceException {

      GameSettings gameSettings = lobby.gameSettings;
      Game game = new Game();

      game.findRule = gameSettings.findRule;
      game.duration = gameSettings.duration;

      try {

        game.board = boardGenerator.generate(gameSettings.boardSize,gameSettings.boardTopology,gameSettings.tileRotation);

        game.start = LocalDateTime.now();
        game.end = game.start.plusSeconds(gameSettings.duration);

        game = games.save(game); //get assigned game id

        for (LobbyUserReference userRef : lobby.users) {
          game.users.add(new GameUserReference(AggregateReference.to(game.id),AggregateReference.to(userRef.user.getId()))); 
        }

        game = games.save(game);


      } catch ( BoardGenerationException bge ) {
        LOG.error(" Board generation failed for game " + game.id );
        throw new GameServiceException(GameServiceError.BOARD_GENERATION_FAILURE);
      }

      return game;

    }

    //some of these are not exceptions... answer response dto should encompass the rejection reasons
    public Game addGameAnswer(Integer gameId, Integer userId, String rawUserAnswer) throws GameServiceException {

      final String userAnswer = rawUserAnswer.toLowerCase();

      Game game = findGameById(gameId);
      User user = findUserById(userId);

      //Does the user belong to this game?
      if ( !game.users.stream().anyMatch( gur -> gur.user.getId().equals(userId)) ) {
        throw new GameServiceException(GameServiceError.USER_NOT_IN_GAME);
      }
      
      //Is the game ongoing?
      if ( LocalDateTime.now().isAfter(game.end) ) {
        LOG.warn(String.format("user %d trying to submit answer for finished game %d",userId,gameId));
        throw new GameServiceException(GameServiceError.GAME_OVER);
      }

      //FIXME : Not an exception , make part of response model
      //Does this word exist in the solution space? 
      try {
        if ( !boardSolver.isWordInSolution(userAnswer,game.board) ) {
          LOG.trace(String.format(" answer %s is not correct for game %d",userAnswer,game.id));
          throw new GameServiceException(GameServiceError.INVALID_ANSWER);
        }
      } catch ( BoardSolverException bse ) {
        throw new GameServiceException(GameServiceError.ERROR);
      }
     
      //FIXME : Not an exception , make part of response model
      //Did this player already find this word?
      if ( game.answers.stream()
          .filter( ga -> ga.user.getId().equals(userId) )
          .anyMatch(ga -> ga.answer.equals(userAnswer) ) 
      ) {
        LOG.debug(String.format(" answer %s is already found for user %d",userAnswer,user.id));
        throw new GameServiceException(GameServiceError.ANSWER_ALREADY_FOUND);
      }

      game.answers.add(new GameAnswer(userId,userAnswer));
      games.save(game);

      return game;

    }

    public GameDTO getGameDTO(Integer gameId,Integer userId) throws GameServiceException {

      Optional<Game> optGame = games.findById(gameId);
      Optional<User> optUser = users.findById(userId);

      if ( optGame.isEmpty() ) {
        throw new GameServiceException(GameServiceError.GAME_NOT_FOUND);
      }
      if ( optUser.isEmpty() ) {
        throw new GameServiceException(GameServiceError.USER_NOT_FOUND);
      }

      Game game = optGame.get();
      User user = optUser.get();

      //extract users answers 
      Set<GameAnswerDTO> userAnswers = 
        game.answers.stream()
        .filter( ans -> {
          return ans.user.getId().equals(userId);
        })
        .map( uga ->  new GameAnswerDTO(uga.answer,uga.answerSubmissionTime) )
        .collect(Collectors.toSet());

      return new GameDTO(game,userAnswers);
    }

    public PostGameDTO getPostGameDTO(Integer gameId,Integer userId) throws GameServiceException {

      Optional<Game> optGame = games.findById(gameId);
      Optional<User> optUser = users.findById(userId);

      if ( optGame.isEmpty() ) {
        throw new GameServiceException(GameServiceError.GAME_NOT_FOUND);
      }
      if ( optUser.isEmpty() ) {
        throw new GameServiceException(GameServiceError.USER_NOT_FOUND);
      }

      Game game = optGame.get();
      User user = optUser.get();

      GameSummary gameSummary = gameSummarizer.summarize(game);

      //transform gameSummary into set of 'GameWordDTOs' (flavor GameWord for user)
      Set<GameWordDTO> gameWordDTOs = new HashSet<GameWordDTO>();
      gameSummary.gameBoardWords().forEach( gbw -> {

        boolean found   = gbw.finders().stream().anyMatch( (finder) -> finder.id().equals(userId) );
        //some duplicate logic here with the calculation of GameSummary
        boolean counted = false;
        if ( found ) {

          switch ( game.findRule ) {
            case UNIQUE:
              counted = gbw.finders().size() == 1;
              break;
            case FIRST:
              Optional<WordFinder> firstFinder = gbw.finders().stream().sorted( (a,b) -> a.time().compareTo(b.time()) ).findFirst();
              counted = firstFinder.get().id().equals(userId);
              break;
            case ANY:
            default:
              counted = true;
          }

        }

        gameWordDTOs.add(new GameWordDTO(gbw.word(),gbw.paths(),gbw.finders(),gbw.points(),found,counted));

      });

      return new PostGameDTO(game,gameWordDTOs,gameSummary.scoreboard());

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
