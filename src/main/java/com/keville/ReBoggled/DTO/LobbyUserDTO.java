package com.keville.ReBoggled.DTO;

import com.keville.ReBoggled.model.user.User;

public class LobbyUserDTO {

    public Integer id;
    public String username;

    public LobbyUserDTO(User user) {
      this.id = user.id;
      this.username = user.username;
    }

    public LobbyUserDTO(Integer id,String username) {
      this.id = id;
      this.username = username;
    }

}
