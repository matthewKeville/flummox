package com.keville.ReBoggled.service.gameService;

import com.keville.ReBoggled.DTO.GameDTO;
import com.keville.ReBoggled.DTO.PostGameDTO;
import com.keville.ReBoggled.model.game.Game;
import com.keville.ReBoggled.model.lobby.Lobby;

public interface GameService {

    public Game createGame(Lobby lobby) throws GameServiceException;
    public Game addGameAnswer(Integer gameId, Integer userId, String answer) throws GameServiceException;
    public GameDTO getGameDTO(Integer gameId,Integer userId) throws GameServiceException;
    public PostGameDTO getPostGameDTO(Integer gameId,Integer userId) throws GameServiceException;

}
