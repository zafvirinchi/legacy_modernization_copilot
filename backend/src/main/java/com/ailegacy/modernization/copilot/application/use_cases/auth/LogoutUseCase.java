package com.ailegacy.modernization.copilot.application.use_cases.auth;

import com.ailegacy.modernization.copilot.application.use_cases.UseCase;
import com.ailegacy.modernization.copilot.domain.repositories.RefreshTokenRepository;
import com.ailegacy.modernization.copilot.infrastructure.security.JwtTokenProvider;
import com.ailegacy.modernization.copilot.interfaces.rest.dto.auth.RefreshTokenRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Revokes the presented refresh token so it can no longer be redeemed.
 *
 * Idempotent and intentionally tolerant of already-invalid tokens: logout
 * always succeeds from the caller's perspective.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LogoutUseCase implements UseCase<RefreshTokenRequest, Void> {

    private static final String TOKEN_TYPE_REFRESH = "refresh";

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public Void execute(RefreshTokenRequest request) {
        String token = request.getRefreshToken();

        if (jwtTokenProvider.validateToken(token) && TOKEN_TYPE_REFRESH.equals(jwtTokenProvider.getTokenType(token))) {
            String hash = jwtTokenProvider.hashToken(jwtTokenProvider.getJtiFromJWT(token));
            refreshTokenRepository.findByTokenHash(hash).ifPresent(refreshToken -> {
                refreshToken.setRevoked(true);
                refreshTokenRepository.save(refreshToken);
                log.info("User logged out | userId={}", refreshToken.getUserId());
            });
        }

        return null;
    }

}
