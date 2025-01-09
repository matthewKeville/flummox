package com.keville.flummox.events;

public class GameEndEvent {
  public int lobbyId;
  public GameEndEvent(int lobbyId) {
    this.lobbyId = lobbyId;
  }
}
