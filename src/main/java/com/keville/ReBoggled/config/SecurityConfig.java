package com.keville.ReBoggled.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import com.keville.ReBoggled.security.AuthenticationSuccessHandlerImpl;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

  @Autowired
  private AuthenticationSuccessHandlerImpl authenticationSuccessHandler;

  @Bean
  public SecurityFilterChain securityFilterChain(@Autowired HttpSecurity http,
      @Autowired HandlerMappingIntrospector introspector) 
        throws Exception 
  {
    MvcRequestMatcher.Builder mvcMatcherBuilder = new MvcRequestMatcher.Builder(introspector);

    return http
      .csrf(csrf -> csrf.disable())

      //there must be a better way not sure why I can't access these all the
      //unless I explicitly allow access
      //public resources
      .authorizeHttpRequests( request -> request
        .requestMatchers(mvcMatcherBuilder.pattern("/built/bundle.js")).permitAll()
        .requestMatchers(mvcMatcherBuilder.pattern("/style.css")).permitAll()
        .requestMatchers(mvcMatcherBuilder.pattern("/favicon.ico")).permitAll()
        .requestMatchers(mvcMatcherBuilder.pattern("/icons/user-profile-white-trans.png")).permitAll()
        .requestMatchers(mvcMatcherBuilder.pattern("/icons/user-profile-black-trans.png")).permitAll()
        .requestMatchers(mvcMatcherBuilder.pattern("/icons/user-profile-white-full-trans.png")).permitAll()
        .requestMatchers(mvcMatcherBuilder.pattern("/icons/user-profile-black-full-trans.png")).permitAll()
      )

      //guest & users
      .authorizeHttpRequests( request -> request
          .requestMatchers(mvcMatcherBuilder.pattern("/")).permitAll()
          .requestMatchers(mvcMatcherBuilder.pattern("/login")).permitAll()
          .requestMatchers(mvcMatcherBuilder.pattern("/lobby")).permitAll()
          .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.POST, "/api/lobby/*/join")).permitAll()
          .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.POST, "/api/lobby/*/leave")).permitAll()
          .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET, "/api/lobby")).permitAll()
      )

      //users only
      .authorizeHttpRequests( request -> request
          .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.POST, "/api/lobby")).authenticated()
      )

      // customize login form replaces built : .httpBasic(withDefaults()); 
      .formLogin((form) -> form
          .permitAll()
          .successHandler( authenticationSuccessHandler )
      )

      //invalid user session when /logout
      .logout((logout) -> logout.permitAll())

      // lock down any other request 
      .authorizeHttpRequests( request -> request
          .anyRequest().authenticated()
      )
      .build();
  }

  @Bean
  public InMemoryUserDetailsManager users() {
    return new InMemoryUserDetailsManager(
        User.withUsername("matt")
          .password("{noop}test") //use no op password encoder
          .roles("SA")
          .authorities("read")
          .build(),
        User.withUsername("guest")
          .password("{noop}test") //use no op password encoder
          .roles("user")
          .authorities("read")
          .build()
    );
  }

}
