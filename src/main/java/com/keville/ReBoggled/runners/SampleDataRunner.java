package com.keville.ReBoggled.runners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Component;

import com.keville.ReBoggled.model.game.BoardSize;
import com.keville.ReBoggled.model.game.BoardTopology;
import com.keville.ReBoggled.model.game.FindRule;
import com.keville.ReBoggled.model.game.GameSettings;
import com.keville.ReBoggled.model.lobby.Lobby;
import com.keville.ReBoggled.model.user.User;
import com.keville.ReBoggled.repository.LobbyRepository;
import com.keville.ReBoggled.repository.UserRepository;
import com.keville.ReBoggled.service.lobbyService.LobbyService;
import com.keville.ReBoggled.service.lobbyService.LobbyServiceException;

@Component
public class SampleDataRunner implements CommandLineRunner {

  private static final Logger LOG = LoggerFactory.getLogger(SampleDataRunner.class);

  @Autowired
  private LobbyService lobbyService;
  @Autowired
  private UserDetailsManager userDetailsManager;
  @Autowired 
  UserRepository users;
  @Autowired LobbyRepository lobbies;

  @Override
  public void run(String... args) {

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

      // Development User Auth

      UserDetails mattDetails = org.springframework.security.core.userdetails.User.builder()
        .username("matt@email.com")
        .password("{noop}test") //use no op password encoder
        .roles("SA")
        .authorities("read")
        .build();

      UserDetails aliceDetails = org.springframework.security.core.userdetails.User.builder()
        .username("alice@email.com")
        .password("{noop}guest") //use no op password encoder
        .roles("user")
        .authorities("read")
        .build();

      UserDetails bobDetails = org.springframework.security.core.userdetails.User.builder()
        .username("bob@email.com")
        .password("{noop}guest") //use no op password encoder
        .roles("user")
        .authorities("read")
        .build();

      UserDetails charlieDetails = org.springframework.security.core.userdetails.User.builder()
        .username("charlie@email.com")
        .password("{noop}guest") //use no op password encoder
        .roles("user")
        .authorities("read")
        .build();

      UserDetails danDetails = org.springframework.security.core.userdetails.User.builder()
        .username("dan@email.com")
        .password("{noop}guest") //use no op password encoder
        .roles("user")
        .authorities("read")
        .build();

      UserDetails meDetails = org.springframework.security.core.userdetails.User.builder()
        .username("emily@email.com")
        .password("{noop}guest") //use no op password encoder
        .roles("user")
        .authorities("read")
        .build();
  
  
      userDetailsManager.createUser(mattDetails);
      userDetailsManager.createUser(aliceDetails);
      userDetailsManager.createUser(bobDetails);
      userDetailsManager.createUser(charlieDetails);
      userDetailsManager.createUser(danDetails);

      try {

        User matt = users.save(User.createUser("matt@email.com", "fake"));
        AggregateReference<User, Integer> mattRef = AggregateReference.to(matt.id);

        User alice = users.save(User.createUser("alice@email.com", "alice"));
        User bob = users.save(User.createUser("bob@email.com", "bob42"));

        AggregateReference<User, Integer> bobRef = AggregateReference.to(bob.id);
        User charlie = users.save(User.createUser("charlie@email.com", "bigCharles"));
        User dan = users.save(User.createUser("dan@email.com", "thePipesArePlaying"));
        User emily = users.save(User.createUser("emily@email.com", "empemjem"));

        // Development Lobby Data

        //if we add users to any private lobby we throw here TBD

        //Lobby secret = new Lobby("Secret Dungeon", 4, false, mattRef);
        Lobby secret = lobbyService.createNew(matt.id);
        lobbyService.addUserToLobby(alice.id, secret.id);

        GameSettings gameSettings = new GameSettings(BoardSize.FIVE, BoardTopology.CYLINDER, FindRule.UNIQUE, 120);
        Lobby roomA = lobbies.save(new Lobby("Room A", 2, false, AggregateReference.to(charlie.id), gameSettings));
        lobbyService.addUserToLobby(charlie.id, roomA.id);
        lobbyService.addUserToLobby(dan.id, roomA.id);

        lobbies.save(new Lobby("The Purple Lounge", 12, false, bobRef));

        Lobby single = lobbies.save(new Lobby("The Single", 1, false, AggregateReference.to(emily.id)));
        lobbyService.addUserToLobby(emily.id, single.id);

      } catch ( LobbyServiceException lse) {
        LOG.error(lse.getMessage());
      }
      
      System.exit(0);

    }

  static AggregateReference<User, Integer> ARof(User user) {
    return AggregateReference.to(user.id);
  }

}
