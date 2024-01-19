package com.keville.ReBoggled.model.game;

import java.util.List;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.data.relational.core.mapping.MappedCollection;

@Configurable
public class Board {

  @MappedCollection(idColumn = "GAME")
  public List<Tile> tiles;

  public BoardSize boardSize;
  public BoardTopology boardTopology;

  public  Board(BoardSize boardSize, BoardTopology boardTopology, List<Tile> tiles) {
    this.boardSize = boardSize;
    this.boardTopology = boardTopology;
    this.tiles = tiles;
  }

  public  Board(BoardSize boardSize, BoardTopology boardTopology) {
    this.boardSize = boardSize;
    this.boardTopology = boardTopology;
  }

}
