package com.keville.ReBoggled.service.gameSummaryService;

import org.springframework.beans.factory.annotation.Autowired;

import com.keville.ReBoggled.model.game.Game;
import com.keville.ReBoggled.model.gameSummary.GameSummary;
import com.keville.ReBoggled.repository.UserRepository;
import com.keville.ReBoggled.service.solutionService.SolutionService;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.keville.ReBoggled.service.answerService.AnswerService;

public class DefaultGameSummaryService implements GameSummaryService {


  public static Logger LOG = LoggerFactory.getLogger(DefaultGameSummaryService.class);
  public SolutionService solutionService;
  public AnswerService answerService;
  public UserRepository users;

  private Map<Integer,GameSummary> gameSummaryCache = new HashMap<Integer,GameSummary>();

  public DefaultGameSummaryService(
      @Autowired SolutionService solutionService,
      @Autowired AnswerService answerService,
      @Autowired UserRepository users) {
    this.solutionService = solutionService;
    this.answerService = answerService;
    this.users = users;
  }

  public GameSummary getSummary(Game game) {

    if (gameSummaryCache.containsKey( game.id )) {
      return gameSummaryCache.get(game.id);
    }

    GameSummary summary = generateGameSummary(game);
    gameSummaryCache.put(game.id,summary);
    return summary;

  }

  private GameSummary generateGameSummary(Game game) {
    //todo
    return null;
  }

  /*
  private int nominalScore(BoardWord boardWord,Game game) {
    return 0;
  }

  public Set<UserGameBoardWord> getUserGameBoardWords(Game game, User user) throws AnswerServiceException {

    Set<GameBoardWord> gameBoardWords = getGameBoardWords(game);
    Set<UserGameBoardWord> userGameBoardWords = new HashSet<UserGameBoardWord>();

    gameBoardWords.forEach( gameBoardWord -> {

      boolean userFoundWord = game.answers.stream().anyMatch( answer -> answer.answer.equalsIgnoreCase(gameBoardWord.word) 
          && answer.user.getId().equals( user.id ));

      int userPoints = 0;
      if ( userFoundWord ) {
        //TODO : calculate user points with respect to other plays and the game rules
      }

      userGameBoardWords.add(new UserGameBoardWord(gameBoardWord,userPoints,userFoundWord));

    });

    return userGameBoardWords;

  }

  //What happens when we retire guest accounts and we want to recalculate old score boards?
  public List<ScoreBoardEntry> getScoreBoard(Game game) throws AnswerServiceException {

    List<ScoreBoardEntry> scoreBoard = new ArrayList<ScoreBoardEntry>();

    Set<Integer> gameUserIds = 
      new HashSet<Integer>(
          game.answers.stream()
          .map( x -> x.user.getId())
          .distinct()
          .toList()
      );

    for ( Integer userId : gameUserIds ) {
      Optional<User> user = users.findById(userId);
      if ( user.isEmpty() ) {
        throw new AnswerServiceException(AnswerServiceError.ERROR);
      }
      int userScore    = getUserGameBoardWords(game,user.get()).stream().map( ugbw -> ugbw.actualPoints).reduce(0, (total, x) -> total + x);
      long foundWords  = getUserGameBoardWords(game,user.get()).stream().count();
      scoreBoard.add(new ScoreBoardEntry(userId, userScore, (int) foundWords));
    }

    return scoreBoard;

    return null;

  }

  private Set<GameBoardWord> getGameBoardWords(Game game) throws AnswerServiceException {

    try {

      Set<GameBoardWord> gameBoardWords = new HashSet<GameBoardWord>();

      Map<String,BoardWord> solution = solutionService.solve(new GameSeed(game));

      for ( BoardWord boardWord : solution.values() ) {

        int potentialPoints = nominalScore(boardWord,game);

        GameAnswer firstAnswer = null;
        List<Integer> cofinders = new ArrayList<Integer>();

        for ( GameAnswer gameAnswer : game.answers ) {
          if ( gameAnswer.answer.equalsIgnoreCase(boardWord.word) ) {

            cofinders.add(gameAnswer.user.getId());
            if ( firstAnswer == null || gameAnswer.answerSubmissionTime.isBefore(firstAnswer.answerSubmissionTime) ) {
              firstAnswer = gameAnswer;
            }

          }
        }

        Integer firstFinder = firstAnswer == null ?  null : firstAnswer.user.getId();

        gameBoardWords.add(new GameBoardWord(boardWord,potentialPoints,firstFinder,cofinders));

      }

      return gameBoardWords;

    } catch (SolutionServiceException e) {
      throw new AnswerServiceException(AnswerServiceError.ERROR);
    }
  }
  */

}
