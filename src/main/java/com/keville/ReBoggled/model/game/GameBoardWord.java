package com.keville.ReBoggled.model.game;

import java.util.List;
import java.util.Set;

public class GameBoardWord{

  public String word;
  public Set<List<Integer>> paths;
  public int potentialPoints;
  public Integer firstFinder;
  public int cofinders;

  public GameBoardWord(BoardWord boardWord,int potentialPoints,Integer firstFinder, int cofinders) {
    this(boardWord.word,boardWord.paths,potentialPoints,firstFinder,cofinders);
  }

  public GameBoardWord (
      String word,
      Set<List<Integer>> paths,
      int potentialPoints,
      Integer firstFinder,
      int cofinders
      ) {
    this.word = word;
    this.paths = paths;
    this.potentialPoints = potentialPoints;
    this.firstFinder = firstFinder;
    this.cofinders = cofinders;
  }

}
