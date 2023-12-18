package com.keville.ReBoggled.model.lobby;

import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.relational.core.mapping.Column;

import com.keville.ReBoggled.model.user.User;

public class LobbyUserReference {
  
  @Id
  public Integer id;
  @Column("USERINFO") //rem H2 only has UPPERCASE COLUMSN AND TABLES
  public AggregateReference<User, Integer> user;

  //public LobbyUserReference(){}

  public LobbyUserReference(AggregateReference<User, Integer> user){
    this.user = user;
  }

  @Override
  public int hashCode() {
    if ( user.getId() == null ) {
      return 0;
    }
    return 31 * this.user.getId();
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

      return user.getId() == lobbyUserReference.user.getId();
    }

    return false;

  }

}
