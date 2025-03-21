package com.keville.flummox.service.gameService.solution.graphs;

import java.util.List;

import com.keville.flummox.model.game.Tile;

public abstract class GraphBuilder {

  List<Tile> tiles;

  public GraphBuilder setTiles(List<Tile> tiles) {
    this.tiles = tiles;
    return this;
  }

  public abstract TileGraph build() throws GraphBuilderException;

  public class GraphBuilderException extends Exception {
    public GraphBuilderException(String message) {
      super(message);
    }
  }

}
