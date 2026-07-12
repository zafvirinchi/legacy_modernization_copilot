package com.ailegacy.modernization.copilot.application.services;

import com.ailegacy.modernization.copilot.application.mappers.UserProfileMapper;
import com.ailegacy.modernization.copilot.domain.entities.RefreshToken;
import com.ailegacy.modernization.copilot.domain.entities.User;
import com.ailegacy.modernization.copilot.domain.repositories.RefreshTokenRepository;
import com.ailegacy.modernization.copilot.infrastructure.security.JwtTokenProvider;
import com.ailegacy.modernization.copilot.interfaces.rest.dto.auth.AuthTokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Issues access/refresh token pairs for an authenticated user and persists the
 * refresh token's hash so it can later be validated or revoked.
 *
 * Shared by registration, login and token refresh so token issuance stays
 * consistent across all three entry points.
 */
@Service
@RequiredArgsConstructor
public class AuthTokenService implements BaseApplicationService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserProfileMapper userProfileMapper;

    public AuthTokenResponse issueTokens(User user) {
        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);

        String jti = jwtTokenProvider.getJtiFromJWT(refreshToken);
        RefreshToken record = RefreshToken.builder()
                .userId(user.getId())
                .tokenHash(jwtTokenProvider.hashToken(jti))
                .expiresAt(jwtTokenProvider.getExpirationFromJWT(refreshToken))
                .revoked(false)
                .build();
        refreshTokenRepository.save(record);

        return AuthTokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getAccessTokenExpirationMs() / 1000)
                .user(userProfileMapper.toProfileResponse(user))
                .build();
    }

}
