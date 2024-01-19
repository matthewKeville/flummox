package com.keville.ReBoggled.model.game;

import java.util.List;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.data.relational.core.mapping.MappedCollection;

@Configurable
public class Board {

  /* default behaviour of keyColumn for mapped collection in embedded entity
   * is to take the name of the embedded. Spring JDBC was trying to create a statement
   * with BOARD_KEY ... */
  @MappedCollection(idColumn = "GAME", keyColumn = "GAME_KEY")
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

  /* spring data jdbc needs this */
  public Board(){};

}
