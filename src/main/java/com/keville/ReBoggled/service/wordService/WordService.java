package com.keville.ReBoggled.service.wordService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class WordService {

  private static Logger LOG = LoggerFactory.getLogger(WordService.class);

  //Not sure why not working ... ugh
  /*
  @Value("${keville.service.wordService.wordListPath}")
  private String wordListPath;
  */
  private final String wordListPath = "./src/main/resources/words_alpha.txt";

  //private String[] words;
  private List<String> words;
  private Set<String> wordSet;

  public WordService() throws IOException {
    Path path = Paths.get(wordListPath);
    words = new ArrayList<String>(Files.lines(path).toList()); //stream.toList() is immutable
    LOG.info("parsed words");
    Collections.sort(words);
    LOG.info("sorting worked");
    wordSet = new TreeSet<String>(words);
  }

  public boolean isWord(String word) {
    return wordSet.contains(word);
  }

  public boolean isPartialWord(String word) {
    LOG.info("partials sample of : " + word);
    findPartialWords(word).stream().limit(3).forEach( x -> { LOG.info(x); });
    return findPartialWords(word).size() != 0;
  }

  public boolean isLegalBoggleWord(String word) {
    return isWord(word) && checkLegality(word);
  }

  public boolean isPartialLegalBoggleWord(String word) {
    return findPartialWords(word).stream().anyMatch( w -> { return isLegalBoggleWord(w); });
  }

  //internal

  private List<String> findPartialWords(String word) {

    int match = partialBinarySearch(word,0,words.size()-1);

    if ( match == -1 ) {
      return Collections.<String>emptyList();
    }

    //determine match neighborhood
    int firstUnmatch = match-1;
    while ( firstUnmatch >= 0 && word.equals( words.get(firstUnmatch).substring(0,word.length())) ) {
      firstUnmatch--;
    }

    int lastUnmatch = match+1;
    while ( firstUnmatch < words.size() && word.equals( words.get(lastUnmatch).substring(0,word.length())) ) {
      lastUnmatch++;
    }

    return words.subList(firstUnmatch+1,lastUnmatch);

  }

  private int partialBinarySearch(String target,int lower, int upper) {

    LOG.trace(String.format("word :  %s lower : % d upper % d ",target,lower,upper));

    if ( lower == upper ) {
      LOG.info("no match");
      return -1;
    }

    int midpoint = (upper - lower) / 2 + lower;


    String midWord = words.get(midpoint);

    int comparison = 0;

    //fallback if not qualified for full partial comparison
    if ( midWord.length() < target.length() ) {
      comparison = target.compareTo(midWord);
    } else {
      comparison =  target.compareTo(midWord.substring(0,target.length()));
    }

    LOG.trace(String.format("midpoint word : %s midpoint index : %d comparison value %d ",midWord,midpoint,comparison));

    if ( comparison < 0) {
      upper = (upper - lower != 1) ? midpoint : midpoint-1;
      return partialBinarySearch(target, lower, upper);
    }

    if ( comparison > 0) {
      lower = (upper - lower != 1) ? midpoint : midpoint+1;
      return partialBinarySearch(target, lower, upper);
    }
    
    return midpoint;
    

  }

  private boolean checkLegality(String word) {
    LOG.warn("WordService.checkLegality is not implemented, returning true");
    return true;
  }
}
