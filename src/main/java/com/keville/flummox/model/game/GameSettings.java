package com.keville.flummox.model.game;

/* A lobby's game generation settings */
public class GameSettings {

  public BoardSize boardSize = BoardSize.FOUR;
  public BoardTopology boardTopology = BoardTopology.PLANE;
  public Boolean tileRotation = false;
  public FindRule findRule = FindRule.UNIQUE;
  public Integer duration = 180; /* in seconds */

  public GameSettings() {}

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

}
