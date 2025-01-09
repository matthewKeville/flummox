package com.keville.flummox.model.lobby;

import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.relational.core.mapping.Column;

import com.keville.flummox.model.user.User;

public class LobbyUserReference {
  
  @Id
  public Integer id;

  //@Column("LOBBY_ID")
  public AggregateReference<Lobby, Integer> lobby;

  //@Column("USER_ID")
  public AggregateReference<User, Integer> user;

  public LobbyUserReference(
      AggregateReference<Lobby, Integer> lobby,
      AggregateReference<User, Integer> user){
    this.lobby = lobby;
    this.user = user;
  }

  @Override
  public int hashCode() {
    if ( user.getId() == null ) {
      return 0;
    }
    return 31 * (this.user.getId()+1) * (this.lobby.getId()+1);
  }

  @Override
  public boolean equals(Object object) {
    if ( object instanceof LobbyUserReference ) {

      LobbyUserReference lobbyUserReference = (LobbyUserReference) object;

      if ( user == null ) {
        return lobbyUserReference.user == null;
      } else if (lobbyUserReference.user == null) {
        return false;
      }

      return user.getId().equals(lobbyUserReference.user.getId());
    }

    return false;

  }

}
