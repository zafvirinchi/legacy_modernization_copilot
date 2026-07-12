package com.ailegacy.modernization.copilot.infrastructure.security;

import com.ailegacy.modernization.copilot.domain.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Date;
import java.util.HexFormat;
import java.util.UUID;

/**
 * JWT token provider for access/refresh token generation and validation.
 *
 * Access tokens carry the user's identity and role and are used to authenticate
 * API requests. Refresh tokens are long-lived, carry only a unique identifier (jti)
 * and are exchanged for a new token pair; the caller is responsible for persisting
 * a hash of the jti so refresh tokens can be individually revoked.
 */
@Slf4j
@Component
public class JwtTokenProvider {

    private static final String CLAIM_TYPE = "type";
    private static final String CLAIM_ROLE = "role";
    private static final String CLAIM_EMAIL = "email";
    private static final String CLAIM_NAME = "name";
    private static final String TOKEN_TYPE_ACCESS = "access";
    private static final String TOKEN_TYPE_REFRESH = "refresh";

    @Value("${jwt.secret}")
    private String jwtSecretKey;

    @Value("${jwt.expiration-ms}")
    private long accessTokenExpirationMs;

    @Value("${jwt.refresh-expiration-ms}")
    private long refreshTokenExpirationMs;

    public long getAccessTokenExpirationMs() {
        return accessTokenExpirationMs;
    }

    /**
     * Generate a short-lived access token carrying the user's identity and role.
     */
    public String generateAccessToken(User user) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenExpirationMs);

        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setSubject(user.getId())
                .claim(CLAIM_TYPE, TOKEN_TYPE_ACCESS)
                .claim(CLAIM_NAME, user.getName())
                .claim(CLAIM_EMAIL, user.getEmail())
                .claim(CLAIM_ROLE, user.getRole().name())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(signingKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Generate a long-lived refresh token. The token's jti must be hashed and persisted
     * by the caller so it can later be looked up and revoked.
     */
    public String generateRefreshToken(User user) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + refreshTokenExpirationMs);

        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setSubject(user.getId())
                .claim(CLAIM_TYPE, TOKEN_TYPE_REFRESH)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(signingKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Validate token signature and expiration.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(signingKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
            log.warn("JWT validation failed: {}", ex.getMessage());
            return false;
        }
    }

    public String getUserIdFromJWT(String token) {
        return getClaimsFromJWT(token).getSubject();
    }

    public String getJtiFromJWT(String token) {
        return getClaimsFromJWT(token).getId();
    }

    public String getTokenType(String token) {
        return getClaimsFromJWT(token).get(CLAIM_TYPE, String.class);
    }

    public String getRoleFromJWT(String token) {
        return getClaimsFromJWT(token).get(CLAIM_ROLE, String.class);
    }

    public Instant getExpirationFromJWT(String token) {
        return getClaimsFromJWT(token).getExpiration().toInstant();
    }

    public Claims getClaimsFromJWT(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(signingKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception ex) {
            log.error("Failed to extract claims from JWT", ex);
            throw new RuntimeException("Failed to extract claims from JWT", ex);
        }
    }

    /**
     * Hash a token identifier (jti) for safe storage. Refresh tokens themselves are
     * never persisted, only this one-way hash of their unique identifier.
     */
    public String hashToken(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 algorithm not available", ex);
        }
    }

    private SecretKey signingKey() {
        return Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }

}
