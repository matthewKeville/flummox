package com.keville.ReBoggled.service.lobbyService;

import java.util.List;
import java.util.Optional;

import com.keville.ReBoggled.DTO.LobbyMessageDTO;
import com.keville.ReBoggled.DTO.LobbyNewMessageDTO;
import com.keville.ReBoggled.DTO.LobbySummaryDTO;
import com.keville.ReBoggled.model.lobby.Lobby;
import com.keville.ReBoggled.model.lobby.LobbyUpdate;
import com.keville.ReBoggled.service.exceptions.BadRequest;
import com.keville.ReBoggled.service.exceptions.EntityNotFound;
import com.keville.ReBoggled.service.exceptions.NotAuthorized;
import com.keville.ReBoggled.service.gameService.board.BoardGenerationException;

public interface LobbyService {

    public Iterable<Lobby> getLobbies();

    public Lobby getLobby(int id) throws EntityNotFound;

    public Integer getLobbyOwnerId(int id) throws EntityNotFound;

    public Integer getUserLobbyId(int id);

    public String getUserInviteLink(Integer userId) throws NotAuthorized;

    public Lobby addUserToLobby(Integer userId,Integer lobbyId,Optional<String> Token) throws EntityNotFound,BadRequest;

    public Optional<Lobby> removeUserFromLobby(Integer userId,Integer lobbyId,boolean kicked) throws EntityNotFound,BadRequest,NotAuthorized;

    public Lobby transferLobbyOwnership(Integer lobbyId,Integer userId) throws EntityNotFound;

    public Lobby update(LobbyUpdate lobbyUpdate) throws EntityNotFound,BadRequest,NotAuthorized;

    public Boolean delete(Integer lobbyId) throws EntityNotFound;

    public Lobby createNew(Integer userId) throws EntityNotFound,BadRequest;

    public Lobby startGame(Integer lobbyId) throws EntityNotFound,NotAuthorized,BoardGenerationException;

    public Lobby addMessageToLobby(LobbyNewMessageDTO lobbyNewMessageDTO,Integer lobbyId,Integer userId) throws EntityNotFound,NotAuthorized;

    public List<LobbySummaryDTO> getLobbySummaryDTOs() throws EntityNotFound;
    public LobbySummaryDTO getLobbySummaryDTO(int id) throws EntityNotFound;
    public List<LobbyMessageDTO>  getLobbyMessageDTOs(int id) throws EntityNotFound;

}
