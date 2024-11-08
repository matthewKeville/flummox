package com.keville.ReBoggled.DTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.keville.ReBoggled.model.game.Game;
import com.keville.ReBoggled.model.game.Tile;

public class GameDTO {

  public Integer id;
  public List<Tile> tiles;
  public boolean tileRotation;
  public LocalDateTime start;
  public LocalDateTime end;

  public Set<String> answers;

  public GameDTO(Game game,Set<String> answers) {
    this.id = game.id;
    this.tiles = game.board.tiles;
    this.tileRotation = game.board.tileRotation;
    this.start = game.start;
    this.end = game.end;
    this.answers = answers;
  }

}
