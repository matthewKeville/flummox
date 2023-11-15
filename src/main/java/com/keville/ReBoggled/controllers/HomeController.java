package com.keville.ReBoggled.controllers;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String home() {
      return "secure";
    }

    @GetMapping("/secure")
    @ResponseBody //don't try to find a view with the name of the string
    public String home(@Autowired Principal principal) {
      return "welcome : " + principal.getName();
    }

}
