package com.keville.ReBoggled.controllers.web.api;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.keville.ReBoggled.DTO.UserInfoDTO;
import com.keville.ReBoggled.model.lobby.Lobby;
import com.keville.ReBoggled.model.user.User;
import com.keville.ReBoggled.repository.LobbyRepository;

@RestController
public class UserController {

    @Autowired
    private LobbyRepository lobbies;

    @GetMapping("/api/user/info")
    public UserInfoDTO info() {

      User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      Optional<Lobby> lobby = lobbies.findUserLobby(principal.id);

      return new UserInfoDTO(principal.id,principal.username,principal.guest,lobby.isPresent() ? lobby.get().id : null);

    }


}
