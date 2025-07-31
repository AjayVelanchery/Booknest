package com.booknest.booknest.security;


import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;


@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    private final String jwtSecret="your_secret_key_example";
private final long jwtExpirationMs=86400000;

public String
    generateJwtToken(org.springframework.security.core.userdetails.UserDetails userDetails) {

    return Jwts.builder()
            .setSubject(userDetails.getUsername())
            .setIssuedAt(new Date())
            .setExpiration(new Date(new Date().getTime() + jwtExpirationMs))
            .signWith(SignatureAlgorithm.HS512, jwtSecret)
            .compact();
}

public String getUsernameFromJwtToken(String token){

    return
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();




}

public boolean validateJwtToken(String authToken){
    try{
        Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
        return true;
    }
    catch (JwtException | IllegalArgumentException e){
        logger.error("Invalid JWT token: {}", e.getMessage());
    }
  return false;
}

}
