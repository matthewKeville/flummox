package com.keville.ReBoggled.model.game;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.MappedCollection;

import com.keville.ReBoggled.util.ClassicBoardGenerator;
import com.keville.ReBoggled.util.ClassicBoardGenerator.BoardGenerationException;

public class Game {

  @Transient
  private static final Logger LOG = LoggerFactory.getLogger(Game.class);

  @Id
  public Integer id;

  @Column("GAME_START")
  public LocalDateTime start;

  @Column("GAME_END")
  public LocalDateTime end;

  @MappedCollection(idColumn = "GAME")
  public Set<GameAnswer> answers = new HashSet<GameAnswer>();

  @MappedCollection(idColumn = "GAME")
  public List<Tile> tiles;

  @LastModifiedDate
  @Column("LAST_MODIFIED")
  public LocalDateTime lastModifiedDate;

  @Embedded.Nullable
  public GameSettings gameSettings;

  public  Game() {}

  public Game(GameSettings gameSettings)  {

    this.gameSettings = gameSettings;
    this.start = LocalDateTime.now();
    this.end = this.start.plusSeconds(gameSettings.duration);

    try {
      this.tiles = ClassicBoardGenerator.generate(gameSettings.boardSize);
    } catch ( BoardGenerationException bge) {
      LOG.error("failed to create board for game, reason : " + bge.getMessage());
    }

  }




}
