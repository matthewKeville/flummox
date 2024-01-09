package com.keville.ReBoggled.service.solutionService;

import java.util.List;

import com.keville.ReBoggled.model.game.Tile;

abstract class GraphBuilder {

  List<Tile> tiles;

  public GraphBuilder setTiles(List<Tile> tiles) {
    this.tiles = tiles;
    return this;
  }

  public abstract TileGraph build();
}
