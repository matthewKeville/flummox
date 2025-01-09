package com.keville.flummox.service.gameService;

import com.keville.flummox.DTO.GameAnswerRequestDTO;
import com.keville.flummox.DTO.GameAnswerResponseDTO;
import com.keville.flummox.DTO.GameDTO;
import com.keville.flummox.DTO.PostGameDTO;
import com.keville.flummox.model.game.Game;
import com.keville.flummox.model.lobby.Lobby;
import com.keville.flummox.service.exceptions.EntityNotFound;
import com.keville.flummox.service.exceptions.NotAuthorized;
import com.keville.flummox.service.gameService.board.BoardGenerationException;

public interface GameService {

    public Game createGame(Lobby lobby) throws BoardGenerationException,NotAuthorized;
    public GameAnswerResponseDTO submitGameAnswer(Integer gameId, Integer userId,GameAnswerRequestDTO gameAnswerRequestDTO) throws EntityNotFound,InternalError,NotAuthorized;
    public GameDTO getGameDTO(Integer gameId,Integer userId) throws EntityNotFound,NotAuthorized;
    public PostGameDTO getPostGameDTO(Integer gameId,Integer userId) throws EntityNotFound,NotAuthorized;

}
