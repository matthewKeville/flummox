package com.keville.ReBoggled.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
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

      // customize login form replaces built : .httpBasic(withDefaults()); 
      .formLogin((form) -> form
          .permitAll()
          .successHandler( authenticationSuccessHandler )
      )

      //invalid user session when /logout
      .logout((logout) -> logout.permitAll())

      .authorizeHttpRequests( request -> request

        .requestMatchers(mvcMatcherBuilder.pattern("/h2-console/*")).permitAll()

        .requestMatchers(mvcMatcherBuilder.pattern("/built/bundle.js")).permitAll()
        .requestMatchers(mvcMatcherBuilder.pattern("/style.css")).permitAll()
        .requestMatchers(mvcMatcherBuilder.pattern("/favicon.ico")).permitAll()
        .requestMatchers(mvcMatcherBuilder.pattern("/icons/user-profile-white-trans.png")).permitAll()
        .requestMatchers(mvcMatcherBuilder.pattern("/icons/user-profile-black-trans.png")).permitAll()
        .requestMatchers(mvcMatcherBuilder.pattern("/icons/user-profile-white-full-trans.png")).permitAll()
        .requestMatchers(mvcMatcherBuilder.pattern("/icons/user-profile-black-full-trans.png")).permitAll()

        .requestMatchers(mvcMatcherBuilder.pattern("/")).permitAll()
        .requestMatchers(mvcMatcherBuilder.pattern("/error")).permitAll()
        .requestMatchers(mvcMatcherBuilder.pattern("/login")).permitAll()
        .requestMatchers(mvcMatcherBuilder.pattern("/lobby")).permitAll()
        .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET,  "/api/user/info")).permitAll()

        .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET,  "/api/lobby")).permitAll()
        .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET,  "/api/lobby/view/lobby")).permitAll()
        .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET,  "/api/lobby/*/view/lobby")).permitAll()
        .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET,  "/api/lobby/*")).permitAll()
        .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET,  "/api/lobby/*/view/lobby/sse")).permitAll()
        .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.POST, "/api/lobby/*/join")).permitAll()
        .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.POST, "/api/lobby/*/leave")).permitAll()

        .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET,  "/api/game")).permitAll()
        .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET,  "/api/game/*")).permitAll()
        .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.POST, "/api/game/*/answer")).permitAll()
        .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET,  "/api/game/*/view/user/sse")).permitAll()
        .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET,  "/api/game/*/view/user")).permitAll()
        .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET,  "/api/game/*/view/user/summary")).permitAll()

        .anyRequest().authenticated()

      )

      .build();
  }

}
