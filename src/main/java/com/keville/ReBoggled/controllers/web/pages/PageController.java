package com.keville.ReBoggled.controllers.web.pages;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

  @GetMapping(value = { "/"})
  public String lobby() {
    return "main";
  }

}
