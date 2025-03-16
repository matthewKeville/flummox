package com.keville.flummox.controllers.web.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.keville.flummox.service.userService.UserService;

@RestController
@RequestMapping("/api/session")
public class SessionController {

    @Autowired
    private UserService userService;

    @PostMapping("/active")
    public ResponseEntity<?> verify() {
      userService.updateUserSessionActivityChecker();
      return ResponseEntity.ok().build();
    }


}
