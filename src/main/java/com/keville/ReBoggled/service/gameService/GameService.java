package com.keville.ReBoggled.service.gameService;

import java.time.LocalDateTime;
import java.util.Set;

import com.keville.ReBoggled.model.game.Game;
import com.keville.ReBoggled.model.lobby.Lobby;

public interface GameService {

    public Game getGame(int id) throws GameServiceException;

    public Iterable<Game> getGames();

    public boolean exists (Integer gameId);

    public Game createGame(Lobby lobby) throws GameServiceException;

    public Game addGameAnswer(Integer gameId, Integer userId, String answer) throws GameServiceException;

    public boolean isOutdated(Integer gameId,LocalDateTime lastTime) throws GameServiceException;

}
