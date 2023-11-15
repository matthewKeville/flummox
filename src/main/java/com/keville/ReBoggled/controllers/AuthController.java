package com.keville.ReBoggled.controllers;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.keville.ReBoggled.service.TokenService;


@RestController
public class AuthController {

    private static final Logger LOG = LoggerFactory.getLogger(AuthController.class);

    private final TokenService tokenService;

    public AuthController(@Autowired TokenService tokenService) {
      this.tokenService = tokenService;
    }

    @ResponseBody
    @PostMapping("/token")
    public String token(Authentication authentication) {
      LOG.info("Token requested for user : '{}'", authentication.getName());
      String token = tokenService.generateToken(authentication);
      LOG.info("Token granted {}",token);
      return token;
    }

    @ResponseBody
    @GetMapping("/token")
    public String home(Authentication authentication) {
      LOG.info("Token requested for user : '{}'", authentication.getName());
      String token = tokenService.generateToken(authentication);
      LOG.info("Token granted {}",token);
      return token;
    }

    @ResponseBody
    @PostMapping("/smeg")
    public String smeg(Authentication authentication) {
      LOG.info("smegma smega");
      return "smega";
    }

}
