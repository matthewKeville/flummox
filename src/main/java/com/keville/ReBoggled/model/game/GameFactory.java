package com.keville.ReBoggled.model.game;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GameFactory {

  private static final Logger LOG = LoggerFactory.getLogger(GameFactory.class);

  @Autowired
  private ClassicBoardGenerator classicBoardGenerator;

  public Game getGame(GameSettings gameSettings) {

    Game game = new Game();

    game.gameSettings = gameSettings;
    game.start = LocalDateTime.now();
    game.end = game.start.plusSeconds(gameSettings.duration);

    try {
      game.tiles = classicBoardGenerator.generate(gameSettings.boardSize);
    } catch ( BoardGenerationException bge) {
      LOG.error("failed to create board for game, reason : " + bge.getMessage());
    }

    return game;

  }

}
