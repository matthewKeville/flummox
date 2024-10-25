package com.keville.ReBoggled.DTO;

import java.time.LocalDateTime;
import java.util.List;

import com.keville.ReBoggled.model.game.GameSettings;
import com.keville.ReBoggled.model.game.GameUserReference;
import com.keville.ReBoggled.model.lobby.Lobby;

public class LobbySummaryDTO {

    //Lobby Locals
    public Integer id;
    public String name;
    public Integer capacity;
    public Boolean isPrivate;
    public LocalDateTime lastModifiedDate;
    public GameSettings gameSettings;

    //Game instance
    public Integer gameId;
    public Boolean gameActive = false;

    //Associated Users
    public LobbyUserDTO owner;
    /* users in the lobby */
    public List<LobbyUserDTO> users;
    /* users in the active game */
    public List<LobbyUserDTO> gameUsers; 

    public LobbySummaryDTO(Lobby lobby) {
      this.id = lobby.id;
      this.name = lobby.name;
      this.capacity = lobby.capacity;
      this.isPrivate = lobby.isPrivate;
      this.gameSettings = lobby.gameSettings;
      this.lastModifiedDate = lobby.lastModifiedDate;
    }

}
