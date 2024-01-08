package com.keville.ReBoggled.service.solutionService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.keville.ReBoggled.model.game.GameSeed;
import com.keville.ReBoggled.model.game.Tile;
import com.keville.ReBoggled.model.game.TileCodeStringMap;
import com.keville.ReBoggled.util.GameBoardStringifier;

@Component
public class SolutionService {

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

  class TileGraph {

    List<Tile> tiles;
    List<Set<Integer>> adj;

    public TileGraph(List<Tile> tiles) {
      this.tiles = tiles;
      this.adj = new ArrayList<Set<Integer>>();
      for ( int i = 0; i < tiles.size(); i++ ) {
        this.adj.add(new HashSet<Integer>());
      }
    }

    void addEdge(int indexA,int indexB) {
      addEdge(indexA,indexB,true);
    }

    void addEdge(int indexA,int indexB,boolean undirected) {
      adj.get(indexA).add(indexB);
      if ( undirected ) {
        addEdge(indexB,indexA,false);
      }
    }

    @Override
    public String toString() {
      String result = "";
      for ( int i = 0; i < adj.size(); i++ ) {
        for ( Integer edge : adj.get(i) ) {
        result += "\n(" + i + "," + edge + ")";
        }
      }
      return result;
    }

  }

  abstract class GraphBuilder {
    List<Tile> tiles;

    public GraphBuilder setTiles(List<Tile> tiles) {
      this.tiles = tiles;
      return this;
    }

    public abstract TileGraph build();
  }

  class PlaneBoardGraphBuilder extends GraphBuilder {
    Integer size;

    public PlaneBoardGraphBuilder setSize(int size) {
      this.size = size;
      return this;
    }

    public TileGraph build() {
      if ( tiles == null ) {
        LOG.warn("tiles not set, generated empty graph");
        return new TileGraph(new ArrayList<Tile>());
      }
      if ( tiles == null ) {
        LOG.warn("size not set, using default size 4");
        size = 4;
      }

      TileGraph graph = new TileGraph(tiles);

      for ( int i = 0; i < tiles.size(); i++ ) {
        for ( Integer x : adj(i) ) {
          graph.addEdge(i,x);
        }
      }

      return graph;

    }
 
    private List<Integer> adj(int i) {

      List<Integer> adjacent = new ArrayList<Integer>();

      Predicate<Integer> inBounds     = x -> ( x >= 0 && x < tiles.size() );
      Predicate<Integer> isLeftEdge   = x -> ( x % size != 0 );
      Predicate<Integer> isRightEdge  = x -> ( x % size != size -1);

      //Ortho
      
      //left
      int next = i - 1;
      if ( inBounds.test(next) && !isLeftEdge.test(i) ) {
        adjacent.add(next);
      }

      //right
      next = i + 1;
      if ( inBounds.test(next) && !isRightEdge.test(i) ) {
        adjacent.add(next);
      }

      //up
      next = i - size;
      if ( inBounds.test(next) ) {
        adjacent.add(next);
      }

      //down
      next = i + size;
      if ( inBounds.test(next) ) {
        adjacent.add(next);
      }

      //diag

      //up left
      next = i - 1 - size;
      if ( inBounds.test(next) && !isLeftEdge.test(i) ) {
        adjacent.add(next);
      }

      //up right
      next = i + 1 - size;
      if ( inBounds.test(next) && !isRightEdge.test(i) ) {
        adjacent.add(next);
      }

      //down left
      next = i - 1 + size;
      if ( inBounds.test(next) && !isLeftEdge.test(i)) {
        adjacent.add(next);
      }

      //down right
      next = i + 1 + size;
      if ( inBounds.test(next) && !isRightEdge.test(i)) {
        adjacent.add(next);
      }

      return adjacent;
    }

  }

  class TorusBoardGraphBuilder extends GraphBuilder {
    public TileGraph build() {
      return null;
    }
  }

  class CylinderBoardGraphBuilder extends GraphBuilder {
    public TileGraph build() {
      return null;
    }
  }

}
