package com.keville.ReBoggled.model.gameSummary;

import java.util.List;
import java.util.Set;

public record GameSummary(Set<GameWord> gameBoardWords,List<ScoreBoardEntry> scoreboard){};
