package com.keville.ReBoggled.service.solutionService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlaneBoardGraphBuilder extends GraphBuilder {
    Integer size;
    static Logger LOG = LoggerFactory.getLogger(PlaneBoardGraphBuilder.class);
    protected Predicate<Integer> inBounds     = x -> ( x >= 0 && x < tiles.size() );
    protected Predicate<Integer> isLeftEdge   = x -> ( x % this.size == 0 );
    protected Predicate<Integer> isRightEdge  = x -> ( x % this.size == this.size - 1);

    public PlaneBoardGraphBuilder setSize(int size) {
      this.size = size;
      return this;
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

    protected void validate() throws GraphBuilderException {
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
    }
 
    protected Set<Integer> adj(int i) {

      Set<Integer> adjacent = new HashSet<Integer>();


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

