package com.keville.ReBoggled.security;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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

      .formLogin((form) -> form
          .permitAll()
          .loginPage("/login")
          .successHandler( authenticationSuccessHandler )
      )

      //invalid user session when /logout
      .logout((logout) -> logout.permitAll())

      .authorizeHttpRequests( request -> request

        .requestMatchers(mvcMatcherBuilder.pattern("/h2-console/*")).permitAll()

        .requestMatchers(mvcMatcherBuilder.pattern("/built/bundle.js")).permitAll()
        /* long term, these things should be bundled */
        .requestMatchers(mvcMatcherBuilder.pattern("/css/style.css")).permitAll()
        .requestMatchers(mvcMatcherBuilder.pattern("/css/buttons.css")).permitAll()
        .requestMatchers(mvcMatcherBuilder.pattern("/favicon.ico")).permitAll()
        .requestMatchers(mvcMatcherBuilder.pattern("/icons/user-profile-white-trans.png")).permitAll()
        .requestMatchers(mvcMatcherBuilder.pattern("/icons/user-profile-black-trans.png")).permitAll()
        .requestMatchers(mvcMatcherBuilder.pattern("/icons/user-profile-white-full-trans.png")).permitAll()
        .requestMatchers(mvcMatcherBuilder.pattern("/icons/user-profile-black-full-trans.png")).permitAll()

        .requestMatchers(mvcMatcherBuilder.pattern("/")).permitAll()
        .requestMatchers(mvcMatcherBuilder.pattern("/error")).permitAll()
        .requestMatchers(mvcMatcherBuilder.pattern("/login")).permitAll()

        .requestMatchers(mvcMatcherBuilder.pattern("/signup")).permitAll()
        .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.POST, "/register")).permitAll()

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

  @Bean PasswordEncoder passwordEncoder() {
    Map<String,PasswordEncoder> encoders = new HashMap<>();
    encoders.put("bcrypt",new BCryptPasswordEncoder(8));
    encoders.put("noop", NoOpPasswordEncoder.getInstance());
    String defaultEncoder = "bcrypt";
    DelegatingPasswordEncoder delegatingPasswordEncoder = new DelegatingPasswordEncoder(defaultEncoder,encoders);
    return delegatingPasswordEncoder;
  }

}
