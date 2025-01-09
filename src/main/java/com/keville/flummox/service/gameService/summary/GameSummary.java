package com.keville.flummox.service.gameService.summary;

import java.util.List;

import com.keville.flummox.service.gameService.summary.wordSummary.UserWordSummary;

/*
public record GameSummary(Set<GameWord> gameBoardWords,List<ScoreBoardEntry> scoreboard){};
*/

public record GameSummary(
    List<UserWordSummary> wordSummaries
){};
