package com.ailegacy.modernization.copilot.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * JWT token provider for token generation and validation.
 * 
 * Responsibilities:
 * - Generate JWT tokens from authentication
 * - Validate and extract claims from tokens
 * - Handle token expiration
 */
@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecretKey;

    @Value("${jwt.expiration-ms}")
    private long jwtExpirationMs;

    /**
     * Generate JWT token from authentication details
     */
    public String generateToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return generateToken(userPrincipal.getId(), userPrincipal.getUsername(), userPrincipal.getEmail());
    }

    /**
     * Generate JWT token with custom claims
     */
    public String generateToken(String userId, String username, String email) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecretKey.getBytes());
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(userId)
                .claim("username", username)
                .claim("email", email)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Get user ID from JWT token
     */
    public String getUserIdFromJWT(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecretKey.getBytes());
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        } catch (Exception ex) {
            log.error("Failed to extract user ID from JWT", ex);
            throw new RuntimeException("Failed to extract user ID from JWT", ex);
        }
    }

    /**
     * Get all claims from JWT token
     */
    public Claims getClaimsFromJWT(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecretKey.getBytes());
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception ex) {
            log.error("Failed to extract claims from JWT", ex);
            throw new RuntimeException("Failed to extract claims from JWT", ex);
        }
    }

    /**
     * Validate JWT token
     */
    public boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecretKey.getBytes());
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
            log.error("JWT validation failed: {}", ex.getMessage());
            return false;
        }
    }

}
