package com.keville.flummox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@EnableJdbcAuditing
public class FlummoxApplication {

  private static final Logger LOG = LoggerFactory.getLogger(FlummoxApplication.class);

  public static void main(String[] args) {
    SpringApplication.run(FlummoxApplication.class, args);
  }

}
