package com.keville.ReBoggled.model.game;

import java.util.List;
import java.util.Set;

public class BoardWord {
  public Set<List<Integer>> paths;
  public String word;

  public BoardWord(Set<List<Integer>> paths,String word) {
    this.paths = paths;
    this.word = word;
  }

  @Override
  public String toString() {
    return ""+paths.size();
  }

}
