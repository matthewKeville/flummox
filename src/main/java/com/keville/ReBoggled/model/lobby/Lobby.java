package com.keville.ReBoggled.model.lobby;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.MappedCollection;

import com.keville.ReBoggled.model.game.GameSettings;
import com.keville.ReBoggled.model.user.User;

public class Lobby {

    @Id
    public Integer id;

    public String name;
    public int capacity;
    public Boolean isPrivate;

    @Embedded.Nullable
    public GameSettings gameSettings;

    public AggregateReference<User, Integer> owner ;

    @MappedCollection(idColumn = "LOBBY")
    public Set<LobbyUserReference> users = new HashSet<LobbyUserReference>();

    public Lobby(){}

    public Lobby(String name, int capacity, boolean isPrivate, AggregateReference<User, Integer> owner) {
      this.name = name;
      this.capacity = capacity;
      this.isPrivate = isPrivate;
      this.owner = owner;
      this.gameSettings = new GameSettings();
    }

    public Lobby(String name, int capacity, boolean isPrivate,
        AggregateReference<User, Integer> owner, GameSettings gameSettings) {
      this.name = name;
      this.capacity = capacity;
      this.isPrivate = isPrivate;
      this.owner = owner;
      this.gameSettings = gameSettings;
    }

}
