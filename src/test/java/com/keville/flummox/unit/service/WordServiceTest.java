package com.keville.flummox.unit.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.keville.flummox.service.gameService.solution.WordValidator;

//FIXME : We don't need the spring context for this
@SpringBootTest
class WordValidatorTest {

  @Autowired
  private WordValidator wordValidator;

  @Disabled
  @ParameterizedTest
  @ValueSource(strings = { "sardonic", "apple", "zebra", "trine" })
  void isWordReturnsTrue(String word) {
    assertTrue(wordValidator.isWord(word), String.format("%s is a word!", word));
  }

  @Disabled
  @ParameterizedTest
  @ValueSource(strings = { "appleman", "zkasdlfj", "xxxxx", "catdogfish" })
  void isWordReturnsFalse(String word) {
    assertFalse(wordValidator.isWord(word), String.format("%s is not a word!", word));
  }

  @Disabled
  @ParameterizedTest
  @ValueSource(strings = { "sard", "m", "mandi", "rac", "meretric", "ho", "HO" })
  void isPartialWordReturnsTrue(String word) {
    assertTrue(wordValidator.isPartialWord(word), String.format("%s is a partial word!", word));
  }

  @Disabled
  @ParameterizedTest
  @ValueSource(strings = { "nxvy", "bbh", "cdga", "abababab" })
  void isPartialWordReturnsFalse(String word) {
    assertFalse(wordValidator.isPartialWord(word), String.format("%s is not a partial word!", word));
  }

}
