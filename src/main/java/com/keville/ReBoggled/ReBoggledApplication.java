package com.keville.ReBoggled;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.keville.ReBoggled.config.RsaKeyProperties;
import com.keville.ReBoggled.model.Lobby;
import com.keville.ReBoggled.repository.LobbyRepository;

@EnableConfigurationProperties(RsaKeyProperties.class) // Note : no RsaKeyProperties
@SpringBootApplication                                //bean it's a record property
public class ReBoggledApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReBoggledApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(LobbyRepository lobbies) {
      return args -> {
        lobbies.save(new Lobby("testing",10));
        lobbies.save(new Lobby("free the nip",12));
        lobbies.save(new Lobby("joey salads",4));
      };
    }

}
