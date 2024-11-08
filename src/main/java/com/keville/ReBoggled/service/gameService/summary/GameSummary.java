package com.keville.ReBoggled.service.gameService.summary;

import java.util.List;

import com.keville.ReBoggled.service.gameService.summary.wordSummary.UserWordSummary;

/*
public record GameSummary(Set<GameWord> gameBoardWords,List<ScoreBoardEntry> scoreboard){};
*/

public record GameSummary(
    List<UserWordSummary> wordSummaries
){};
