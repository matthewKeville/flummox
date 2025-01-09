package com.keville.flummox.model.game;

public class Tile {

  public static int RotateDefault = 0;
  public static int RotateLeft = 90;
  public static int RotateRight = 180;
  public static int RotateDown = 270;

  public Integer code ;
  public Integer rotation = RotateDefault; /* in degrees */

  public Tile() {}

  public Tile(Integer code) {
    this.code = code;
  }

  public Tile(Integer code,Integer rotation) {
    this.code = code;
    this.rotation = rotation;
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
  public int hashCode() {
    return 31 * this.code;
  }

  @Override 
  public String toString() {
    return  " Code : " + code ;
  }

}
