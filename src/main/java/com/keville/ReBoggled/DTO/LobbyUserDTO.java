package com.keville.ReBoggled.DTO;

import java.util.List;

import com.keville.ReBoggled.model.User;

public class LobbyUserDTO {

    public Integer id;
    public String username;

    public LobbyUserDTO(User user) {
      this.id = user.getId();
      this.username = user.getUsername();
    }

}