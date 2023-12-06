package com.keville.ReBoggled;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jdbc.core.mapping.AggregateReference;

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
    CommandLineRunner commandLineRunner(@Autowired LobbyRepository lobbies, @Autowired UserRepository users) {
      return args -> {

        // User (info) testing data

        User matt = users.save(User.createUser("matt@email.com","fake"));
        AggregateReference<User, Integer>  mattRef = AggregateReference.to(matt.getId());

        User alice = users.save(User.createUser("alice@email.com","alice"));
        User bob = users.save(User.createUser("bob@email.com","bob42"));
        User charlie = users.save(User.createUser("charlie@email.com","bigCharles"));
        User dan = users.save(User.createUser("dan@email.com","thePipesArePlaying"));

        // Lobby Testing Data

        Lobby secret = new Lobby("Secret Dungeon",4,true,mattRef);
        secret = lobbies.save(secret);
        lobbyService.addUserToLobby(matt.getId(),secret.id);
        lobbyService.addUserToLobby(alice.getId(),secret.id);

        GameSettings gameSettings = new GameSettings(BoardSize.FIVE,BoardTopology.CYLINDER,FindRule.UNIQUE,120);
        Lobby roomA = lobbies.save(new Lobby("Room A",2,false,mattRef,gameSettings));
        lobbyService.addUserToLobby(charlie.getId(),roomA.id);
        lobbyService.addUserToLobby(dan.getId(),roomA.id);

        lobbies.save(new Lobby("The Purple Lounge",12,false,mattRef));

      };
    }

    static AggregateReference<User, Integer> ARof(User user) {
      return AggregateReference.to(user.getId());
    }

}
