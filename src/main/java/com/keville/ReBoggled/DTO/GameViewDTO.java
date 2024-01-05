package com.keville.ReBoggled.DTO;

import java.time.LocalDateTime;

import com.keville.ReBoggled.model.game.Game;
import com.keville.ReBoggled.model.game.GameSettings;

public class GameViewDTO {

  public Integer id;
  public String boardString;
  public GameSettings gameSettings;
  public LocalDateTime start;
  public LocalDateTime end;

  public GameViewDTO(Game game) {
    this.id = game.id;
    this.boardString = game.boardString;
    this.start = game.start;
    this.end = game.end;
    this.gameSettings = game.gameSettings;
  }

}
