package com.keville.ReBoggled.model.game;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.MappedCollection;

public class Game {

  @Id
  public Integer id;

  public String boardString;

  @Column("GAME_START")
  public LocalDateTime start;

  @Column("GAME_END")
  public LocalDateTime end;

  @MappedCollection(idColumn = "GAME")
  public Set<GameAnswer> answers = new HashSet<GameAnswer>();

  @Embedded.Nullable
  public GameSettings gameSettings;

  public  Game() {
    this.boardString = "auxdfpceaufgsase";
    this.start = LocalDateTime.now();
    this.end = this.start.plusSeconds(180);
    this.gameSettings = new GameSettings();
  }

  public Game(GameSettings gameSettings)  {
    this();
    this.gameSettings = gameSettings;
    this.end = this.start.plusSeconds(gameSettings.duration);
  }



}
