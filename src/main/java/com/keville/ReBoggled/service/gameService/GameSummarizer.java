package com.keville.ReBoggled.service.gameService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.keville.ReBoggled.model.game.BoardWord;
import com.keville.ReBoggled.model.game.Game;
import com.keville.ReBoggled.model.game.GameAnswer;
import com.keville.ReBoggled.model.gameSummary.GameSummary;
import com.keville.ReBoggled.model.gameSummary.GameWord;
import com.keville.ReBoggled.model.gameSummary.ScoreBoardEntry;
import com.keville.ReBoggled.model.gameSummary.WordFinder;
import com.keville.ReBoggled.repository.UserRepository;
import com.keville.ReBoggled.service.gameService.solution.BoardSolver;
import com.keville.ReBoggled.service.gameService.solution.BoardSolver.BoardSolverException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class GameSummarizer {

  public static Logger LOG = LoggerFactory.getLogger(GameSummarizer.class);
  public BoardSolver boardSolver;
  public UserRepository users;

  private Map<Integer,GameSummary> gameSummaryCache = new HashMap<Integer,GameSummary>();

  public GameSummarizer(
      @Autowired BoardSolver boardSolver,
      @Autowired UserRepository users) {
    this.boardSolver = boardSolver;
    this.users = users;
  }

  public GameSummary summarize(Game game) {

    if (gameSummaryCache.containsKey( game.id )) {
      return gameSummaryCache.get(game.id);
    }

    GameSummary summary = generateGameSummary(game);
    gameSummaryCache.put(game.id,summary);
    return summary;

  }

  private GameSummary generateGameSummary(Game game) {

    try {

      Map<String,BoardWord> solution = boardSolver.solve(game.board);
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

    } catch (BoardSolverException bse) {

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
      public Integer userId; //so we can sort before creating ScoreBoardEntry
      UserTally(int score,int words,Integer userId){
        this.score = score;
        this.words = words;
        this.userId = userId;
      }
    }

    Map<Integer,UserTally> userPoints = new HashMap<Integer,UserTally>();

    for ( GameWord gameWord : gameWords ) {

      List<WordFinder> findersSorted = new ArrayList<WordFinder>(gameWord.finders());
      Collections.sort(findersSorted,
        (a , b) -> 
        { return a.time().compareTo(b.time()); } 
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
          currentUser = userPoints.get(userId);
        } else {
          userPoints.put(userId,new UserTally(0,0,userId));
          currentUser = userPoints.get(userId);
        }

        currentUser.words++;
        currentUser.score+=points;

      }

    }

    // repackage UserTally Map
    List<ScoreBoardEntry> scoreBoard = new ArrayList<ScoreBoardEntry>();

    //sort user tallies by points
    List<Entry<Integer,UserTally>> sortedUserPoints = userPoints.entrySet()
      .stream()
      .sorted( (a,b) -> Integer.compare(a.getValue().score, b.getValue().score) )
      .toList();


    Iterator<Entry<Integer,UserTally>> iterator = sortedUserPoints.iterator();
      
    int rank = 1;
    int ties = 0;
    UserTally tally;
    UserTally tallyPrev = null;
    while ( iterator.hasNext() ) {

      tally = iterator.next().getValue();

      if ( tallyPrev == null || tallyPrev.score == tally.score ) {
        //rank stays
        if ( tallyPrev != null ) {
          ties++;
        }
      } else {
        rank+=(ties+1);
        ties = 0;
      }

      tallyPrev = tally;
      scoreBoard.add(new ScoreBoardEntry(tally.userId,rank,tally.score,tally.words));

    }

    return scoreBoard;

  }

}
