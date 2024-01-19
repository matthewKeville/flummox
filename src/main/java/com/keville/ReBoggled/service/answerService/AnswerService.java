package com.keville.ReBoggled.service.answerService;

import com.keville.ReBoggled.model.game.Game;

public interface AnswerService {
  public boolean isValidWord(String word,Game game);
}
