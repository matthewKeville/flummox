package com.keville.ReBoggled.unit.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.keville.ReBoggled.model.game.BoardTopology;
import com.keville.ReBoggled.model.game.Game;
import com.keville.ReBoggled.model.game.GameFactory;
import com.keville.ReBoggled.model.game.GameSeed;
import com.keville.ReBoggled.model.game.GameSettings;
import com.keville.ReBoggled.service.solutionService.SolutionService;
import com.keville.ReBoggled.service.solutionService.SolutionServiceException;

@SpringBootTest
class SolutionServiceTest {

  public static Logger LOG = LoggerFactory.getLogger(SolutionServiceTest.class);

  @Autowired
  private SolutionService solutionService;
  @Autowired
  private GameFactory gameFactory;

  /*
    O   A   P   W


    L   T   N   R


    E   E   B   T


    S   I   Qu   N
  */

  @Test
  void solveFindsAllWords() throws SolutionServiceException {

    List<String> wordsInBoard = Arrays.asList( "belt","pant","apt","pan", "beet", "set", "elate" );

    GameSettings gameSettings = new GameSettings();
    Game game = gameFactory.getGameUsingTileString(gameSettings,"oapwltnreebtsiqn");

    List<String> solution = solutionService.solve(new GameSeed(game));
    Collections.sort(solution);

    LOG.info(solution.toString());

    for ( String word : wordsInBoard ) {
      assertTrue(solution.stream().anyMatch( x -> x.equalsIgnoreCase(word)),String.format("%s is in the board but was not found",word));
    }

  }

  /*
    O   A   P   W


    L   T   N   R


    E   E   B   T


    S   I   Qu   N
  */

  @Test
  void solveCrossesHorizontalCylinder() throws SolutionServiceException {

    LOG.info("horizontal test");

    List<String> wordsInBoard = new ArrayList<String>(Arrays.asList( "belt","pant","apt","pan", "beet", "set", "elate" ));
    wordsInBoard.addAll(Arrays.asList( "owl" , "alter", "test", "brownest" ));

    GameSettings gameSettings = new GameSettings();
    gameSettings.boardTopology = BoardTopology.CYLINDER;
    Game game = gameFactory.getGameUsingTileString(gameSettings,"oapwltnreebtsiqn");

    List<String> solution = solutionService.solve(new GameSeed(game));
    Collections.sort(solution);

    LOG.info(solution.toString());

    for ( String word : wordsInBoard ) {
      assertTrue(solution.stream().anyMatch( x -> x.equalsIgnoreCase(word)),String.format("%s is in the board but was not found",word));
    }

  }

  /*
    O   A   P   W


    L   T   N   R


    E   E   B   T


    S   I   Qu   N
  */

  @Test
  void solveCrossesVerticalCylinder() throws SolutionServiceException {
    
    LOG.info("vertical test");

    List<String> wordsInBoard = new ArrayList<String>(Arrays.asList( "belt","pant","apt","pan", "beet", "set", "elate" ));
    wordsInBoard.addAll(Arrays.asList( "sap", "qua"));

    GameSettings gameSettings = new GameSettings();
    gameSettings.boardTopology = BoardTopology.CYLINDER_ALT;
    Game game = gameFactory.getGameUsingTileString(gameSettings,"oapwltnreebtsiqn");

    List<String> solution = solutionService.solve(new GameSeed(game));
    Collections.sort(solution);

    LOG.info(solution.toString());

    for ( String word : wordsInBoard ) {
      assertTrue(solution.stream().anyMatch( x -> x.equalsIgnoreCase(word)),String.format("%s is in the board but was not found",word));
    }

  }

  /*
    O   A   P   W


    L   T   N   R


    E   E   B   T


    S   I   Qu   N
  */

  @Test
  void solveCrossesTorus() throws SolutionServiceException {
    
    LOG.info("torus test");

    List<String> wordsInBoard = new ArrayList<String>(Arrays.asList( "belt","pant","apt","pan", "beet", "set", "elate" ));
    wordsInBoard.addAll(Arrays.asList( "sap", "qua"));
    wordsInBoard.addAll(Arrays.asList( "owl" , "alter", "test", "brownest" ));

    GameSettings gameSettings = new GameSettings();
    gameSettings.boardTopology = BoardTopology.TORUS;
    Game game = gameFactory.getGameUsingTileString(gameSettings,"oapwltnreebtsiqn");

    List<String> solution = solutionService.solve(new GameSeed(game));
    Collections.sort(solution);

    LOG.info(solution.toString());

    for ( String word : wordsInBoard ) {
      assertTrue(solution.stream().anyMatch( x -> x.equalsIgnoreCase(word)),String.format("%s is in the board but was not found",word));
    }

  }

}
