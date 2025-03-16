package com.keville.flummox.service.statsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.keville.flummox.DTO.SiteStatsDTO;
import com.keville.flummox.repository.GameRepository;
import com.keville.flummox.repository.LobbyRepository;
import com.keville.flummox.repository.UserRepository;
import com.keville.flummox.session.SessionAuthenticationMap;

@Component
public class DefaultStatsService implements StatsService {

  UserRepository users;
  LobbyRepository lobbies;
  GameRepository games;

  public DefaultStatsService(
      @Autowired UserRepository userRepository, 
      @Autowired LobbyRepository lobbyRepository,
      @Autowired GameRepository gamesRepository) {
      this.users = userRepository;
      this.lobbies = lobbyRepository;
      this.games = gamesRepository;
  }

  public SiteStatsDTO getStats() {

    long onlineUserCount = SessionAuthenticationMap.onlineCount();
    long lobbiesCount = lobbies.count();
    long gamesPlayed = games.count();

    return new SiteStatsDTO(onlineUserCount, lobbiesCount, gamesPlayed);

  }

}
