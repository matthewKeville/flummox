package com.keville.ReBoggled.service.answerService;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.keville.ReBoggled.model.game.BoardWord;
import com.keville.ReBoggled.model.game.Game;
import com.keville.ReBoggled.model.game.GameSeed;
import com.keville.ReBoggled.repository.UserRepository;
import com.keville.ReBoggled.service.solutionService.SolutionService;
import com.keville.ReBoggled.service.solutionService.SolutionServiceException;

@Component
public class DefaultAnswerService implements AnswerService {

  public static Logger LOG = LoggerFactory.getLogger(AnswerService.class);
  public SolutionService solutionService;
  public UserRepository users;

  public DefaultAnswerService(
      @Autowired SolutionService solutionService,
      @Autowired UserRepository users) {
    this.solutionService = solutionService;
    this.users = users;
  }

  public boolean isValidWord(String word,Game game) {

    word = word.toLowerCase();

    GameSeed gameSeed = new GameSeed(game);
    try {
      Map<String,BoardWord> solution = solutionService.solve(game.board);
      boolean result = solution.containsKey(word);
      LOG.info("word : " + word + ( result ? " was found found " : " wasn't found ") ) ;
      LOG.info("solution : " + solution.toString());
      return result;
    } catch (SolutionServiceException sse) {
      LOG.error("Caught exception trying to get board solution");
      LOG.error(sse.getMessage());
      return false;
    }

  }

  //Score without considering other users
  private int nominalScore(BoardWord boardWord,Game game) {
    return 0;
  }

}
