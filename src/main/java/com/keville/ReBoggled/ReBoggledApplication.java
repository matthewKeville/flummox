package com.keville.ReBoggled;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jdbc.core.mapping.AggregateReference;


import com.keville.ReBoggled.model.Lobby;
import com.keville.ReBoggled.model.User;
import com.keville.ReBoggled.repository.LobbyRepository;
import com.keville.ReBoggled.repository.UserRepository;

@SpringBootApplication
public class ReBoggledApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReBoggledApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(@Autowired LobbyRepository lobbies, @Autowired UserRepository users) {
      return args -> {

        User matt = users.save(new User("fake@email.com",false));
        AggregateReference<User, Integer>  mattRef = AggregateReference.to(matt.getId());

        User alice = users.save(new User("fake@email.com",false));
        User bob = users.save(new User("fake@email.com",false));
        User charlie = users.save(new User("fake@email.com",false));

        lobbies.save(new Lobby("Room A",10,false,mattRef));
        lobbies.save(new Lobby("The Purple Lounge",12,false,mattRef));

        Lobby secret = new Lobby("Secret Dungeon",4,true,mattRef);

        secret.addUser(ARof(matt));
        secret.addUser(ARof(alice));
        secret.addUser(ARof(charlie));

        lobbies.save(secret);

      };
    }

    static AggregateReference<User, Integer> ARof(User user) {
      return AggregateReference.to(user.getId());
    }

}
