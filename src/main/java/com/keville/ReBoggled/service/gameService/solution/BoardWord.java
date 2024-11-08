package com.keville.ReBoggled.service.gameService.solution;

import java.util.List;
import java.util.Set;

public class BoardWord {
  public Set<List<Integer>> paths;
  public String word;
  public int index;

  public BoardWord(Set<List<Integer>> paths,String word,int index) {
    this.paths = paths;
    this.word = word;
    this.index = index;
  }

  @Override
  public String toString() {
    return ""+paths.size();
  }

}
