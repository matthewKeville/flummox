package com.keville.ReBoggled.service.lobbyService;

import org.springframework.context.ApplicationEvent;

public class LobbyGameStartEvent extends ApplicationEvent {

  private int lobbyId;
  public LobbyGameStartEvent(Object source,int lobbyId) {
    super(source);
    this.lobbyId = lobbyId;
  }

  public int getLobbyId() {
    return lobbyId;
  }

}
