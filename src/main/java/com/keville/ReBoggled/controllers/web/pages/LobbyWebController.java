package com.keville.ReBoggled.controllers.web.pages;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

//FIXME : This doesn't feel appropriate. Perhaps there should be a general template controller class
//but not one for lobby. General and Login make sense, and this is serving as a "general" controller atm.

@Controller
public class LobbyWebController {

  private static final Logger LOG = LoggerFactory.getLogger(LobbyWebController.class);

  @GetMapping(value = { "lobby/{lobbyId}/" , "/lobby", "/" })
  public String lobby() {
    return "main";
  }

}
