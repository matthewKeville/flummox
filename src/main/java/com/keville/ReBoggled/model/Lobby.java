package com.keville.ReBoggled.model;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.relational.core.mapping.MappedCollection;

public class Lobby {

    @Id
    public Integer id;

    public String name;
    public int capacity;
    public Boolean isPrivate;
    public GameSettings gameSettings;

    public AggregateReference<User, Integer> owner ;

    @MappedCollection(idColumn = "LOBBY")
    public Set<LobbyUserReference> users = new HashSet<LobbyUserReference>();

    // Err: if no nargs cons
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
