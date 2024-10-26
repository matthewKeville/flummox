package com.keville.ReBoggled.DTO;

import java.time.LocalDateTime;
import java.util.List;

import com.keville.ReBoggled.model.game.Game;
import com.keville.ReBoggled.model.game.GameSettings;
import com.keville.ReBoggled.model.game.Tile;

public class GameViewDTO {

  public Integer id;
  public List<Tile> tiles;
  public boolean tileRotation;
  public LocalDateTime start;
  public LocalDateTime end;

  public GameViewDTO(Game game) {
    this.id = game.id;
    this.tiles = game.board.tiles;
    this.tileRotation = game.board.tileRotation;
    this.start = game.start;
    this.end = game.end;
  }

}
