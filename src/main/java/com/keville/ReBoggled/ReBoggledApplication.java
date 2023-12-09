package com.keville.ReBoggled;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

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

  /* 
   * This is really a security related matter ( or security adjacent )
   * I have this here because I want re-use the security context
   * when performing Mvc tests, but I don't want the MvcTests to create
   * this Bean. There is probably a better way to handle this using
   * @WithUserDetails
   */
  @Bean
  public JdbcUserDetailsManager users(DataSource dataSource) {

        // user authentication will always be (email,password)
        // thus email is the bridge between User (info) & Auth (UserDetails)

        UserDetails user = org.springframework.security.core.userdetails.User.builder()
          .username("matt@email.com")
          .password("{noop}test") //use no op password encoder
          .roles("SA")
          .authorities("read")
          .build();

        UserDetails alice = org.springframework.security.core.userdetails.User.builder()
          .username("alice@email.com")
          .password("{noop}guest") //use no op password encoder
          .roles("user")
          .authorities("read")
          .build();

        UserDetails bob = org.springframework.security.core.userdetails.User.builder()
          .username("bob@email.com")
          .password("{noop}guest") //use no op password encoder
          .roles("user")
          .authorities("read")
          .build();

        UserDetails charlie = org.springframework.security.core.userdetails.User.builder()
          .username("charlie@email.com")
          .password("{noop}guest") //use no op password encoder
          .roles("user")
          .authorities("read")
          .build();

        UserDetails dan = org.springframework.security.core.userdetails.User.builder()
          .username("dan@email.com")
          .password("{noop}guest") //use no op password encoder
          .roles("user")
          .authorities("read")
          .build();
    
        JdbcUserDetailsManager users = new JdbcUserDetailsManager (dataSource);

        users.createUser(user);
        users.createUser(alice);
        users.createUser(bob);
        users.createUser(charlie);
        users.createUser(dan);

        return users;
  }

  @Bean
  CommandLineRunner commandLineRunner(@Autowired LobbyRepository lobbies, @Autowired UserRepository users) {
    return args -> {

      // User (info) testing data

      User matt = users.save(User.createUser("matt@email.com", "fake"));
      AggregateReference<User, Integer> mattRef = AggregateReference.to(matt.getId());

      User alice = users.save(User.createUser("alice@email.com", "alice"));
      User bob = users.save(User.createUser("bob@email.com", "bob42"));
      AggregateReference<User, Integer> bobRef = AggregateReference.to(bob.getId());
      User charlie = users.save(User.createUser("charlie@email.com", "bigCharles"));
      User dan = users.save(User.createUser("dan@email.com", "thePipesArePlaying"));

      // Lobby Testing Data

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
