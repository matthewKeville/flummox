package com.keville.ReBoggled.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.relational.core.mapping.Column;

public class LobbyUserReference {
  
  @Id
  public Integer id;
  @Column("USERINFO") //rem H2 only has UPPERCASE COLUMSN AND TABLES
  public AggregateReference<User, Integer> user;

  //public LobbyUserReference(){}

  public LobbyUserReference(AggregateReference<User, Integer> user){
    this.user = user;
  }

}
