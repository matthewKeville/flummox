package com.keville.ReBoggled.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;

@Configuration
public class UserDetailsConfig {

  @Bean
  public UserDetailsManager users(@Autowired DataSource dataSource) {
    JdbcUserDetailsManager users = new JdbcUserDetailsManager (dataSource);
    return users;
  }

}
