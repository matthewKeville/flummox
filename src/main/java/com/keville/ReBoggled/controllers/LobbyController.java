package com.keville.ReBoggled.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Streamable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.keville.ReBoggled.model.Lobby;
import com.keville.ReBoggled.repository.LobbyRepository;

import java.util.stream.Collectors;
import java.util.Collection;

@RestController
public class LobbyController {

    private static final Logger LOG = LoggerFactory.getLogger(LobbyController.class);

    private LobbyRepository lobbies;

    public LobbyController(@Autowired LobbyRepository lobbies) {
      this.lobbies = lobbies;
    }

    @GetMapping("/api/lobby")
    public Collection<Lobby> test(@RequestParam(required = false, name = "publicOnly") boolean publicOnly) {
      LOG.info("hit api/lobby");
        return Streamable.of(lobbies.findAll()).toList().
            stream().
            collect(Collectors.toList());
    }

    //@GetMapping("/lobby")
    @GetMapping(value = {"/lobby","/"})
    public ModelAndView test() {
      //return the lobby template 
      ModelAndView modelAndView = new ModelAndView();
      modelAndView.setViewName("lobby");
      return modelAndView;
    }

    /*
    @GetMapping("/")
    public ModelAndView rootIsLobby() {
      //return the lobby template 
      ModelAndView modelAndView = new ModelAndView();
      modelAndView.setViewName("lobby");
      return modelAndView;
    }
    */

}
