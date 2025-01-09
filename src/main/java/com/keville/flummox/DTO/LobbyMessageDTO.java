package com.keville.flummox.DTO;

import java.time.LocalDateTime;
import com.keville.flummox.model.lobby.LobbyMessage;

public class LobbyMessageDTO {

  public String username;
  public LocalDateTime sent;
  public String message;
  public boolean system = true;
  //current db constraint of 80 chars

  public LobbyMessageDTO(LobbyMessage lobbyMessage) {
    this.sent = lobbyMessage.sent;
    this.message = lobbyMessage.message;
  }

  public LobbyMessageDTO(LobbyMessage lobbyMessage,String username) {
    this.username = username;
    this.sent = lobbyMessage.sent;
    this.message = lobbyMessage.message;
    this.system = false;
  }



}
