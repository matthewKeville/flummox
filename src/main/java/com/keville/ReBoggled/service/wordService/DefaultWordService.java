package com.keville.ReBoggled.service.wordService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DefaultWordService implements WordService {

  private static Logger LOG = LoggerFactory.getLogger(WordService.class);

  //private final String wordListPath = "./src/main/resources/words_alpha.txt";
  private final String wordListPath = "./src/main/resources/corncob_lowercase.txt";

  private List<String> words;
  private Set<String> wordSet;

  public DefaultWordService() throws IOException {
    Path path = Paths.get(wordListPath);
    words = new ArrayList<String>(Files.lines(path).toList());
    Collections.sort(words);
    wordSet = new TreeSet<String>(words);
  }

  public boolean isWord(String word) {
    word = word.toLowerCase();
    return wordSet.contains(word);
  }

  public boolean isPartialWord(String word) {
    word = word.toLowerCase();
    findPartialWords(word).stream().limit(3).forEach( x -> { LOG.info(x); });
    return findPartialWords(word).size() != 0;
  }

  public boolean isLegalBoggleWord(String word) {
    word = word.toLowerCase();
    return isWord(word) && checkLegality(word);
  }

  public boolean isPartialLegalBoggleWord(String word) {
    word = word.toLowerCase();
    return findPartialWords(word).stream().anyMatch( w -> { return checkLegality(w); });
  }

  /* OOB bug seen here ... */
  private List<String> findPartialWords(String word) {

    int match = partialBinarySearch(word,0,words.size()-1);

    if ( match == -1 ) {
      return Collections.<String>emptyList();
    }

    //determine match neighborhood
    int firstUnmatch = match-1;
    //FIXME : these logical statements are far from readable
    while ( firstUnmatch >= 0 && 
        words.get(firstUnmatch).length() >= word.length() &&
        word.equals( words.get(firstUnmatch).substring(0,word.length())) ) {
      firstUnmatch--;
    }

    int lastUnmatch = match+1;
    //FIXME : these logical statements are far from readable
    while ( firstUnmatch < words.size() &&
        words.get(lastUnmatch).length() >= word.length() &&
        word.equals( words.get(lastUnmatch).substring(0,word.length())) ) {
      lastUnmatch++;
    }

    return words.subList(firstUnmatch+1,lastUnmatch);

  }

  private int partialBinarySearch(String target,int lower, int upper) {

    LOG.trace(String.format("word :  %s lower : % d upper % d ",target,lower,upper));

    if ( lower == upper ) {
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

  /* FIXME : legality depends on the board size. In Big Boggle the word minimum
   * is 4, but in original it is 3 
  */
  private boolean checkLegality(String word) {
    //LOG.warn("WordService.checkLegality is not fully implemented");
    return word.length() >= 3;
  }
}
