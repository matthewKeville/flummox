package com.keville.flummox.model.lobby;

import java.beans.Transient;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.core.mapping.AggregateReference;

import com.keville.flummox.model.user.User;

@Configurable
public class LobbyMessage {

  @Id
  public Integer id;

  public AggregateReference<Lobby, Integer> lobby;

  //nullable : no user -> system message
  public AggregateReference<User, Integer> user;

  public LocalDateTime sent;

  //current db constraint of 80 chars
  public String message;

  public LobbyMessage() {}

  @Transient
  public static LobbyMessage joinLobbyMessage(
      AggregateReference<Lobby,Integer> lobby,
      String username) {
    LobbyMessage message = new LobbyMessage();
    message.lobby = lobby;
    message.sent = LocalDateTime.now();
    message.message = String.format("%s has joined the lobby",username);
    return message;
  }

  @Transient
  public static LobbyMessage leaveLobbyMessage(
      AggregateReference<Lobby,Integer> lobby,
      String username) {
    LobbyMessage message = new LobbyMessage();
    message.lobby = lobby;
    message.sent = LocalDateTime.now();
    message.message = String.format("%s has left the lobby",username);
    return message;
  }

  @Transient
  public static LobbyMessage kickLobbyMessage(
      AggregateReference<Lobby,Integer> lobby,
      String username) {
    LobbyMessage message = new LobbyMessage();
    message.lobby = lobby;
    message.sent = LocalDateTime.now();
    message.message = String.format("%s has been kicked",username);
    return message;
  }

  @Transient
  public static LobbyMessage promoteLobbyMessage(
      AggregateReference<Lobby,Integer> lobby,
      String username) {
    LobbyMessage message = new LobbyMessage();
    message.lobby = lobby;
    message.sent = LocalDateTime.now();
    message.message = String.format("%s is now the lobby owner",username);
    return message;
  }


}
