package com.keville.ReBoggled.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
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

      // h2-console sends X-Frame-Options Deny
      .headers( headers -> headers
          .frameOptions().sameOrigin()
      )

      .authorizeHttpRequests( request -> request
        .requestMatchers(mvcMatcherBuilder.pattern("/h2-console/*")).permitAll()
        //.headers().frameOptions().sameOrigin().
      )

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

          .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET,  "/api/user/info")).permitAll()
          .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET,  "/api/lobby")).permitAll()
          .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET,  "/api/lobby/*")).permitAll()

          .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.POST, "/api/lobby/*/join")).permitAll()
          .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.POST, "/api/lobby/*/leave")).permitAll()
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

  /* 
   * I believe boot automatically creates the DataSource bean
   * with respect to h2, otherwise I would have to register my own
   */
  @Bean
  public JdbcUserDetailsManager users(DataSource dataSource) {

        // user authentication will always be (email,password)
        // thus email is the bridge between User (info) & Auth (UserDetails)

        UserDetails user = User.builder()
          .username("matt@email.com")
          .password("{noop}test") //use no op password encoder
          .roles("SA")
          .authorities("read")
          .build();

        UserDetails alice = User.builder()
          .username("alice@email.com")
          .password("{noop}guest") //use no op password encoder
          .roles("user")
          .authorities("read")
          .build();

        UserDetails bob = User.builder()
          .username("bob@email.com")
          .password("{noop}guest") //use no op password encoder
          .roles("user")
          .authorities("read")
          .build();

        UserDetails charlie = User.builder()
          .username("charlie@email.com")
          .password("{noop}guest") //use no op password encoder
          .roles("user")
          .authorities("read")
          .build();

        UserDetails dan = User.builder()
          .username("dan@email.com")
          .password("{noop}guest") //use no op password encoder
          .roles("user")
          .authorities("read")
          .build();
    
        JdbcUserDetailsManager users = new JdbcUserDetailsManager (dataSource);

        users.createUser(user);
        users.createUser(alice);
        users.createUser(bob);
        users.createUser(charlie);
        users.createUser(dan);

        return users;
  }

}
