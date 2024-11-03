package com.keville.ReBoggled.integration.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.keville.ReBoggled.model.game.Board;
import com.keville.ReBoggled.model.game.BoardGenerationException;
import com.keville.ReBoggled.model.game.BoardSize;
import com.keville.ReBoggled.model.game.BoardTopology;
import com.keville.ReBoggled.model.game.BoardWord;
import com.keville.ReBoggled.model.game.ClassicTilesGenerator;
import com.keville.ReBoggled.model.game.TileCodeStringMap;
import com.keville.ReBoggled.service.gameService.BoardGenerator;
import com.keville.ReBoggled.service.solutionService.DefaultSolutionService;
import com.keville.ReBoggled.service.solutionService.SolutionService;
import com.keville.ReBoggled.service.solutionService.SolutionServiceException;
import com.keville.ReBoggled.service.solutionService.WordValidator;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {DefaultSolutionService.class,TileCodeStringMap.class,WordValidator.class,BoardGenerator.class,ClassicTilesGenerator.class})
class SolutionServiceTest {

  public static Logger LOG = LoggerFactory.getLogger(SolutionServiceTest.class);

  @Autowired
  private SolutionService solutionService;
  @Autowired
  private BoardGenerator boardGenerator;

  /*
    O   A   P   W


    L   T   N   R


    E   E   B   T


    S   I   Qu   N
  */

  @Test
  void solveFindsAllWords() throws SolutionServiceException , BoardGenerationException {

    List<String> wordsInBoard = Arrays.asList( "belt","pant","apt","pan", "beet", "set", "elate" );
    BoardSize size = BoardSize.FOUR;
    BoardTopology topology = BoardTopology.PLANE;
    Board board = boardGenerator.generateFromTileString("oapwltnreebtsiqn",size,topology);

    Map<String,BoardWord> solution = solutionService.solve(board);

    LOG.info(solution.toString());

    for ( String word : wordsInBoard ) {
      assertTrue(solution.containsKey(word.toLowerCase()),String.format("%s is in the board but was not found",word));
    }

  }

  /*
    O   A   P   W


    L   T   N   R


    E   E   B   T


    S   I   Qu   N
  */

  @Test
  void solveCrossesHorizontalCylinder() throws SolutionServiceException , BoardGenerationException {

    LOG.info("horizontal test");

    List<String> wordsInBoard = new ArrayList<String>(Arrays.asList( "belt","pant","apt","pan", "beet", "set", "elate" ));
    wordsInBoard.addAll(Arrays.asList( "owl" , "alter", "test", "brownest" ));

    BoardSize size = BoardSize.FOUR;
    BoardTopology topology = BoardTopology.CYLINDER;
    Board board = boardGenerator.generateFromTileString("oapwltnreebtsiqn",size,topology);

    Map<String,BoardWord> solution = solutionService.solve(board);

    LOG.info(solution.toString());

    for ( String word : wordsInBoard ) {
      assertTrue(solution.containsKey(word.toLowerCase()),String.format("%s is in the board but was not found",word));
    }

  }

  /*
    O   A   P   W


    L   T   N   R


    E   E   B   T


    S   I   Qu   N
  */

  @Test
  void solveCrossesVerticalCylinder() throws SolutionServiceException , BoardGenerationException {
    
    LOG.info("vertical test");

    List<String> wordsInBoard = new ArrayList<String>(Arrays.asList( "belt","pant","apt","pan", "beet", "set", "elate" ));
    wordsInBoard.addAll(Arrays.asList( "sap", "qua"));

    BoardSize size = BoardSize.FOUR;
    BoardTopology topology = BoardTopology.CYLINDER_ALT;
    Board board = boardGenerator.generateFromTileString("oapwltnreebtsiqn",size,topology);

    Map<String,BoardWord> solution = solutionService.solve(board);

    LOG.info(solution.toString());

    for ( String word : wordsInBoard ) {
      assertTrue(solution.containsKey(word.toLowerCase()),String.format("%s is in the board but was not found",word));
    }

  }

  /*
    O   A   P   W


    L   T   N   R


    E   E   B   T


    S   I   Qu   N
  */

  @Test
  void solveCrossesTorus() throws SolutionServiceException , BoardGenerationException {
    
    LOG.info("torus test");

    List<String> wordsInBoard = new ArrayList<String>(Arrays.asList( "belt","pant","apt","pan", "beet", "set", "elate" ));
    wordsInBoard.addAll(Arrays.asList( "sap", "qua"));
    wordsInBoard.addAll(Arrays.asList( "owl" , "alter", "test", "brownest" ));

    BoardSize size = BoardSize.FOUR;
    BoardTopology topology = BoardTopology.TORUS;
    Board board = boardGenerator.generateFromTileString("oapwltnreebtsiqn",size,topology);

    Map<String,BoardWord> solution = solutionService.solve(board);

    LOG.info(solution.toString());

    for ( String word : wordsInBoard ) {
      assertTrue(solution.containsKey(word.toLowerCase()),String.format("%s is in the board but was not found",word));
    }

  }

}
