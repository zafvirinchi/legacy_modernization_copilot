package com.ailegacy.modernization.copilot.application.use_cases.auth;

import com.ailegacy.modernization.copilot.application.services.AuthTokenService;
import com.ailegacy.modernization.copilot.application.use_cases.UseCase;
import com.ailegacy.modernization.copilot.domain.entities.RefreshToken;
import com.ailegacy.modernization.copilot.domain.entities.User;
import com.ailegacy.modernization.copilot.domain.exceptions.UnauthorizedException;
import com.ailegacy.modernization.copilot.domain.repositories.RefreshTokenRepository;
import com.ailegacy.modernization.copilot.domain.repositories.UserRepository;
import com.ailegacy.modernization.copilot.infrastructure.security.JwtTokenProvider;
import com.ailegacy.modernization.copilot.interfaces.rest.dto.auth.AuthTokenResponse;
import com.ailegacy.modernization.copilot.interfaces.rest.dto.auth.RefreshTokenRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Redeems a refresh token for a new access/refresh token pair.
 *
 * The presented refresh token is single-use: on success it is revoked and a new
 * pair is issued (rotation), which limits the blast radius of a leaked token.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RefreshTokenUseCase implements UseCase<RefreshTokenRequest, AuthTokenResponse> {

    private static final String TOKEN_TYPE_REFRESH = "refresh";
    private static final String INVALID_TOKEN_MESSAGE = "Invalid or expired refresh token";

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final AuthTokenService authTokenService;

    @Override
    public AuthTokenResponse execute(RefreshTokenRequest request) {
        String token = request.getRefreshToken();

        if (!jwtTokenProvider.validateToken(token) || !TOKEN_TYPE_REFRESH.equals(jwtTokenProvider.getTokenType(token))) {
            throw new UnauthorizedException(INVALID_TOKEN_MESSAGE);
        }

        String hash = jwtTokenProvider.hashToken(jwtTokenProvider.getJtiFromJWT(token));
        RefreshToken stored = refreshTokenRepository.findByTokenHash(hash)
                .orElseThrow(() -> new UnauthorizedException(INVALID_TOKEN_MESSAGE));

        if (stored.isRevoked() || stored.getExpiresAt().isBefore(Instant.now())) {
            log.warn("Refresh token reuse or expiry detected | userId={}", stored.getUserId());
            throw new UnauthorizedException(INVALID_TOKEN_MESSAGE);
        }

        User user = userRepository.findById(jwtTokenProvider.getUserIdFromJWT(token))
                .orElseThrow(() -> new UnauthorizedException(INVALID_TOKEN_MESSAGE));

        stored.setRevoked(true);
        refreshTokenRepository.save(stored);

        log.info("Refresh token rotated | userId={}", user.getId());
        return authTokenService.issueTokens(user);
    }

}
