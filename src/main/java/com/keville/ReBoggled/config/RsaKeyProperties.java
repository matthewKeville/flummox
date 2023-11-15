package com.keville.ReBoggled.config;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import org.springframework.boot.context.properties.ConfigurationProperties;

// I think we are defining configuration properties that go into application.properties
// rsa.private-key slots into -> privateKey
// rsa.public-key slots into -> publicKey
@ConfigurationProperties(prefix = "rsa")
public record RsaKeyProperties(RSAPublicKey publicKey, RSAPrivateKey privateKey) {

}
