package com.keville.ReBoggled.controllers.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LobbyWebController {

  private static final Logger LOG = LoggerFactory.getLogger(LobbyWebController.class);

  @GetMapping(value = { "/lobby", "/" })
  public String lobby() {
    return "lobby";
  }

}
