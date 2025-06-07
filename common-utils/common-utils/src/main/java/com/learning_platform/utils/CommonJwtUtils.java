package com.learning_platform.utils;

import com.learning_platform.dtos.UserDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CommonJwtUtils {
  @Value("${jwt.secret:defaultSecretKey123456789012345678901234567890}")
  private String secret;

  @Value("${jwt.access-expiration:3600000}")
  protected Long accessTokenExpiration;

  @Value("${jwt.refresh-expiration:7200000}")
  protected Long refreshTokenExpiration;

  private final String BEARER_PREFIX = "Bearer ";

  protected Key getSignKey() {
    byte[] keyBytes = Decoders.BASE64.decode(secret);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  public Boolean validateToken(String token, UserDto userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
  }

  public String extractUsername(String token) {
    if (token.startsWith(BEARER_PREFIX)) {
      token = token.substring(BEARER_PREFIX.length());
    }
    return extractClaim(token, Claims::getSubject);
  }

  public Claims decodeJWTClaims(String token) {
    if (token.startsWith(BEARER_PREFIX)) {
      token = token.substring(BEARER_PREFIX.length());
    }
    return extractAllClaims(token);
  }

  private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token).getBody();
  }

  private Boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }
}
