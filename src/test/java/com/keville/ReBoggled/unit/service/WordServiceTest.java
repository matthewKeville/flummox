package com.keville.ReBoggled.unit.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.keville.ReBoggled.service.wordService.WordService;

@SpringBootTest
class WordServiceTest {

  @Autowired
  private WordService wordService;

  @Disabled
  @ParameterizedTest
  @ValueSource(strings = { "sardonic", "apple", "zebra", "trine" })
  void isWordReturnsTrue(String word) {
    assertTrue(wordService.isWord(word),String.format("%s is a word!",word));
  }

  @Disabled
  @ParameterizedTest
  @ValueSource(strings = { "appleman", "zkasdlfj", "xxxxx", "catdogfish" })
  void isWordReturnsFalse(String word) {
    assertFalse(wordService.isWord(word),String.format("%s is not a word!",word));
  }

  @Disabled
  @ParameterizedTest
  @ValueSource(strings = { "sard" , "m" , "mandi" , "rac", "meretric", "ho", "HO" })
  void isPartialWordReturnsTrue(String word) {
    assertTrue(wordService.isPartialWord(word),String.format("%s is a partial word!",word));
  }

  @Disabled
  @ParameterizedTest
  @ValueSource(strings = { "nxvy" , "bbh" , "cdga" , "abababab" })
  void isPartialWordReturnsFalse(String word) {
    assertFalse(wordService.isPartialWord(word),String.format("%s is not a partial word!",word));
  }


}
