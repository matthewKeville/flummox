package com.keville.ReBoggled.service.gameService.solution.graphs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.keville.ReBoggled.model.game.Tile;

public class TileGraph {

  public List<Tile> tiles;
  public List<Set<Integer>> adj;

  public TileGraph(List<Tile> tiles) {
    this.tiles = tiles;
    this.adj = new ArrayList<Set<Integer>>();
    for ( int i = 0; i < tiles.size(); i++ ) {
      this.adj.add(new HashSet<Integer>());
    }
  }

  public void addEdge(int indexA,int indexB) {
    addEdge(indexA,indexB,true);
  }

  public void addEdge(int indexA,int indexB,boolean undirected) {
    adj.get(indexA).add(indexB);
    if ( undirected ) {
      addEdge(indexB,indexA,false);
    }
  }

  public Set<Integer> getAdjacentIndicies(int tileIndex) {
    return adj.get(tileIndex);
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
