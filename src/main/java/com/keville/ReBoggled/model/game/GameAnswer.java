package com.keville.ReBoggled.model.game;

import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.relational.core.mapping.Column;

import com.keville.ReBoggled.model.user.User;

public class GameAnswer {

  @Column("USERINFO")
  public AggregateReference<User, Integer> user;
  public String answer;

  public  GameAnswer() {}
}
