package com.keville.ReBoggled.unit.service;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.keville.ReBoggled.model.game.Game;
import com.keville.ReBoggled.model.game.GameFactory;
import com.keville.ReBoggled.model.game.GameSeed;
import com.keville.ReBoggled.model.game.GameSettings;
import com.keville.ReBoggled.service.solutionService.SolutionService;

@SpringBootTest
class SolutionServiceTest {

  public static Logger LOG = LoggerFactory.getLogger(SolutionServiceTest.class);

  @Autowired
  private SolutionService solutionService;
  @Autowired
  private GameFactory gameFactory;

  @Test
  void solveFindsAllWords() {
    GameSettings gameSettings = new GameSettings();
    Game game = gameFactory.getGame(gameSettings);
    List<String> solution = solutionService.solve(new GameSeed(game));
    Collections.sort(solution);
    LOG.info(solution.toString());
  }

}
