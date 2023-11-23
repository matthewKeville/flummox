package com.keville.ReBoggled.model;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.relational.core.mapping.MappedCollection;

public class Lobby {

    @Id
    private Integer id;

    private String name;
    private int capacity;
    private Boolean isPrivate;
    private AggregateReference<User, Integer> owner ;

    @MappedCollection(idColumn = "LOBBY")
    private Set<LobbyUserReference> users = new HashSet<LobbyUserReference>();

    // Err: if no nargs cons
    public Lobby(){}

    public Lobby(String name, int capacity, boolean isPrivate, AggregateReference<User, Integer> owner) {
      this.name = name;
      this.capacity = capacity;
      this.isPrivate = isPrivate;
      this.owner = owner;
    }
 
    // Create Sub Entity
    public void addUser(AggregateReference<User,Integer> user) {
      this.users.add(new LobbyUserReference(user));
    }

    // Getters & Setters 
    // Not sure why I need these , but User.java & LobbyUserReference don't..

    public Integer getId() {
      return id;
    }

    public void setId(Integer id) {
      this.id = id;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public int getCapacity() {
      return capacity;
    }



    public void setCapacity(int capacity) {
      this.capacity = capacity;
    }



    public Boolean getIsPrivate() {
      return isPrivate;
    }



    public void setIsPrivate(Boolean isPrivate) {
      this.isPrivate = isPrivate;
    }



    public AggregateReference<User, Integer> getOwner() {
      return owner;
    }



    public void setOwner(AggregateReference<User, Integer> owner) {
      this.owner = owner;
    }



    public Set<LobbyUserReference> getUsers() {
      return users;
    }



    public void setUsers(Set<LobbyUserReference> users) {
      this.users = users;
    }

}
