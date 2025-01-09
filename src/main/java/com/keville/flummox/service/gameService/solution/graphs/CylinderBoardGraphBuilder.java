package com.keville.flummox.service.gameService.solution.graphs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.keville.flummox.model.game.Tile;

public class CylinderBoardGraphBuilder extends PlaneBoardGraphBuilder {

    static Logger LOG = LoggerFactory.getLogger(CylinderBoardGraphBuilder.class);

    boolean isVertical = false;

    Predicate<Integer> isTopEdge           = x -> ( x / this.size == 0 );
    Predicate<Integer> isBottomEdge        = x -> ( x / this.size == this.size - 1);

    Function<Integer,Integer>  leftWrap    = x -> ( x + (size - 1) );
    Function<Integer,Integer>  rightWrap   = x -> ( x - (size - 1) );

    Function<Integer,Integer>  topWrap     = x -> ( x + (size - 1)*(size) );
    Function<Integer,Integer>  bottomWrap  = x -> ( x - (size - 1)*(size) );

    public void setVertical() {
      isVertical = true;
    }
    public void setHorizontal() {
      isVertical = false;
    }

    public TileGraph build() throws GraphBuilderException {

      validate();

      TileGraph graph = new TileGraph(tiles);

      for ( int i = 0; i < tiles.size(); i++ ) {
        for ( Integer x : adj(i) ) {
          graph.addEdge(i,x);
        }
      }

      return graph;

    }

    @Override
    protected Set<Integer> adj(int i) {

      Set<Integer> adjacent = super.adj(i);

      if ( isVertical ) {
        adjacent.addAll(adjVertical(i));
      } else {
        adjacent.addAll(adjHorizontal(i));
      }

      return adjacent;

    }

    protected Set<Integer> adjHorizontal(int i) {

      Set<Integer> adjacent = new HashSet<Integer>();

      //left wrap
      int next = -1;
      if (isLeftEdge.test(i) ) {
        next = leftWrap.apply(i);
        adjacent.add(next);
      }

      //right wrap
      if (isRightEdge.test(i) ) {
        next = rightWrap.apply(i);
        adjacent.add(next);
      }

      //up left wrap
      if (isLeftEdge.test(i) ) {
        next = leftWrap.apply(i) - size;
        if ( inBounds.test(next) ) {
          adjacent.add(next);
        }
      }

      //up right wrap
      if (isRightEdge.test(i) ) {
        next = rightWrap.apply(i) - size;
        if ( inBounds.test(next) ) {
          adjacent.add(next);
        }
      }

      //down left wrap
      if (isLeftEdge.test(i) ) {
        next = leftWrap.apply(i) + size;
        if ( inBounds.test(next) ) {
          adjacent.add(next);
        }
      }

      //down right wrap
      if (isRightEdge.test(i) ) {
        next = rightWrap.apply(i) + size;
        if ( inBounds.test(next) ) {
          adjacent.add(next);
        }
      }

      return adjacent;

    } 
    protected Set<Integer> adjVertical(int i) {

      Set<Integer> adjacent = new HashSet<Integer>();

      //up wrap
      int next = -1;
      if (isTopEdge.test(i) ) {
        next = topWrap.apply(i);
        adjacent.add(next);
      }

      //down wrap
      if (isBottomEdge.test(i) ) {
        next = bottomWrap.apply(i);
        adjacent.add(next);
      }

      //up left wrap
      if (isTopEdge.test(i) ) {
        next = topWrap.apply(i) - 1;
        if ( inBounds.test(next) ) {
          adjacent.add(next);
        }
      }

      //up right wrap
      if (isTopEdge.test(i) ) {
        next = topWrap.apply(i) + 1;
        if ( inBounds.test(next) ) {
          adjacent.add(next);
        }
      }

      //down left wrap
      if (isBottomEdge.test(i) ) {
        next = bottomWrap.apply(i) - 1;
        if ( inBounds.test(next) ) {
          adjacent.add(next);
        }
      }

      //down right wrap
      if (isBottomEdge.test(i) ) {
        next = bottomWrap.apply(i) + 1;
        if ( inBounds.test(next) ) {
          adjacent.add(next);
        }
      }

      return adjacent;

    } 

  }

