package com.keville.ReBoggled.model.game;

import java.time.Duration;

public class GameSettings {

  public BoardSize boardSize;
  public BoardTopology boardTopology;
  public FindRule findRule;
  public Integer duration; /* in seconds */

  //public List<SpecialTile> //wildcards
  //public Mutations         //special rules

  public GameSettings() {
    this.boardSize = BoardSize.FOUR;
    this.boardTopology = BoardTopology.PLANE;
    this.findRule = FindRule.ANY;
    this.duration = 180;
  }

  public GameSettings(
      BoardSize boardSize,
      BoardTopology boardTopology,
      FindRule findRule,
      Integer duration) {
    this.boardSize = boardSize;
    this.boardTopology = boardTopology;
    this.findRule = findRule;
    this.duration = duration;
  }

  @Override
  public boolean equals(Object object) {

    if ( object instanceof GameSettings ) {

      GameSettings gameSettings = (GameSettings) object;

      if ( !gameSettings.boardSize.equals(boardSize) ) {
        return false;
      }

      if ( !gameSettings.boardTopology.equals(boardTopology) ) {
        return false;
      }

      if ( !gameSettings.findRule.equals(findRule) ) {
        return false;
      }

      /* We don't care about duration for this.
      if ( !gameSettings.duration.equals(duration) ) {
        return false;
      }
      */

      return true;

    }

    return false;

  }


}
