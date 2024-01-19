package com.keville.ReBoggled.service.gameSummaryService;

import org.springframework.beans.factory.annotation.Autowired;

import com.keville.ReBoggled.DTO.GameAnswerSubmissionDTO;
import com.keville.ReBoggled.model.game.BoardWord;
import com.keville.ReBoggled.model.game.Game;
import com.keville.ReBoggled.model.game.GameAnswer;
import com.keville.ReBoggled.model.gameSummary.GameSummary;
import com.keville.ReBoggled.model.gameSummary.GameWord;
import com.keville.ReBoggled.model.gameSummary.ScoreBoardEntry;
import com.keville.ReBoggled.model.gameSummary.WordFinder;
import com.keville.ReBoggled.repository.UserRepository;
import com.keville.ReBoggled.service.solutionService.SolutionService;
import com.keville.ReBoggled.service.solutionService.SolutionServiceException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    try {

      Map<String,BoardWord> solution = solutionService.solve(game.board);
      Set<GameWord> gameBoardWords = new HashSet<GameWord>();

      for ( BoardWord boardWord : solution.values() ) {

        int points = nominalScore(boardWord,game);
        GameAnswer firstAnswer = null;
        List<WordFinder> finders = new ArrayList<WordFinder>();

        //Who found this word & who found it first
        for ( GameAnswer gameAnswer : game.answers ) {
          if ( gameAnswer.answer.equalsIgnoreCase(boardWord.word) ) {
            finders.add(new WordFinder(gameAnswer.user.getId(),gameAnswer.answerSubmissionTime));
          }
        }


        gameBoardWords.add(new GameWord(boardWord.word,boardWord.paths,points,finders));

      }

      List<ScoreBoardEntry> scoreBoardEntries = createScoreBoard(gameBoardWords, game);
      GameSummary summary = new GameSummary(gameBoardWords,scoreBoardEntries);

      return summary;

    } catch (SolutionServiceException e) {

      LOG.error("GameSummaryService execption");
      LOG.error("GameSummaryService execption");
      LOG.error("GameSummaryService execption");

      LOG.error("returning empty summary");

      return new GameSummary(Collections.EMPTY_SET,Collections.EMPTY_LIST);
    }

  }

  private int nominalScore(GameWord gameWord,Game game) {
    BoardWord boardWord = new BoardWord(gameWord.paths(),gameWord.word());
    return nominalScore(boardWord, game);
  }

  private int nominalScore(BoardWord boardWord,Game game) {
    /* This is classic boggle scoring, this should depend on Game Settings , perhaps WordScale enum*/
    switch ( boardWord.word.length() ) {
      case 1:
      case 2:
        return 0;
      case 3:
      case 4:
        return 1;
      case 5:
        return 2;
      case 6:
        return 3;
      case 7:
        return 5;
      default:
        return boardWord.word.length();
    }
  }

  private List<ScoreBoardEntry> createScoreBoard(Set<GameWord> gameWords,Game game) {

    class UserTally {
      public int score;
      public int words;
      UserTally(int score,int words){
        this.score = score;
        this.words = words;
      }
    }

    Map<Integer,UserTally> userPoints = new HashMap<Integer,UserTally>();

    for ( GameWord gameWord : gameWords ) {

      List<WordFinder> findersSorted = new ArrayList<WordFinder>(gameWord.finders());
      Collections.sort(findersSorted,
        (a , b) -> 
        { return -1*a.time().compareTo(b.time()); } 
      );

      //credit finders
      for ( int i = 0; i < gameWord.finders().size(); i++ ) {

        int points = 0;

        switch ( game.findRule ) {
          case FIRST:
            if ( i == 0 ) {
              points = nominalScore(gameWord, game);
            }
            break;
          case ANY:
            points = nominalScore(gameWord, game);
            break;
          case UNIQUE:
            if ( gameWord.finders().size() == 1 ) {
              points = nominalScore(gameWord, game);
            }
            break;
        }

        int userId = gameWord.finders().get(i).id();

        UserTally currentUser;
        if ( userPoints.containsKey(userId) ) {
          currentUser = userPoints.get(gameWord.finders().get(i).id());
        } else {
          currentUser= userPoints.put(userId,new UserTally(0,0));
        }

        currentUser.words++;
        currentUser.score+=points;

      }

    }

    // repackage UserTally Map
    List<ScoreBoardEntry> scoreBoard = new ArrayList<ScoreBoardEntry>();

    userPoints.entrySet().forEach( entry -> {
      scoreBoard.add(new ScoreBoardEntry(entry.getKey(), entry.getValue().score, entry.getValue().words));
    });

    return scoreBoard;

  }

}
