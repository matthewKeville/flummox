package com.keville.ReBoggled.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.relational.core.mapping.Column;

public class LobbyUserReference {
  
  @Id
  public Integer id;
  @Column("USERDETAIL") //rem H2 only has UPPERCASE COLUMSN AND TABLES
  public AggregateReference<User, Integer> user;

  //public LobbyUserReference(){}

  public LobbyUserReference(AggregateReference<User, Integer> user){
    this.user = user;
  }

  /*

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public AggregateReference<User, Integer> getUser() {
    return user;
  }

  public void setUser(AggregateReference<User, Integer> user) {
    this.user = user;
  };

  */

}
