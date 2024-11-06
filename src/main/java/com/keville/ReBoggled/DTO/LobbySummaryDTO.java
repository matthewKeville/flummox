package com.keville.ReBoggled.DTO;

import com.keville.ReBoggled.model.lobby.Lobby;

public class LobbySummaryDTO {

    //Lobby Locals
    public Integer id;
    public String name;
    public Boolean isPrivate;
    public Integer playerCount;
    public Integer capacity;

    public LobbySummaryDTO(Lobby lobby) {
      this.id = lobby.id;
      this.name = lobby.name;
      this.isPrivate = lobby.isPrivate;
      this.playerCount = lobby.users.size();
      this.capacity = lobby.capacity;
    }

}
