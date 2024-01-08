package com.keville.ReBoggled.service.solutionService;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.keville.ReBoggled.model.game.GameSeed;
import com.keville.ReBoggled.model.game.TileCodeStringMap;
import com.keville.ReBoggled.util.GameBoardStringifier;

@Component
public class DefaultSolutionService implements SolutionService {

  public static Logger LOG = LoggerFactory.getLogger(SolutionService.class);

  @Autowired
  private TileCodeStringMap tileCodeStringMap;
  @Autowired
  private GameBoardStringifier gameBoardStringifier;

  public List<String> solve(GameSeed seed) {

    LOG.info("building graph");
    PlaneBoardGraphBuilder planeBoardGraphBuilder = new PlaneBoardGraphBuilder();
    planeBoardGraphBuilder.setTiles(seed.tiles);
    planeBoardGraphBuilder.setSize(4);
    TileGraph graph = planeBoardGraphBuilder.build();
    LOG.info(graph.toString());
    LOG.info(graph.toString());

    seed.tiles.forEach( t -> {
      LOG.info( t.toString() + " -> " + tileCodeStringMap.getString(t.code) );
    });

    LOG.info(gameBoardStringifier.stringify(seed.tiles,seed.gameSettings.boardSize,seed.gameSettings.boardTopology));

    return null;

  }

}
