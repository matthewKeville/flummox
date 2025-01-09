package com.keville.flummox.service.gameService.summary.wordSummary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import com.keville.flummox.model.game.GameAnswer;
import com.keville.flummox.repository.UserRepository;
import com.keville.flummox.model.game.FindRule;
import com.keville.flummox.service.gameService.solution.BoardWord;
import com.keville.flummox.service.gameService.summary.wordSummary.WordSummary.Word;

@Component
public class WordSummarizer {

  @Autowired
  private UserRepository users;

  public List<WordSummary> getWordSummaries(Map<String,BoardWord> solution,Set<GameAnswer> answers) {

    List<WordSummary> wordSummaries = new LinkedList<WordSummary>(){};

    for ( BoardWord boardWord : solution.values() ) {

      WordSummary summary = new WordSummary();

      // Word

      int points = boardWord.word.length();
      Word word = new WordSummary.Word(boardWord.index,boardWord.word,points,boardWord.paths);

      summary.word = word;

      // Anagrams

      // Lemmatizations

      // Finders

      summary.finders = answers
        .stream()
        .filter( ga -> ga.answer.equals(boardWord.word) )
        .map( ga -> new WordSummary.Finder(ga.user.getId(),ga.answerSubmissionTime) )
        .toList();

      wordSummaries.add(summary);

    }
      
    return wordSummaries;

  }

  public List<UserWordSummary> getUserWordSummaries(FindRule findRule,List<WordSummary> wordSummaries,int userId) {

    List<UserWordSummary> userWordSummaries = new LinkedList<UserWordSummary>();

    for ( WordSummary wordSummary : wordSummaries ) {

      UserWordSummary summary = new UserWordSummary();

      // Word
  
      boolean found = wordSummary.finders.stream().anyMatch( f -> f.userId() == userId);
      boolean counted;

      if ( found ) {
        switch ( findRule ) {
          case ANY:
            counted = true;
            break;
          case FIRST:
            counted = wordSummary.finders
              .stream()
              .sorted( (fa,fb) -> fa.time()
              .compareTo(fb.time()) )
              .findFirst()
              .get().userId() == userId;
            break;
          case UNIQUE:
            counted = wordSummary.finders.size() == 1;
          default:
            counted = false;
        }
      } else {
        counted = false;
      }

      int pointsAwarded = 0;
      if ( counted ) {
        pointsAwarded = wordSummary.word.points();
      }

      summary.word = new UserWordSummary.Word(
        wordSummary.word.index(),
        wordSummary.word.name(),
        wordSummary.word.points(),
        pointsAwarded,
        found,
        counted,
        wordSummary.word.paths()
      );

      Function<Integer,Boolean> finderCounted = (uid) -> {
        switch ( findRule ) {
          default:
            return false;
          case ANY:
            return true;
          case UNIQUE:
            return wordSummary.finders.size() == 1;
          case FIRST:
            return wordSummary.finders
              .stream()
              .sorted( (fa,fb) -> fa.time()
              .compareTo(fb.time()) )
              .findFirst()
              .get().userId() == uid;
        }
      };

      // Finders
      summary.finders = wordSummary.finders.stream().map( wsf -> {
        return new UserWordSummary.Finder(
          wsf.userId(),
          users.findById(wsf.userId()).get().username,
          wsf.time(),
          finderCounted.apply(wsf.userId())
        );
      }).toList();

      userWordSummaries.add(summary);

    }

    return userWordSummaries;

  }


}
