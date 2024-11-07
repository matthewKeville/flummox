package com.keville.ReBoggled.DTO;

import java.util.List;

import com.keville.ReBoggled.model.game.GameSettings;

public class LobbyDTO {

  public class LobbyUserDTO {

    public Integer id;
    public String username;
    public boolean inGame;

    public LobbyUserDTO(){};

    public LobbyUserDTO(Integer id,String username,boolean inGame) {
      this.id = id;
      this.username = username;
      this.inGame = inGame;
    }

  }

  public Integer id;
  //Lobby Settings ...
  public String name;
  public Integer capacity;
  public Boolean isPrivate;
  public GameSettings gameSettings;

  public Integer gameId;
  public Boolean gameActive = false;

  public LobbyUserDTO owner;
  public List<LobbyUserDTO> users;

  public LobbyDTO(){};



}
