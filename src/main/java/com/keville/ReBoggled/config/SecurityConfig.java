package com.keville.ReBoggled.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.config.Customizer;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(@Autowired HttpSecurity http) throws Exception {
    return http
      .csrf(csrf -> csrf.disable()) // not sure who / why recc. dangerous?
      .authorizeHttpRequests( request -> request
          .anyRequest().authenticated() //evertything else needs auth
      )
      // for default login .httpBasic(withDefaults()); 
      // this can't be used with custom form login below...
      .formLogin((form) -> form //set the login page
          .loginPage("/login")
          .permitAll()
      )
      .logout((logout) -> logout.permitAll()) //invalid user session when /logout
      .build();
  }

  @Bean
  public InMemoryUserDetailsManager users() {
    return new InMemoryUserDetailsManager(
        User.withUsername("matt")
          .password("{noop}test") //use no op password encoder
          .authorities("read")
          .build()
    );
  }

}
