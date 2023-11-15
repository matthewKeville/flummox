package com.keville.ReBoggled.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.config.Customizer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

  private RsaKeyProperties rsaKeys;

  public SecurityConfig(@Autowired RsaKeyProperties rsaKeys) {
    this.rsaKeys = rsaKeys;
  }

  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
      .csrf(csrf -> csrf.disable())
      //.authorizeRequests( auth -> auth
      .authorizeHttpRequests( auth -> auth
          .anyRequest().authenticated()
      )
      //.oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
      .oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults()))
      .sessionManagement( session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .httpBasic(Customizer.withDefaults())
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

  @Bean
  JwtDecoder jwtDecoder() {    
    return NimbusJwtDecoder.withPublicKey(rsaKeys.publicKey()).build();
  }

  @Bean
  JwtEncoder jwtEncoder() {
    JWK jwk = new RSAKey.Builder(rsaKeys.publicKey()).privateKey(rsaKeys.privateKey()).build();
    JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
    return new NimbusJwtEncoder(jwks);
  }


  

}
