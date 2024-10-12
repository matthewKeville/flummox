package com.keville.ReBoggled.DTO;

import java.util.Set;

import com.keville.ReBoggled.model.game.Game;

public class GameUserSummaryDTO {

  //public game information
  public GameViewDTO gameViewDTO;
  //answers for the requesting user
  public Set<GameAnswerDTO> answers;

  public GameUserSummaryDTO(Game game,Set<GameAnswerDTO> answers) {
    gameViewDTO = new GameViewDTO(game);
    this.answers = answers;
  }

}
