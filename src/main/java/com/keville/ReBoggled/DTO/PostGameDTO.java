package com.keville.ReBoggled.DTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.keville.ReBoggled.model.game.Game;
import com.keville.ReBoggled.model.game.Tile;
import com.keville.ReBoggled.model.gameSummary.ScoreBoardEntry;

public class PostGameDTO {

  //public game information
  public Integer id;
  public List<Tile> tiles;
  public boolean tileRotation;
  public LocalDateTime start;
  public LocalDateTime end;
  //end game info
  public Set<GameWordDTO> words;
  public List<ScoreBoardEntry> scoreboard;

  public PostGameDTO(Game game,Set<GameWordDTO> words,List<ScoreBoardEntry> scoreboard) {
    this.id = game.id;
    this.tiles = game.board.tiles;
    this.tileRotation = game.board.tileRotation;
    this.start = game.start;
    this.end = game.end;
    this.words = words;
    this.scoreboard = scoreboard;
  }

}
