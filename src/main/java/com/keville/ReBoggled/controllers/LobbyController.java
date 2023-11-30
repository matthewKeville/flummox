package com.keville.ReBoggled.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.keville.ReBoggled.model.Lobby;
import com.keville.ReBoggled.service.LobbyService;

import jakarta.servlet.http.HttpSession;

@RestController
public class LobbyController {

    private static final Logger LOG = LoggerFactory.getLogger(LobbyController.class);

    private LobbyService lobbyService;

    public LobbyController(@Autowired LobbyService lobbies) {
      this.lobbyService = lobbies;
    }

    @GetMapping("/lobby")
    public ModelAndView test() {
      //return the lobby template 
      ModelAndView modelAndView = new ModelAndView();
      modelAndView.setViewName("lobby");
      return modelAndView;
    }

    @GetMapping("/api/lobby")
    public Iterable<Lobby> test(
        @RequestParam(required = false, name = "publicOnly") boolean publicOnly,
        HttpSession session) {

      LOG.info("hit /api/lobby");
      Iterable<Lobby> lobbies = lobbyService.getLobbies();

      return lobbies;
    }

    // @PostMapping("/api/lobby")
    // public String addLobby(@Autowired HttpSession session) {
    //
    //   LOG.info("hit POST /api/lobby");
    //   LOG.info("user type : " + (String) session.getAttribute("sessionType"));
    //
    //   return "allowed";
    //
    // }

    // public String joinLobbyRequest(@PathVariable("id") String id,
    //     @Autowired HttpSession session,
    //     @Autowired Authentication auth
    //     ) {
    //
    //   LOG.info("hit POST /api/lobby/"+id+"join");
    //   LOG.info("user type : " + (String) session.getAttribute("sessionType"));
    //   LOG.info("principal " + auth.toString());
    //
    //   return "you are trying to join lobby " + id;
    //
    // }
    // //
    // @PostMapping("/api/lobby/{id}/leave")
    // public String leaveLobbyRequest(@PathVariable("id") String id, @Autowired HttpSession session) {
    //
    //   LOG.info("hit POST /api/lobby/"+id+"leave");
    //   LOG.info("user type : " + (String) session.getAttribute("sessionType"));
    //
    //   return "you are trying to leave lobby " + id;
    //
    // }

}
