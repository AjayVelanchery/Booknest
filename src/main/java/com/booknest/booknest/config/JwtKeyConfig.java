package com.booknest.booknest.config;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;

@Configuration
public class JwtKeyConfig {

    private final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    @Bean
    public SecretKey jwtSecretKey() {
        return secretKey;
    }
}
