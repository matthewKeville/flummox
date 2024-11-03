package com.keville.ReBoggled.model.gameSummary;

import java.util.List;
import java.util.Set;

//FIXME : this isn't part of the model, it's really DTO and doesn't belong here.
public record GameSummary(Set<GameWord> gameBoardWords,List<ScoreBoardEntry> scoreboard){};
