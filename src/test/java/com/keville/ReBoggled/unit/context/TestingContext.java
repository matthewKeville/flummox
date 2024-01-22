package com.keville.ReBoggled.unit.context;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.test.context.ActiveProfiles;

@SpringBootApplication

//@ActiveProfiles("local") 
//for now the default profile (local) is sufficient testing
//in the future perhaps a different profile will be necessary
public class TestingContext {

  public static void main(String[] args) {
    SpringApplication.run(TestingContext.class, args);
  }
}
