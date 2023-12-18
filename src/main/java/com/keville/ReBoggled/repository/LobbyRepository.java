package com.keville.ReBoggled.repository;

import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.keville.ReBoggled.model.lobby.Lobby;

public interface LobbyRepository extends CrudRepository<Lobby, Integer> {

  @Query("""
  SELECT  LOB.* FROM LOBBY_USER_REFERENCE as LUR 
    join LOBBY as LOB 
    on LUR.LOBBY = LOB.ID 
      where LUR.USERINFO = :userId
  """)
  Optional<Lobby> findUserLobby(Integer userId);

  @Query("""
  SELECT * FROM LOBBY WHERE OWNER = :userId;
  """)
  Optional<Lobby> findOwnedLobby(Integer userId);

}
