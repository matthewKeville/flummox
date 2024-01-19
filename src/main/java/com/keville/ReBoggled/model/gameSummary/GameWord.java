package com.keville.ReBoggled.model.gameSummary;

import java.util.List;
import java.util.Set;

public record GameWord(
      String word,
      Set<List<Integer>> paths,
      int points,
      List<WordFinder> finders){};


