package com.keville.ReBoggled.service.lobbyService;

import java.util.List;
import java.util.Optional;

import com.keville.ReBoggled.DTO.LobbyDTO;
import com.keville.ReBoggled.DTO.LobbyMessageRequestDTO;
import com.keville.ReBoggled.DTO.LobbySummaryDTO;
import com.keville.ReBoggled.DTO.LobbyUpdateRequestDTO;
import com.keville.ReBoggled.model.lobby.Lobby;
import com.keville.ReBoggled.service.exceptions.BadRequest;
import com.keville.ReBoggled.service.exceptions.EntityNotFound;
import com.keville.ReBoggled.service.exceptions.NotAuthorized;
import com.keville.ReBoggled.service.gameService.board.BoardGenerationException;

public interface LobbyService {

    public LobbyDTO getLobbyDTO(int id) throws EntityNotFound;
    public List<LobbySummaryDTO> getLobbySummaryDTOs() throws EntityNotFound;

    public Lobby create() throws EntityNotFound,BadRequest;
    public Lobby update(Integer id,LobbyUpdateRequestDTO lobbyUpdateDTO) throws EntityNotFound,BadRequest,NotAuthorized;
    public Boolean delete(Integer lobbyId) throws EntityNotFound;
    public Lobby start(Integer lobbyId) throws EntityNotFound,NotAuthorized,BoardGenerationException;

    public String getInviteLink(Integer lobbyId) throws NotAuthorized;
    public void leave(Integer lobbyId) throws EntityNotFound,BadRequest;
    public void join(Integer lobbyId,Optional<String> Token) throws EntityNotFound,NotAuthorized,BadRequest;
    public void kick(Integer lobbyId,Integer userId) throws EntityNotFound,NotAuthorized,BadRequest;
    public void promote(Integer lobbyId,Integer userId) throws EntityNotFound,NotAuthorized,BadRequest;
    public Lobby addMessage(Integer lobbyId,LobbyMessageRequestDTO lobbyMessageRequestDTO) throws EntityNotFound,NotAuthorized;


}
