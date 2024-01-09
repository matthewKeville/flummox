package com.keville.ReBoggled.DTO;

import java.util.Set;

import com.keville.ReBoggled.model.game.Game;
import com.keville.ReBoggled.model.game.GameAnswer;

public class GameUserViewDTO {

  //public game information
  public GameViewDTO gameViewDTO;
  //answers for the requesting user
  public Set<GameAnswer> answers;

  public GameUserViewDTO(Game game,Set<GameAnswer> answers) {
    gameViewDTO = new GameViewDTO(game);
    this.answers = answers;
  }

}