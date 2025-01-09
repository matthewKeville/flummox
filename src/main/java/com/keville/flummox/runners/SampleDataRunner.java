package com.keville.flummox.runners;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.stereotype.Component;

import com.keville.flummox.model.game.BoardSize;
import com.keville.flummox.model.game.BoardTopology;
import com.keville.flummox.model.game.FindRule;
import com.keville.flummox.model.game.GameSettings;
import com.keville.flummox.model.lobby.Lobby;
import com.keville.flummox.model.user.User;
import com.keville.flummox.repository.LobbyRepository;
import com.keville.flummox.repository.UserRepository;
import com.keville.flummox.service.exceptions.BadRequest;
import com.keville.flummox.service.exceptions.EntityNotFound;
import com.keville.flummox.service.lobbyService.LobbyService;
import com.keville.flummox.service.lobbyService.LobbyServiceException;
import com.keville.flummox.service.userService.UserServiceException;

@Component
public class SampleDataRunner implements CommandLineRunner {

  private static final Logger LOG = LoggerFactory.getLogger(SampleDataRunner.class);

  @Autowired
  private UserRepository users;
  @Autowired
  private LobbyService lobbyService;
  @Autowired 
  LobbyRepository lobbies;

  @Override
  public void run(String... args) throws UserServiceException, BadRequest,EntityNotFound {

      boolean skipCreateDevData = true;

      LOG.info("Parsing Arguments");

      for ( String arg : args ) {

        String[] argParts = arg.split("=");

        if (argParts.length != 2) {
          LOG.warn("invalid argument : " + arg);
          continue;
        }

        try {

          String prop = argParts[0];
          String value = argParts[1];

          if ( prop.equals("--create-dev-data") ) {
            skipCreateDevData = !Boolean.parseBoolean(value);
          }

        } catch (Exception e)  {

          LOG.error("error processing argument " + arg);
          continue;

        }

      }


      if ( skipCreateDevData ) {
        LOG.info("skipping creation of dev data");
        return;
      }

      User ada = new User("ada","matt@email.com","{noop}boggle");
      User lily = new User("lily","lily@email.com","{noop}boggle");

      User alice = new User("alice","alice@email.com","{noop}password");
      User bob = new User("bob","bob@email.com","{noop}password");
      User charlie = new User("charlie","charlie@email.com","{noop}password");

      ada = users.save(ada);
      lily = users.save(lily);

      alice = users.save(alice);
      bob = users.save(bob);
      charlie = users.save(charlie);

      // dev accs

      GameSettings gameSettings = new GameSettings(BoardSize.FOUR, BoardTopology.PLANE, FindRule.FIRST, 30);
      Lobby fountainOfDreams = lobbies.save(new Lobby("Fountain of Dreams", 16, false, ARof(ada), gameSettings));

      gameSettings = new GameSettings(BoardSize.FIVE, BoardTopology.PLANE, FindRule.ANY, 60);
      Lobby pokemonStadium = lobbies.save(new Lobby("Pokemon Stadium", 16, false, ARof(lily), gameSettings));

      // dummy accs

      gameSettings = new GameSettings(BoardSize.SIX, BoardTopology.CYLINDER, FindRule.ANY, 60);
      Lobby planetZebes = lobbies.save(new Lobby("Planet ZebeS", 4, false, ARof(alice), gameSettings));

      gameSettings = new GameSettings(BoardSize.SIX, BoardTopology.CYLINDER, FindRule.ANY, 60);
      Lobby bigBlue = lobbies.save(new Lobby("Big Blue", 16, false, ARof(bob), gameSettings));

      gameSettings = new GameSettings(BoardSize.SIX, BoardTopology.CYLINDER, FindRule.ANY, 60);
      Lobby yoshisIsland = lobbies.save(new Lobby("Yoshi's Island", 16, false, ARof(charlie), gameSettings));

      
      System.exit(0);

    }

  static AggregateReference<User, Integer> ARof(User user) {
    return AggregateReference.to(user.id);
  }

}
