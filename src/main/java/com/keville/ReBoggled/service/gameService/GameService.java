package com.keville.ReBoggled.service.gameService;

import com.keville.ReBoggled.DTO.GameAnswerRequestDTO;
import com.keville.ReBoggled.DTO.GameAnswerResponseDTO;
import com.keville.ReBoggled.DTO.GameDTO;
import com.keville.ReBoggled.DTO.PostGameDTO;
import com.keville.ReBoggled.model.game.Game;
import com.keville.ReBoggled.model.lobby.Lobby;
import com.keville.ReBoggled.service.exceptions.EntityNotFound;
import com.keville.ReBoggled.service.exceptions.NotAuthorized;
import com.keville.ReBoggled.service.gameService.board.BoardGenerationException;

public interface GameService {

    public Game createGame(Lobby lobby) throws BoardGenerationException,NotAuthorized;
    public GameAnswerResponseDTO submitGameAnswer(Integer gameId, Integer userId,GameAnswerRequestDTO gameAnswerRequestDTO) throws EntityNotFound,InternalError,NotAuthorized;
    public GameDTO getGameDTO(Integer gameId,Integer userId) throws EntityNotFound,NotAuthorized;
    public PostGameDTO getPostGameDTO(Integer gameId,Integer userId) throws EntityNotFound,NotAuthorized;

}
