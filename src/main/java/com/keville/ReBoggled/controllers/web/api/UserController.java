package com.keville.ReBoggled.controllers.web.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.keville.ReBoggled.model.user.User;
import com.keville.ReBoggled.service.lobbyService.LobbyService;

@RestController
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private LobbyService lobbyService;

    @GetMapping("/api/user/info")
    public UserInfo test() {

      User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      return new UserInfo(user.id,user.username,user.guest,lobbyService.getUserLobbyId(user.id));

    }

    //fixme : this is a 'dto', move to dtos
    public record UserInfo(Integer id,String username,boolean isGuest,Integer lobbyId) {};

}
