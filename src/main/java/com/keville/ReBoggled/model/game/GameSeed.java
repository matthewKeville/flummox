package com.keville.ReBoggled.model.game;

import java.util.List;

//Too much data here, tiles and topology should be sufficient for Board Solve
//This should be a concept distinct from a Game
public class GameSeed {

  public List<Tile> tiles;

  public GameSeed(List<Tile> tiles) {
    this.tiles = tiles;
  }

  public GameSeed(Game game) {
    this.tiles = game.board.tiles;
  }

  @Override
  public int hashCode() {

    int sum = 31; 
    for ( Tile tile : tiles ) {
      sum *=tile.code;
    }
    return sum;

  }

  @Override
  public boolean equals(Object object) {

    if ( object instanceof GameSeed ) {

      GameSeed gameSeed = (GameSeed) object;

      //compare tile set

      if ( this.tiles.size() != gameSeed.tiles.size() ) {
        return false;
      }

      for ( int i = 0; i < this.tiles.size(); i++ ) {

        if ( !this.tiles.get(i).equals(gameSeed.tiles.get(i)) ) {
          return false;
        }

      }

      return true;

    }

    return false;

  }

}
