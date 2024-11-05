package com.keville.ReBoggled.controllers.web.api;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.keville.ReBoggled.model.lobby.Lobby;
import com.keville.ReBoggled.model.user.User;
import com.keville.ReBoggled.repository.LobbyRepository;

@RestController
public class UserController {

    @Autowired
    private LobbyRepository lobbies;

    @GetMapping("/api/user/info")
    public UserInfo test() {

      User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      Optional<Lobby> lobby = lobbies.findUserLobby(principal.id);

      return new UserInfo(principal.id,principal.username,principal.guest,lobby.isPresent() ? lobby.get().id : null);

    }

    //fixme : this is a 'dto', move to dtos
    public record UserInfo(Integer id,String username,boolean isGuest,Integer lobbyId) {};

}
