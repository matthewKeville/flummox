package com.keville.ReBoggled.service.answerService;

import java.util.List;
import java.util.Set;

import com.keville.ReBoggled.model.game.Game;
import com.keville.ReBoggled.model.game.ScoreBoardEntry;
import com.keville.ReBoggled.model.game.UserGameBoardWord;
import com.keville.ReBoggled.model.user.User;

public interface AnswerService {
  public boolean isValidWord(String word,Game game);
  public Set<UserGameBoardWord> getUserGameBoardWords(Game game,User user) throws AnswerServiceException;
  public List<ScoreBoardEntry> getScoreBoard(Game game) throws AnswerServiceException;
}
