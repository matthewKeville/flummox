package com.keville.ReBoggled.model.gameSummary;

import java.util.List;
import java.util.Set;

import com.keville.ReBoggled.model.game.BoardWord;

public class GameBoardWord{

  public String word;
  public Set<List<Integer>> paths;
  public int potentialPoints;
  public Integer firstFinder;
  public List<Integer> cofinders;

  public GameBoardWord(BoardWord boardWord,int potentialPoints,Integer firstFinder, List<Integer> cofinders) {
    this(boardWord.word,boardWord.paths,potentialPoints,firstFinder,cofinders);
  }

  public GameBoardWord (
      String word,
      Set<List<Integer>> paths,
      int potentialPoints,
      Integer firstFinder,
      List<Integer> cofinders
      ) {
    this.word = word;
    this.paths = paths;
    this.potentialPoints = potentialPoints;
    this.firstFinder = firstFinder;
    this.cofinders = cofinders;
  }

}
