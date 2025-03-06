package com.keville.flummox.service.lobbyService;

import java.util.List;
import java.util.Optional;

import com.keville.flummox.DTO.LobbyDTO;
import com.keville.flummox.DTO.LobbyMessageDTO;
import com.keville.flummox.DTO.LobbyMessageRequestDTO;
import com.keville.flummox.DTO.LobbySummaryDTO;
import com.keville.flummox.DTO.LobbyUpdateRequestDTO;
import com.keville.flummox.model.lobby.Lobby;
import com.keville.flummox.service.exceptions.BadRequest;
import com.keville.flummox.service.exceptions.EntityNotFound;
import com.keville.flummox.service.exceptions.NotAuthorized;
import com.keville.flummox.service.gameService.board.BoardGenerationException;

public interface LobbyService {

    public LobbyDTO getLobbyDTO(int id) throws EntityNotFound;
    public List<LobbySummaryDTO> getLobbySummaryDTOs() throws EntityNotFound;
    public List<LobbyMessageDTO> getLobbyMessages(Integer lobbyId);

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
