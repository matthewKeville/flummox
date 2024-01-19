package com.keville.ReBoggled.DTO;

import java.util.List;
import java.util.Set;

import com.keville.ReBoggled.model.gameSummary.ScoreBoardEntry;

public record PostGameUserViewDTO(GameViewDTO gameViewDTO,List<ScoreBoardEntry> scoreboard,Set<GameWordDTO> words){}
