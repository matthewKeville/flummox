package com.keville.ReBoggled.service.gameService.solution;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class WordValidator {

  private static Logger LOG = LoggerFactory.getLogger(WordValidator.class);
  //private static Resource wordFile = new ClassPathResource("words_alpha.txt");
  private static Resource wordFile = new ClassPathResource("corncob_lowercase.txt");
  private List<String> words;
  private Set<String> wordSet;

  public WordValidator() throws IOException {
    loadWordsFromFile();
    this.wordSet = new TreeSet<String>(words);
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
    while ( lastUnmatch < words.size() &&
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

    LOG.trace(String.format("midpoint word : %s midpoint index : %d comparison value %d  ",midWord,midpoint,comparison));

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

  /*
  https://docs.spring.io/spring-framework/reference/core/resources.html
  ---
  In order to load a file that is in the classpath, but inside a jar,
  we have to use getInputStream() to get the data , we can't get a File object,
  because it is not a real file on the file system, its in the JAR
  */
  private void loadWordsFromFile() throws IOException {
    InputStream wordFileByteStream = wordFile.getInputStream();
    words = new BufferedReader(
      new InputStreamReader(wordFileByteStream, StandardCharsets.UTF_8))
      .lines()
      .collect(Collectors.toList());
  }
}
