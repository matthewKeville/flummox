package com.keville.ReBoggled.unit.context;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("file:./src/main/resources/application.properties")
public class TestingContext {

  public static void main(String[] args) {
    SpringApplication.run(TestingContext.class, args);
  }
}
