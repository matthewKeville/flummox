package com.keville.ReBoggled.DTO;

import java.util.List;
import java.util.Set;

import com.keville.ReBoggled.model.gameSummary.WordFinder;

/* Export of GameWord for users perspective */
public record GameWordDTO(
    String word,
    Set<List<Integer>> paths,
    List<WordFinder> finders,
    int points,
    //with respect to a user
    boolean found,
    boolean counted
){};
