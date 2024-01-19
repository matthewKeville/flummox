package com.keville.ReBoggled.model.gameSummary;

//A users view into words in the game
public class UserGameBoardWord extends GameBoardWord {

  public int actualPoints;
  public boolean found;

  public UserGameBoardWord (GameBoardWord gameBoardWord,int actualPoints,boolean found) {
    super(gameBoardWord.word,gameBoardWord.paths,gameBoardWord.potentialPoints,gameBoardWord.firstFinder,gameBoardWord.cofinders);
    this.actualPoints = actualPoints;
    this.found = found;
  }
}
