package com.keville.ReBoggled.model.game;

import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.relational.core.mapping.Column;

import com.keville.ReBoggled.model.user.User;

public class GameUserReference {
  
  @Id
  public Integer id;

  public AggregateReference<Game, Integer> game;

  public AggregateReference<User, Integer> user;

  public GameUserReference(
      AggregateReference<Game, Integer> game,
      AggregateReference<User, Integer> user){
    this.game = game;
    this.user = user;
  }

  @Override
  public int hashCode() {
    if ( user.getId() == null ) {
      return 0;
    }
    return 31 * (this.user.getId() + 1) * (this.game.getId() + 1);
  }

  @Override
  public boolean equals(Object object) {
    if ( object instanceof GameUserReference ) {

      GameUserReference gameUserReference = (GameUserReference) object;

      if ( user == null ) {
        return gameUserReference.user == null;
      } else if (gameUserReference.user == null) {
        return false;
      }

      return user.getId() == gameUserReference.user.getId();
    }

    return false;

  }

}
