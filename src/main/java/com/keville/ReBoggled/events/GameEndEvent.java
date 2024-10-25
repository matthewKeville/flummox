package com.keville.ReBoggled.events;

public class GameEndEvent {
  public int lobbyId;
  public GameEndEvent(int lobbyId) {
    this.lobbyId = lobbyId;
  }
}
