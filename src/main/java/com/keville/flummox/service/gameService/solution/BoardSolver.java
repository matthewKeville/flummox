package com.keville.flummox.service.gameService.solution;

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

import com.keville.flummox.service.gameService.TileCodeStringMap;
import com.keville.flummox.service.gameService.solution.graphs.CylinderBoardGraphBuilder;
import com.keville.flummox.service.gameService.solution.graphs.GraphBuilder.GraphBuilderException;
import com.keville.flummox.service.gameService.solution.graphs.PlaneBoardGraphBuilder;
import com.keville.flummox.service.gameService.solution.graphs.TileGraph;
import com.keville.flummox.service.gameService.solution.graphs.TorusBoardGraphBuilder;
import com.keville.flummox.model.game.Board;
import com.keville.flummox.model.game.BoardTopology;

@Component
public class BoardSolver {

  public static Logger LOG = LoggerFactory.getLogger(BoardSolver.class);

  @Autowired
  private TileCodeStringMap tileCodeStringMap;

  @Autowired
  private WordValidator wordValidator;
  
  // Board : (lowercase word string : BoardWord )
  private Map<Board,Map<String,BoardWord>> solveCache = new HashMap<Board,Map<String,BoardWord>>();

  public boolean isWordInSolution(String word,Board board) throws BoardSolverException {

    if (!solveCache.containsKey(board) ) {
       solve(board);
    }

    return solveCache.get(board)
      .entrySet()
      .stream()
      .anyMatch( entry -> { return entry.getKey().equals(word); });

  }

  //return board solution
  public Map<String,BoardWord> solve(Board board) throws BoardSolverException {
    if (solveCache.containsKey(board) ) {
      LOG.trace("solve cache hit!");
      return solveCache.get(board);
    }
    LOG.debug("solve cache miss!");
    Map<String,BoardWord> solution = solveBoard(board);
    solveCache.put(board,solution);
    return solution;
  }

  //solve the board
  private Map<String,BoardWord> solveBoard(Board board) throws BoardSolverException {

    LOG.trace("building graph");
    int boardSize = getBoardSize(board);    
    final TileGraph graph;

    try {

    switch ( board.boardTopology ) {
      case PLANE:
        PlaneBoardGraphBuilder planeBoardGraphBuilder = new PlaneBoardGraphBuilder();
        planeBoardGraphBuilder.setTiles(board.tiles);
        planeBoardGraphBuilder.setSize(boardSize);
        graph = planeBoardGraphBuilder.build();
        break;
      case CYLINDER:
        //fall through
      case CYLINDER_ALT:
        CylinderBoardGraphBuilder cylinderBoardGraphBuilder = new CylinderBoardGraphBuilder();
        cylinderBoardGraphBuilder.setTiles(board.tiles);
        cylinderBoardGraphBuilder.setSize(boardSize);
        if ( board.boardTopology == BoardTopology.CYLINDER_ALT ) {
          cylinderBoardGraphBuilder.setVertical();
        }
        graph = cylinderBoardGraphBuilder.build();
        break;
      case TORUS:
        TorusBoardGraphBuilder torusBoardGraphBuilder = new TorusBoardGraphBuilder();
        torusBoardGraphBuilder.setTiles(board.tiles);
        torusBoardGraphBuilder.setSize(boardSize);
        graph = torusBoardGraphBuilder.build();
        break;
      default :
        throw new BoardSolverException(BoardSolverException.Error.INVALID_BOARD_TOPOLOGY);
    }

    } catch (GraphBuilderException gbe) {
      throw new BoardSolverException(BoardSolverException.Error.INCONSISTENT_BOARD_PARAMETERS);
    }

    LOG.trace("solving graph");

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
        if ( wordValidator.isLegalBoggleWord(pathToWord(subPath,graph)) ) {
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
          if (wordValidator.isPartialLegalBoggleWord(pathToWord(nPath,graph))) {
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
        answers.put(pathWord,new BoardWord(wordPaths,pathWord,answers.keySet().size()));
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

  private int getBoardSize(Board board) throws BoardSolverException {
    switch ( board.boardSize ) {
      case FOUR:
        return 4;
      case FIVE:
        return 5;
      case SIX:
        return 6;
      default :
        LOG.debug("unhandled board size " + board.boardSize );
        throw new BoardSolverException(BoardSolverException.Error.INVALID_BOARD_TOPOLOGY);
    }
  }

  public class BoardSolverException extends Exception {

    public Error  error;

    public BoardSolverException(Error error) {
      this.error = error;
    }

    @Override
    public String getMessage() {
      return error.toString();
    }

    public enum Error {
      INVALID_BOARD_SIZE,
      INVALID_BOARD_TOPOLOGY,
      INCONSISTENT_BOARD_PARAMETERS
    }

  }

}
