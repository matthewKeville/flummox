package com.keville.flummox.service.gameService.summary.wordSummary;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class UserWordSummary {

  public record Word(
    int index,
    String name,
    int points,
    int pointsAwarded,
    boolean found,
    boolean counted,
    Set<List<Integer>> paths
  ){}
  public record Anagram (
    int index,
    boolean found,
    boolean complete
  ){};
  public record Lemmatization (
    int index,
    boolean found,
    boolean complete
  ){};
  public record Finder (
    int userId,
    String username,
    LocalDateTime time,
    boolean counted
  ){};


  public Word word;
  public List<Anagram> anagrams = new LinkedList<>();
  public List<Lemmatization> lemmatizations = new LinkedList<>();
  public List<Finder> finders = new LinkedList<>();

  public UserWordSummary(){}

}

