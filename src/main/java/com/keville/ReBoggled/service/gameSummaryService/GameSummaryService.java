package com.keville.ReBoggled.service.gameSummaryService;

import com.keville.ReBoggled.model.game.Game;
import com.keville.ReBoggled.model.gameSummary.GameSummary;

public interface GameSummaryService {
  public GameSummary getSummary(Game game);
}
