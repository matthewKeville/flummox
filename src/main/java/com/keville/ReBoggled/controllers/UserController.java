package com.keville.ReBoggled.controllers;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.keville.ReBoggled.model.User;
import com.keville.ReBoggled.service.UserService;

import jakarta.servlet.http.HttpSession;

@RestController
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @GetMapping("/api/user/info")
    public UserInfo test(HttpSession session) {

      LOG.info("hit /api/user/info");

      if ( session.getAttribute("userId") == null ) {
        LOG.warn(" client tried to request userInfo but the session doesn't have a userId assigned");
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to identify current user");
      }

      User user = userService.getUser((Integer) session.getAttribute("userId"));
      if ( user == null ) {
        LOG.warn(String.format(" unable to locate user from session userId %d ", session.getAttribute("userId")));
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to locate details for identified user");
      }

      return new UserInfo(user.id,user.username,user.guest);
    }

    public record UserInfo(Integer id,String username,boolean isGuest) {};

}
