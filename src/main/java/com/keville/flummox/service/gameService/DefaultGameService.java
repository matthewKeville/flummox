package com.keville.flummox.service.gameService;

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

import com.keville.flummox.DTO.GameAnswerRequestDTO;
import com.keville.flummox.DTO.GameAnswerResponseDTO;
import com.keville.flummox.DTO.GameDTO;
import com.keville.flummox.DTO.PostGameDTO;
import com.keville.flummox.DTO.GameAnswerResponseDTO.Rejection;
import com.keville.flummox.model.game.Game;
import com.keville.flummox.model.game.GameAnswer;
import com.keville.flummox.model.game.GameSettings;
import com.keville.flummox.model.game.GameUserReference;
import com.keville.flummox.model.lobby.Lobby;
import com.keville.flummox.model.lobby.LobbyUserReference;
import com.keville.flummox.model.user.User;
import com.keville.flummox.repository.GameRepository;
import com.keville.flummox.repository.UserRepository;
import com.keville.flummox.service.exceptions.BadRequest;
import com.keville.flummox.service.exceptions.EntityNotFound;
import com.keville.flummox.service.exceptions.NotAuthorized;
import com.keville.flummox.service.gameService.board.BoardGenerationException;
import com.keville.flummox.service.gameService.board.BoardGenerator;
import com.keville.flummox.service.gameService.solution.BoardSolver;
import com.keville.flummox.service.gameService.solution.BoardSolver.BoardSolverException;
import com.keville.flummox.service.gameService.summary.GameSummarizer;
import com.keville.flummox.service.gameService.summary.GameSummary;
import com.keville.flummox.service.utils.ServiceUtils;

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

    public Game getGame(int id) throws EntityNotFound,NotAuthorized {

      User principal = ServiceUtils.getPrincipal();
      Game game = ServiceUtils.findGameById(games,id);

      if ( !game.users.stream().anyMatch( gur -> gur.user.getId() == principal.id )) {
        throw new NotAuthorized(String.format("user %d is not a game %d participant",principal.id,id));
      }

      return game;
    }

    public Game createGame(Lobby lobby) throws BoardGenerationException,NotAuthorized {

      User principal = ServiceUtils.getPrincipal();

      if ( !principal.id.equals(lobby.owner.getId()) ) {
        throw new NotAuthorized(String.format("user %d is not the lobby %d owner",principal.id,lobby.id));
      }

      GameSettings gameSettings = lobby.gameSettings;
      Game game = new Game();

      game.findRule = gameSettings.findRule;
      game.duration = gameSettings.duration;

      game.board = boardGenerator.generate(gameSettings.boardSize,gameSettings.boardTopology,gameSettings.tileRotation);

      game.start = LocalDateTime.now();
      game.end = game.start.plusSeconds(gameSettings.duration);

      game = games.save(game); //get assigned game id

      for (LobbyUserReference userRef : lobby.users) {
        game.users.add(new GameUserReference(AggregateReference.to(game.id),AggregateReference.to(userRef.user.getId()))); 
      }

      game = games.save(game);

      return game;

    }

    /* it feels redundant to include the userId in submit game answer, yet have the userId (of the principal) available in the security
     * context. I think, the service method should still contain the userId, for the purpose of being able
     * to unit test the service. This parameter dictates the type of service request ( add answer for user in game .. )
     * whereas the principal will be used to ensure authorization. Which in unit test can be spoofed or disabled */
    public GameAnswerResponseDTO submitGameAnswer(Integer gameId,Integer userId,GameAnswerRequestDTO gameAnswerRequestDTO) throws EntityNotFound,InternalError,NotAuthorized {

      final String userAnswer = gameAnswerRequestDTO.answer.toLowerCase();

      User principal = ServiceUtils.getPrincipal();
      Game game = ServiceUtils.findGameById(games,gameId);
      User user = ServiceUtils.findUserById(users,userId);

      if ( !principal.id.equals(userId)) {
        throw new NotAuthorized(String.format("principal %d is not user %d",principal.id,user.id));
      }

      //Does the user belong to this game?
      if ( !game.users.stream().anyMatch( gur -> gur.user.getId().equals(user.id)) ) {
        throw new NotAuthorized(String.format("user %d is not a game participant %d",user.id,game.id));
      }
      
      //Is the game ongoing?
      if ( LocalDateTime.now().isAfter(game.end) ) {
        throw new BadRequest(String.format("can't submit answers for game %d because it's ended",game.id));
      }

      //Does this word exist in the solution space? 
      try {
        if ( !boardSolver.isWordInSolution(userAnswer,game.board) ) {
          return GameAnswerResponseDTO.Rejected(Rejection.NOT_FOUND);
        }
      } catch (BoardSolverException e) {
        LOG.error("Exception",e);
        throw new InternalError("Encountered Board Solver Exception");
      }
     
      //Did this player already find this word?
      if ( game.answers.stream().filter( ga -> ga.user.getId().equals(user.id) ).anyMatch(ga -> ga.answer.equals(userAnswer) )) {
        return GameAnswerResponseDTO.Rejected(Rejection.ALREADY_FOUND);
      }

      game.answers.add(new GameAnswer(userId,userAnswer));
      games.save(game);
      return GameAnswerResponseDTO.Accepted();

    }

    public GameDTO getGameDTO(Integer gameId) throws EntityNotFound,NotAuthorized {

      User principal = ServiceUtils.getPrincipal();
      Game game = ServiceUtils.findGameById(games,gameId);

      //TODO : Check if User in Game

      //extract users answers 
      Set<String> userAnswers = 
        game.answers.stream()
        .filter( ans -> {
          return ans.user.getId().equals(principal.id);
        })
        .map( uga ->  uga.answer )
        .collect(Collectors.toSet());

      return new GameDTO(game,userAnswers);

    }

    public PostGameDTO getPostGameDTO(Integer gameId,Integer userId) throws EntityNotFound,NotAuthorized {

      User principal = ServiceUtils.getPrincipal();
      Game game = ServiceUtils.findGameById(games,gameId);
      User user = ServiceUtils.findUserById(users,userId);

      if ( !principal.id.equals(userId)) {
        throw new NotAuthorized(String.format("principal %d is not user %d",principal.id,user.id));
      }

      try {
        GameSummary gameSummary = gameSummarizer.summarize(game,userId);
        return new PostGameDTO(game,gameSummary.wordSummaries());
      } catch (BoardSolverException bse ) {
        throw new InternalError("board solver failure");
      }

    }

}
