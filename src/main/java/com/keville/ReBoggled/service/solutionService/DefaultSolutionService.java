package com.keville.ReBoggled.service.solutionService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.keville.ReBoggled.model.game.BoardWord;
import com.keville.ReBoggled.model.game.BoardTopology;
import com.keville.ReBoggled.model.game.GameSeed;
import com.keville.ReBoggled.model.game.GameSettings;
import com.keville.ReBoggled.model.game.TileCodeStringMap;
import com.keville.ReBoggled.service.solutionService.GraphBuilder.GraphBuilderException;
import com.keville.ReBoggled.service.solutionService.SolutionServiceException.SolutionServiceError;
import com.keville.ReBoggled.service.wordService.WordService;
import com.keville.ReBoggled.util.GameBoardStringifier;

@Component
public class DefaultSolutionService implements SolutionService {

  public static Logger LOG = LoggerFactory.getLogger(SolutionService.class);

  @Autowired
  private TileCodeStringMap tileCodeStringMap;
  @Autowired
  private WordService wordService;
  
  private Map<GameSeed,Map<String,BoardWord>> solveCache = new HashMap<GameSeed,Map<String,BoardWord>>();

  public Map<String,BoardWord> solve(GameSeed seed) throws SolutionServiceException {
    if (solveCache.containsKey(seed) ) {
      LOG.info("solve cache hit!");
      return solveCache.get(seed);
    }
    LOG.info("solve cache miss!");
    Map<String,BoardWord> solution = solveBoard(seed);
    solveCache.put(seed,solution);
    return solution;
  }

  private Map<String,BoardWord> solveBoard(GameSeed seed) throws SolutionServiceException {

    LOG.info("building graph");
    int boardSize = getBoardSize(seed.gameSettings);    
    final TileGraph graph;

    try {

    switch ( seed.gameSettings.boardTopology ) {
      case PLANE:
        PlaneBoardGraphBuilder planeBoardGraphBuilder = new PlaneBoardGraphBuilder();
        planeBoardGraphBuilder.setTiles(seed.tiles);
        planeBoardGraphBuilder.setSize(boardSize);
        graph = planeBoardGraphBuilder.build();
        break;
      case CYLINDER:
        //fall through
      case CYLINDER_ALT:
        CylinderBoardGraphBuilder cylinderBoardGraphBuilder = new CylinderBoardGraphBuilder();
        cylinderBoardGraphBuilder.setTiles(seed.tiles);
        cylinderBoardGraphBuilder.setSize(boardSize);
        if ( seed.gameSettings.boardTopology == BoardTopology.CYLINDER_ALT ) {
          cylinderBoardGraphBuilder.setVertical();
        }
        graph = cylinderBoardGraphBuilder.build();
        break;
      case TORUS:
        TorusBoardGraphBuilder torusBoardGraphBuilder = new TorusBoardGraphBuilder();
        torusBoardGraphBuilder.setTiles(seed.tiles);
        torusBoardGraphBuilder.setSize(boardSize);
        graph = torusBoardGraphBuilder.build();
        break;
      default :
        throw new SolutionServiceException(SolutionServiceError.INVALID_BOARD_TOPOLOGY);
    }

    } catch (GraphBuilderException gbe) {
      throw new SolutionServiceException(SolutionServiceError.INCONSISTENT_BOARD_PARAMETERS);
    }

    LOG.info("solving graph");

    Set<List<Integer>> frontier = new HashSet<List<Integer>>();
    Set<List<Integer>> closed = new HashSet<List<Integer>>();
    Set<List<Integer>> solutions = new HashSet<List<Integer>>();

    for ( int i = 0; i < graph.tiles.size(); i++ ) {
      frontier.add(Arrays.asList(i));
    }

    while ( frontier.size() != 0 ) {

      Set<List<Integer>> newFrontier = new HashSet<List<Integer>>();

      //analyze the frontier
      for ( List<Integer> subPath : frontier ) {

        //is this a word?
        if ( wordService.isLegalBoggleWord(pathToWord(subPath,graph)) ) {
          solutions.add(subPath);
        }

        //any branching paths?
        Set<Integer> neighbors = graph.getAdjacentIndicies(subPath.get(subPath.size()-1));
        for ( Integer n : neighbors ) {

          //already used tile?
          if ( subPath.contains(n) ) {
            continue;
          }

          //create branch
          List<Integer> nPath = new ArrayList<Integer>(subPath);
          nPath.add(n);

          //LOG.trace("exploring branch : " + nPath);
          //path could be word?
          if (wordService.isPartialLegalBoggleWord(pathToWord(nPath,graph))) {
            newFrontier.add(nPath);
          }

        }

        closed.addAll(frontier);
        frontier = newFrontier;
        
      }
    }

    //transform paths into BoardWord Map
    Map<String,BoardWord> answers = new HashMap<String,BoardWord>();

    for ( List<Integer> path : solutions ) {
      String pathWord = pathToWord(path,graph);
      if ( !answers.containsKey(pathWord) ) {
        Set<List<Integer>> wordPaths = new HashSet<List<Integer>>();
        wordPaths.add(path);
        answers.put(pathWord,new BoardWord(wordPaths,pathWord));
      } else {
        answers.get(pathWord).paths.add(path);
      }
    }

    return answers;

  }

  //path to word will always return lowercase words
  private String pathToWord(List<Integer> path,TileGraph tileGraph) {
    return path.stream()
      .map( x -> tileCodeStringMap.getString(tileGraph.tiles.get(x).code))
      .reduce("", (partialWord, tileChar) -> partialWord + tileChar)
      .toLowerCase();
  }

  private int getBoardSize(GameSettings settings) throws SolutionServiceException {
    switch ( settings.boardSize ) {
      case FOUR:
        return 4;
      case FIVE:
        return 5;
      default :
        LOG.warn("unhandled board size " + settings.boardSize );
        throw new SolutionServiceException(SolutionServiceError.INVALID_BOARD_TOPOLOGY);
    }
  }

}
