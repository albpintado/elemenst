package com.albpintado.elemenst.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;



// Utility to create token and validate users against tokens.
@Component
public class TokenManager {

  // Secret to generate tokens securely (raw string by the moment).
  private static final String ACCESS_TOKEN_SECRET = "P4jgeSv65dBW4^5wdM3C#&3Jvym4#NTJ";

  // Validity in seconds for the token generated.
  private static final long ACCESS_TOKEN_VALIDITY = 2_500_000;

  /*
  Function to create a token based on the name and the email of the user,
  with an expiration date calculated based on the validity in seconds.
   */
  public static String createToken(String userName) {
    Date expirationDate = new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY * 1000);

    Map<String, Object> pairToAddToClaim = new HashMap<>();
    pairToAddToClaim.put("userName", userName);
    return Jwts.builder()
            .setSubject(userName)
            .setExpiration(expirationDate)
            .addClaims(pairToAddToClaim)
            .signWith(Keys.hmacShaKeyFor(ACCESS_TOKEN_SECRET.getBytes(StandardCharsets.UTF_8)))
            .compact();
  }

  // Function to authenticate the string token and link it to the email of the user
  public static UsernamePasswordAuthenticationToken getAuthentication(String token) {
    try {
      Claims claims = Jwts.parserBuilder()
              .setSigningKey(ACCESS_TOKEN_SECRET.getBytes())
              .build()
              .parseClaimsJws(token)
              .getBody();

      String userName = claims.getSubject();
      return new UsernamePasswordAuthenticationToken(userName, null, Collections.emptyList());
    } catch (JwtException e) {
      return null;
    }
  }
}
