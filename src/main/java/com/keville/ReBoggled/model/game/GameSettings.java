package com.keville.ReBoggled.model.game;

/* A lobby's game generation settings */
public class GameSettings {

  public BoardSize boardSize;
  public BoardTopology boardTopology;
  public FindRule findRule;
  public Integer duration; /* in seconds */

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

}
