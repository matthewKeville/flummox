package com.keville.ReBoggled.service.answerService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.keville.ReBoggled.model.game.Game;
import com.keville.ReBoggled.model.game.GameSeed;
import com.keville.ReBoggled.service.solutionService.SolutionService;

@Component
public class AnswerService {

  public static Logger LOG = LoggerFactory.getLogger(AnswerService.class);

  public Map<GameSeed,List<String>> solveCache;
  public SolutionService solutionService;


  public AnswerService(@Autowired SolutionService solutionService) {
    this.solutionService = solutionService;
    this.solveCache = new HashMap<GameSeed,List<String>>();
  }

  public boolean isValidWord(String word,Game game) {

    GameSeed gameSeed = new GameSeed(game);

    if ( solveCache.containsKey(gameSeed) ) {

      LOG.info("solve cache hit!");
      return solveCache.get(gameSeed).contains(word);

    } 

    LOG.info("solve cache miss");

    List<String> solution = solutionService.solve(gameSeed);
    solveCache.put(gameSeed,solution);

    return solution.contains(word);

  }

}
