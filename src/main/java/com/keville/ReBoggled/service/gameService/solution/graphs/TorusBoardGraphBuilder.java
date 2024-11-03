package com.keville.ReBoggled.service.gameService.solution.graphs;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TorusBoardGraphBuilder extends CylinderBoardGraphBuilder {

    static Logger LOG = LoggerFactory.getLogger(TorusBoardGraphBuilder.class);

    //Something feels unright about this design.
    //While functional, I greatly dislike that this class has useless
    //function setHorizontal and setVertical, they make sense in Cylinder but
    //not in torus..

    @Override
    protected Set<Integer> adj(int i) {

      Set<Integer> adjacent = super.adj(i);

      adjacent.addAll(adjVertical(i));
      adjacent.addAll(adjHorizontal(i));

      return adjacent;

    }


  }

