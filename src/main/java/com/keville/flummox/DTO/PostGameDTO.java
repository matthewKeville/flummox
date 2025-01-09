package com.keville.flummox.DTO;

import java.time.LocalDateTime;
import java.util.List;

import com.keville.flummox.model.game.Game;
import com.keville.flummox.model.game.Tile;
import com.keville.flummox.service.gameService.summary.wordSummary.UserWordSummary;

public class PostGameDTO {

  public Integer id;
  public List<Tile> tiles;
  public boolean tileRotation;
  public LocalDateTime start;
  public LocalDateTime end;
  public List<UserWordSummary> wordSummaries;
  //public List<ScoreBoardEntry> scoreboard;

  public PostGameDTO(Game game,List<UserWordSummary> wordSummaries) {
    this.id = game.id;
    this.tiles = game.board.tiles;
    this.tileRotation = game.board.tileRotation;
    this.start = game.start;
    this.end = game.end;
    this.wordSummaries = wordSummaries;
  }

}
