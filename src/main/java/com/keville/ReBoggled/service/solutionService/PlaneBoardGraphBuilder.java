package com.keville.ReBoggled.service.solutionService;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.keville.ReBoggled.model.game.Tile;

public class PlaneBoardGraphBuilder extends GraphBuilder {
    Integer size;
    static Logger LOG = LoggerFactory.getLogger(PlaneBoardGraphBuilder.class);

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
      Predicate<Integer> isLeftEdge   = x -> ( x % this.size == 0 );
      Predicate<Integer> isRightEdge  = x -> ( x % this.size == this.size - 1);

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

