package com.keville.ReBoggled.controllers.web.pages;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

  private static final Logger LOG = LoggerFactory.getLogger(PageController.class);

  @GetMapping(value = { "/",  "/lobby",  "lobby/{lobbyId}", "/join"})
  public String lobby() {
    return "main";
  }

}
