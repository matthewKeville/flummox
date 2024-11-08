package com.keville.ReBoggled.service.gameService.summary.wordSummary;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class WordSummary {

  public record Word(
    int index,
    String name,
    int points,
    Set<List<Integer>> paths
  ){}

  public record Anagram(int index){}
  public record Lemmatization(int index){}
  public record Finder(int userId,LocalDateTime time){}

  //

  public Word word;
  public List<Anagram> anagrams = new LinkedList<>();
  public List<Lemmatization> lemmatizations = new LinkedList<>();
  public List<Finder> finders = new LinkedList<>();

  public WordSummary(){}

}

