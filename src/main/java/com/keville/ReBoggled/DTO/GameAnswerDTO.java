package com.keville.ReBoggled.DTO;

import java.time.LocalDateTime;

import com.keville.ReBoggled.model.game.GameAnswer;

/* exporting model map for GameAnswer */
public class GameAnswerDTO {

  /* keep in parity with PostGames naming convention */
  public Integer userId;
  public String word;
  public LocalDateTime answerSubmissionTime;

  public GameAnswerDTO(GameAnswer gameAnswer) {
    this.userId = gameAnswer.user.getId();
    this.word = gameAnswer.answer;;
    this.answerSubmissionTime = gameAnswer.answerSubmissionTime;
  }
}
