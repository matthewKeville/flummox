package com.keville.ReBoggled.model.gameSummary;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

//A users view into words in the game
public class GameSummary {

  public List<GameBoardWord> gameBoardWords;

  public record Finder(Integer id,LocalDateTime time){}

  public record GameWord(
      String word,
      Set<List<Integer>> paths,
      int points,
      List<Finder> finders
  ){};

}
