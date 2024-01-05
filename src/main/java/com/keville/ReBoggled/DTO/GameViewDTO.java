package com.keville.ReBoggled.DTO;

import java.time.LocalDateTime;
import java.util.List;

import com.keville.ReBoggled.model.game.Game;
import com.keville.ReBoggled.model.game.GameSettings;
import com.keville.ReBoggled.model.game.Tile;

public class GameViewDTO {

  public Integer id;
  public List<Tile> tiles;
  public GameSettings gameSettings;
  public LocalDateTime start;
  public LocalDateTime end;

  public GameViewDTO(Game game) {
    this.id = game.id;
    this.tiles = game.tiles;
    this.start = game.start;
    this.end = game.end;
    this.gameSettings = game.gameSettings;
  }

}
