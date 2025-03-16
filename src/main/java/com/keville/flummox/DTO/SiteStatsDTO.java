package com.keville.flummox.DTO;

public record SiteStatsDTO(
    long onlineUserCount,
    long lobbiesCount,
    long gamesPlayed
    ) {};
