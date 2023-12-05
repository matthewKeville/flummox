package com.keville.ReBoggled.DTO;

import java.util.List;

import com.keville.ReBoggled.model.GameSettings;
import com.keville.ReBoggled.model.Lobby;
import com.keville.ReBoggled.model.User;

public class LobbyDTO {

    public Integer id;
    public String name;
    public int capacity;
    public Boolean isPrivate;
    public GameSettings gameSettings;

    public LobbyUserDTO owner;
    public List<LobbyUserDTO> users;

    public LobbyDTO(Lobby lobby) {
      this.id = lobby.getId();
      this.name = lobby.getName();
      this.capacity = lobby.getCapacity();
      this.isPrivate = lobby.getIsPrivate();
      this.gameSettings = lobby.getGameSettings();
    }

}
