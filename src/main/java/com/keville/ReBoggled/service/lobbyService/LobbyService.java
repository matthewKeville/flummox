package com.keville.ReBoggled.service.lobbyService;

import java.time.LocalDateTime;

import com.keville.ReBoggled.model.lobby.Lobby;
import com.keville.ReBoggled.model.lobby.LobbyUpdate;

public interface LobbyService {

    public Iterable<Lobby> getLobbies();

    public Lobby getLobby(int id) throws LobbyServiceException;

    public Integer getLobbyOwnerId(int id) throws LobbyServiceException;

    public void addLobby(Lobby lobby);

    public Lobby addUserToLobby(Integer userId,Integer lobbyId) throws LobbyServiceException;

    public Lobby removeUserFromLobby(Integer userId,Integer lobbyId) throws LobbyServiceException;

    public Lobby transferLobbyOwnership(Integer lobbyId,Integer userId) throws LobbyServiceException;

    public Lobby update(LobbyUpdate lobbyUpdate) throws LobbyServiceException;

    public Boolean delete(Integer lobbyId) throws LobbyServiceException;

    public Lobby createNew(Integer userId) throws LobbyServiceException;

    public boolean isOutdated(Integer lobbyId,LocalDateTime lastTime) throws LobbyServiceException;

    public Lobby startGame(Integer lobbyId) throws LobbyServiceException;

    public boolean exists (Integer lobbyId);

}
