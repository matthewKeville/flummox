package com.keville.ReBoggled.model.game;

public class Tile {

  public Integer code ;

  public Tile(Integer code) {
    this.code = code;
  }

  @Override
  public boolean equals(Object object) {

    if ( !(object instanceof Tile) ) {
      return false ;
    }

    Tile tile = (Tile) object;

    return tile.code == this.code;

  }

  @Override 
  public String toString() {
    return  " Code : " + code ;
  }

}
