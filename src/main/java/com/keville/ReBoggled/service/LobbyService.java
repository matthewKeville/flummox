package com.keville.ReBoggled.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.keville.ReBoggled.model.Lobby;
import com.keville.ReBoggled.repository.LobbyRepository;

import java.util.stream.Collectors;
import java.util.Collection;
import java.util.Optional;

@Component
public class LobbyService {

    private static final Logger LOG = LoggerFactory.getLogger(LobbyService.class);

    private LobbyRepository lobbies;

    public LobbyService(@Autowired LobbyRepository lobbies) {
      this.lobbies = lobbies;
    }

    public Iterable<Lobby> getLobbies() {
      return lobbies.findAll();
    }

    public Optional<Lobby> getLobby(int id) {
      return lobbies.findById(id);
    }

    public void addLobby(Lobby lobby) {
      lobbies.save(lobby);
    }
}
