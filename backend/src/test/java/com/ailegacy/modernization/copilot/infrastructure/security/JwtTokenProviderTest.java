package com.ailegacy.modernization.copilot.infrastructure.security;

import com.ailegacy.modernization.copilot.domain.entities.User;
import com.ailegacy.modernization.copilot.domain.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private User user;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecretKey", "test-secret-key-for-jwt-signing-must-be-at-least-64-bytes-long-for-hs512-to-accept-it");
        ReflectionTestUtils.setField(jwtTokenProvider, "accessTokenExpirationMs", 60_000L);
        ReflectionTestUtils.setField(jwtTokenProvider, "refreshTokenExpirationMs", 120_000L);

        user = User.builder()
                .id("user-1")
                .name("Ada Lovelace")
                .email("ada@example.com")
                .role(Role.ARCHITECT)
                .build();
    }

    @Test
    void accessTokenCarriesUserIdentityAndRole() {
        String token = jwtTokenProvider.generateAccessToken(user);

        assertThat(jwtTokenProvider.validateToken(token)).isTrue();
        assertThat(jwtTokenProvider.getUserIdFromJWT(token)).isEqualTo("user-1");
        assertThat(jwtTokenProvider.getRoleFromJWT(token)).isEqualTo("ARCHITECT");
        assertThat(jwtTokenProvider.getTokenType(token)).isEqualTo("access");
    }

    @Test
    void refreshTokenIsDistinguishableFromAccessToken() {
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);

        assertThat(jwtTokenProvider.validateToken(refreshToken)).isTrue();
        assertThat(jwtTokenProvider.getTokenType(refreshToken)).isEqualTo("refresh");
        assertThat(jwtTokenProvider.getJtiFromJWT(refreshToken)).isNotBlank();
    }

    @Test
    void tokenSignedWithDifferentKeyFailsValidation() {
        String token = jwtTokenProvider.generateAccessToken(user);

        JwtTokenProvider otherProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(otherProvider, "jwtSecretKey", "a-completely-different-signing-key-value-that-is-also-at-least-64-bytes-long-for-hs512");
        ReflectionTestUtils.setField(otherProvider, "accessTokenExpirationMs", 60_000L);
        ReflectionTestUtils.setField(otherProvider, "refreshTokenExpirationMs", 120_000L);

        assertThat(otherProvider.validateToken(token)).isFalse();
    }

    @Test
    void hashTokenIsDeterministicAndOneWay() {
        String hash1 = jwtTokenProvider.hashToken("some-jti-value");
        String hash2 = jwtTokenProvider.hashToken("some-jti-value");

        assertThat(hash1).isEqualTo(hash2);
        assertThat(hash1).isNotEqualTo("some-jti-value");
    }

}
