package com.keville.flummox.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

@Configuration
public class FlummoxConfig {

  @Bean TaskScheduler taskScheduler() {
    return new ConcurrentTaskScheduler();
  }

}
