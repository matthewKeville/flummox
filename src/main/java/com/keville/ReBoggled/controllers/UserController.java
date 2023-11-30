package com.keville.ReBoggled.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.keville.ReBoggled.model.User;

import jakarta.servlet.http.HttpSession;

@RestController
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/api/user/info")
    public UserInfo test(HttpSession session) {

      LOG.info("hit /api/user/info");

      if ( session.getAttribute("user") == null ) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown User");
      }

      User user = (User) session.getAttribute("user");
      return new UserInfo(user.getUsername(),user.isGuest());
    }

    public record UserInfo(String username,boolean isGuest) {};

}
