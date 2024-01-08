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

    Comparator<String> subWordComparator = new Comparator<String>() {
      public int compare(String s1, String s2) {

        //fallback if not qualified for partial comparison
        if ( s1.length() < word.length() || s2.length() < word.length() ) {
          return s1.compareTo(s1);
        }

        return s1.substring(0,word.length()).compareTo(s2.substring(0,word.length()));
      }
    };

    int match = Collections.binarySearch(words,word,subWordComparator);
    if ( match == -1 ) {
      return Collections.<String>emptyList();
    }

    //determine match neighborhood
    int firstMatch = match-1;
    while ( firstMatch >= 0 && subWordComparator.compare(word,words.get(firstMatch)) == 0) {
      firstMatch--;
    }
    int lastMatch = match+1;
    while ( lastMatch < words.size() && subWordComparator.compare(word,words.get(lastMatch)) == 0) {
      lastMatch++;
    }

    return words.subList(firstMatch,lastMatch+1);

  }

  private boolean checkLegality(String word) {
    LOG.warn("WordService.checkLegality is not implemented, returning true");
    return true;
  }
}
