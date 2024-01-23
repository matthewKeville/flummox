package com.keville.ReBoggled.unit.context;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@ActiveProfiles("local") 
@SpringBootApplication
public class UnitTestingContext {
  public static void main(String[] args) {
    SpringApplication.run(UnitTestingContext.class, args);
  }
}
