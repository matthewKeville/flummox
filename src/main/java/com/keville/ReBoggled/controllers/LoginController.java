package com.keville.ReBoggled.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

    private static final Logger LOG = LoggerFactory.getLogger(LoginController.class);

    @GetMapping(value = {"/login", "/"})
    public String login(@Autowired HttpSession session) {

      LOG.info("user type : " + (String) session.getAttribute("sessionType"));
      
      return "login";
    }

}
