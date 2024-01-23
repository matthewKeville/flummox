package com.keville.ReBoggled;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing;

@SpringBootApplication
@EnableJdbcAuditing
public class ReBoggledApplication {

  private static final Logger LOG = LoggerFactory.getLogger(ReBoggledApplication.class);

  public static void main(String[] args) {
    SpringApplication.run(ReBoggledApplication.class, args);
  }

}
