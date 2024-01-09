package com.keville.ReBoggled.service.solutionService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.keville.ReBoggled.model.game.GameSeed;
import com.keville.ReBoggled.model.game.TileCodeStringMap;
import com.keville.ReBoggled.service.wordService.WordService;
import com.keville.ReBoggled.util.GameBoardStringifier;

@Component
public class DefaultSolutionService implements SolutionService {

  public static Logger LOG = LoggerFactory.getLogger(SolutionService.class);

  @Autowired
  private TileCodeStringMap tileCodeStringMap;
  @Autowired
  private GameBoardStringifier gameBoardStringifier;
  @Autowired
  private WordService wordService;

  public List<String> solve(GameSeed seed) {

    //TODO Graph buildl respects topology
    LOG.info("building graph");
    PlaneBoardGraphBuilder planeBoardGraphBuilder = new PlaneBoardGraphBuilder();
    planeBoardGraphBuilder.setTiles(seed.tiles);
    planeBoardGraphBuilder.setSize(4);
    TileGraph graph = planeBoardGraphBuilder.build();

    //DEBUG info
    LOG.info(graph.toString());
    LOG.info(graph.toString());
    seed.tiles.forEach( t -> {
      LOG.info( t.toString() + " -> " + tileCodeStringMap.getString(t.code) );
    });
    LOG.info(gameBoardStringifier.stringify(seed.tiles,seed.gameSettings.boardSize,seed.gameSettings.boardTopology));

    LOG.info("solving");

    //solve for 1 tile
    Set<List<Integer>> frontier = new HashSet<List<Integer>>();
    Set<List<Integer>> closed = new HashSet<List<Integer>>();
    Set<List<Integer>> solutions = new HashSet<List<Integer>>();

    for ( int i = 0; i < graph.tiles.size(); i++ ) {
      frontier.add(Arrays.asList(i));
    }

    int dbgCounter = 0;

    while ( frontier.size() != 0 ) {

      //LOG.debug("dbg counter : " + dbgCounter );

      Set<List<Integer>> newFrontier = new HashSet<List<Integer>>();

      //analyze the frontier
      for ( List<Integer> subPath : frontier ) {

        //LOG.debug("evaluating subpath : " + subPath);

        //is this a word?
        if ( wordService.isLegalBoggleWord(pathToWord(subPath,graph)) ) {
          solutions.add(subPath);
        }

        //any branching paths?
        Set<Integer> neighbors = graph.getAdjacentIndicies(subPath.get(subPath.size()-1));
        for ( Integer n : neighbors ) {

          //create branch
          List<Integer> nPath = new ArrayList<Integer>(subPath);
          nPath.add(n);

          //I don't think we can explore the same path twice...
          /*
          //already explored?
          if (closed.contains(nPath)) {
            LOG.debug("branch already explored : " + nPath);
            continue;
          }
          */

          //LOG.debug("exploring branch : " + nPath);
          //path could be word?
          if (wordService.isPartialLegalBoggleWord(pathToWord(nPath,graph))) {
            newFrontier.add(nPath);
          }

        }

        closed.addAll(frontier);
        frontier = newFrontier;
        
      }
    }

    //stream.toList() is immutable
    return new ArrayList<String>(solutions.stream().map( path -> pathToWord(path,graph) ).distinct().toList());

  }

  private String pathToWord(List<Integer> path,TileGraph tileGraph) {
    return path.stream()
      .map( x -> tileCodeStringMap.getString(tileGraph.tiles.get(x).code))
      .reduce("", (partialWord, tileChar) -> partialWord + tileChar);
  }

}
