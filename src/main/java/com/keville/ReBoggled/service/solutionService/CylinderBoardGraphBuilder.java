package com.keville.ReBoggled.service.solutionService;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.keville.ReBoggled.model.game.Tile;

public class CylinderBoardGraphBuilder extends GraphBuilder {

    Integer size;
    static Logger LOG = LoggerFactory.getLogger(CylinderBoardGraphBuilder.class);

    /* Return Horizontal Cylinder */
    public CylinderBoardGraphBuilder setSize(int size) {
      this.size = size;
      return this;
    }

    public TileGraph build() throws GraphBuilderException {

      //TODO : this check is similar to PlaneGraphBuilder
      //This duplication is bothersome. Perhaps the best abstract
      //is a nest of the classes Torus extends Cylinder extends Plane . Mobius would be it's own group
      if ( tiles == null ) {
        LOG.warn("tiles not set");
        throw new GraphBuilderException("this.tiles is null");
      }
      if ( size == null ) {
        LOG.warn("size not set");
        throw new GraphBuilderException("this.size is null");
      }
      if ( this.size * this.size != this.tiles.size() ) {
        LOG.warn("tile list and size  parameter are inconsistent");
        throw new GraphBuilderException("tile list and size  parameter are inconsistent");
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

      Predicate<Integer> inBounds           = x -> ( x >= 0 && x < tiles.size() );
      Predicate<Integer> isLeftEdge         = x -> ( x % this.size == 0 );
      Predicate<Integer> isRightEdge        = x -> ( x % this.size == this.size - 1);

      Function<Integer,Integer>  leftWrap   = x -> ( x + (size - 1) );
      Function<Integer,Integer>  rightWrap  = x -> ( x - (size - 1) );

      ///////
      //Ortho
      
      //left
      int next = i - 1;
      if ( inBounds.test(next) && !isLeftEdge.test(i) ) {
        adjacent.add(next);
      //left wrap
      } else if (isLeftEdge.test(i) ) {
        next = leftWrap.apply(i);
        adjacent.add(next);
      }

      //right
      next = i + 1;
      if ( inBounds.test(next) && !isRightEdge.test(i) ) {
        adjacent.add(next);
      //right wrap
      } else if (isRightEdge.test(i) ) {
        next = rightWrap.apply(i);
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

      ///////
      //diag

      //up left
      next = i - 1 - size;
      if ( inBounds.test(next) && !isLeftEdge.test(i) ) {
        adjacent.add(next);
      //up left wrap
      } else if (isLeftEdge.test(i) ) {
        next = leftWrap.apply(i) - size;
        if ( inBounds.test(next) ) {
          adjacent.add(next);
        }
      }

      //up right
      next = i + 1 - size;
      if ( inBounds.test(next) && !isRightEdge.test(i) ) {
        adjacent.add(next);
      //up right wrap
      } else if (isRightEdge.test(i) ) {
        next = rightWrap.apply(i) - size;
        if ( inBounds.test(next) ) {
          adjacent.add(next);
        }
      }

      //down left
      next = i - 1 + size;
      if ( inBounds.test(next) && !isLeftEdge.test(i)) {
        adjacent.add(next);
      //down left wrap
      } else if (isLeftEdge.test(i) ) {
        next = leftWrap.apply(i) + size;
        if ( inBounds.test(next) ) {
          adjacent.add(next);
        }
      }

      //down right
      next = i + 1 + size;
      if ( inBounds.test(next) && !isRightEdge.test(i)) {
        adjacent.add(next);
      //down right wrap
      } else if (isRightEdge.test(i) ) {
        next = rightWrap.apply(i) + size;
        if ( inBounds.test(next) ) {
          adjacent.add(next);
        }
      }

      return adjacent;
    }

  }

