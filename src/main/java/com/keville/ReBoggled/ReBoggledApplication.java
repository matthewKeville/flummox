package com.keville.ReBoggled;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.keville.ReBoggled.model.Lobby;
import com.keville.ReBoggled.repository.LobbyRepository;

@SpringBootApplication                                //bean it's a record property
public class ReBoggledApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReBoggledApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(LobbyRepository lobbies) {
      return args -> {
        System.out.println("building fake lobby data");
        lobbies.save(new Lobby("testing",10));
        lobbies.save(new Lobby("free the nip",12));
        lobbies.save(new Lobby("joey salads",4));
      };
    }

}
