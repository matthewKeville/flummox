package com.keville.ReBoggled;

import java.io.File;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;

import com.keville.ReBoggled.model.BoardSize;
import com.keville.ReBoggled.model.BoardTopology;
import com.keville.ReBoggled.model.FindRule;
import com.keville.ReBoggled.model.GameSettings;
import com.keville.ReBoggled.model.Lobby;
import com.keville.ReBoggled.model.User;
import com.keville.ReBoggled.repository.LobbyRepository;
import com.keville.ReBoggled.repository.UserRepository;
import com.keville.ReBoggled.service.LobbyService;

@SpringBootApplication
public class ReBoggledApplication {

  @Autowired
  private LobbyService lobbyService;

  public static void main(String[] args) {
    SpringApplication.run(ReBoggledApplication.class, args);
  }

  @Bean
  public UserDetailsManager users(DataSource dataSource) {
    JdbcUserDetailsManager users = new JdbcUserDetailsManager (dataSource);
    return users;
  }

  @Bean
  CommandLineRunner populateDbIfEmpty(
    @Autowired LobbyRepository lobbies,
    @Autowired UserRepository users,
    @Autowired UserDetailsManager userDetailsManager
    ) {

    return args -> {
   
      // Quick and dirty check to see if db schema was already created
      File dbFile = new File("tmp/reboggled.db.mv.db");
      if ( dbFile.exists() )  {
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
  
      userDetailsManager.createUser(mattDetails);
      userDetailsManager.createUser(aliceDetails);
      userDetailsManager.createUser(bobDetails);
      userDetailsManager.createUser(charlieDetails);
      userDetailsManager.createUser(danDetails);

      // Development User Data

      User matt = users.save(User.createUser("matt@email.com", "fake"));
      AggregateReference<User, Integer> mattRef = AggregateReference.to(matt.getId());

      User alice = users.save(User.createUser("alice@email.com", "alice"));
      User bob = users.save(User.createUser("bob@email.com", "bob42"));
      AggregateReference<User, Integer> bobRef = AggregateReference.to(bob.getId());
      User charlie = users.save(User.createUser("charlie@email.com", "bigCharles"));
      User dan = users.save(User.createUser("dan@email.com", "thePipesArePlaying"));

      // Development Lobby Data

      Lobby secret = new Lobby("Secret Dungeon", 4, true, mattRef);
      secret = lobbies.save(secret);
      lobbyService.addUserToLobby(matt.getId(), secret.id);
      lobbyService.addUserToLobby(alice.getId(), secret.id);

      GameSettings gameSettings = new GameSettings(BoardSize.FIVE, BoardTopology.CYLINDER, FindRule.UNIQUE, 120);
      Lobby roomA = lobbies.save(new Lobby("Room A", 2, false, mattRef, gameSettings));
      lobbyService.addUserToLobby(charlie.getId(), roomA.id);
      lobbyService.addUserToLobby(dan.getId(), roomA.id);

      lobbies.save(new Lobby("The Purple Lounge", 12, false, bobRef));

    };
  }

  static AggregateReference<User, Integer> ARof(User user) {
    return AggregateReference.to(user.getId());
  }

}
