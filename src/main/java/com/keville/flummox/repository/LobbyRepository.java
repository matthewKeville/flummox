package com.keville.flummox.repository;

import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.keville.flummox.model.lobby.Lobby;

public interface LobbyRepository extends CrudRepository<Lobby, Integer> {

  @Query("""
  SELECT  LOB.* FROM lobby_user_reference as LUR 
    join lobby as LOB 
    on LUR.LOBBY = LOB.ID 
      where LUR.USER = :userId
  """)
  Optional<Lobby> findUserLobby(Integer userId);

  @Query("""
  SELECT * FROM lobby WHERE OWNER = :userId;
  """)
  Optional<Lobby> findOwnedLobby(Integer userId);

}
