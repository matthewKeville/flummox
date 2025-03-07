package com.keville.flummox.security;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

  @Autowired
  private GuestUserAnonymousAuthenticationFilter anonAuthenticationFilter;
  @Autowired
  private CustomAuthenticationFailureHandler failureHandler;
  @Autowired
  private CustomAuthenticationSuccessHandler successHandler;

  @Bean
  public SecurityFilterChain securityFilterChain(
      @Autowired HttpSecurity http,
      @Autowired HandlerMappingIntrospector introspector,
      @Autowired Environment env) 
        throws Exception 
  {
    MvcRequestMatcher.Builder mvcMatcherBuilder = new MvcRequestMatcher.Builder(introspector);

    return http

      .csrf(csrf -> csrf.disable())

      .headers( headers -> headers
          .frameOptions().sameOrigin()
      )

      .formLogin((form) -> form
          .permitAll()
           .loginPage(env.getProperty("flummox.origin")+"/#login") // if redirected from auth required
           .loginProcessingUrl("/api/user/login")
           .successHandler(successHandler)
           .failureHandler(failureHandler)
      )

      .logout((logout) -> logout
          .permitAll()
          .logoutUrl("/api/user/logout")
      )

      .anonymous( (anonymous) -> anonymous
          .authenticationFilter(anonAuthenticationFilter)
      )

      .sessionManagement(sessionManagementCustomizer -> 
          // ATTENTION : Needed for assumptions in anonAuthenticationFilter
          sessionManagementCustomizer.sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
      )


      .authorizeHttpRequests( request -> request

        //pages & resources

        .requestMatchers(mvcMatcherBuilder.pattern("/built/bundle.js")).permitAll()
        .requestMatchers(mvcMatcherBuilder.pattern("/audio/*")).permitAll()
        .requestMatchers(mvcMatcherBuilder.pattern("/favicon.ico")).permitAll()


        .requestMatchers(mvcMatcherBuilder.pattern("/")).permitAll()
        .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.POST, "/api/user/register")).permitAll()
        .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.POST, "/api/user/verify")).permitAll()

        //api

        .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET,  "/api/user/info")).permitAll()

        .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET,  "/api/lobby")).permitAll()
        .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET,  "/api/lobby/*")).permitAll()
        .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET,  "/api/lobby/*/messages")).permitAll()
        .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET,  "/api/lobby/invite")).permitAll()
        .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.POST, "/api/lobby/*/join")).permitAll()
        .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.POST, "/api/lobby/*/leave")).permitAll()
        .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.POST, "/api/lobby/create")).permitAll()
        .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.POST, "/api/lobby/*/update")).permitAll()
        .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.DELETE, "/api/lobby/*")).permitAll()

        .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.POST, "/api/lobby/*/start")).permitAll()
        .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.POST, "/api/lobby/*/kick/*")).permitAll()
        .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.POST, "/api/lobby/*/promote/*")).permitAll()
        .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.POST,  "/api/lobby/*/messages")).permitAll()

        .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET,  "/api/lobby/summary")).permitAll()
        .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET,  "/api/lobby/*/summary")).permitAll()
        .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET,  "/api/lobby/*/summary/sse")).permitAll()
        .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET,  "/api/lobby/*/messages/sse")).permitAll()


        .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.POST, "/api/game/*/answer/*")).permitAll()
        .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET,  "/api/game/*")).permitAll()
        .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET,  "/api/game/*/sse/*")).permitAll()
        .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET,  "/api/game/*/post-game/*")).permitAll()

        .anyRequest().authenticated()

      )

      .build();
  }

  @Bean PasswordEncoder passwordEncoder() {
    Map<String,PasswordEncoder> encoders = new HashMap<>();
    encoders.put("bcrypt",new BCryptPasswordEncoder(8));
    encoders.put("noop", NoOpPasswordEncoder.getInstance());
    String defaultEncoder = "bcrypt";
    DelegatingPasswordEncoder delegatingPasswordEncoder = new DelegatingPasswordEncoder(defaultEncoder,encoders);
    return delegatingPasswordEncoder;
  }

}
