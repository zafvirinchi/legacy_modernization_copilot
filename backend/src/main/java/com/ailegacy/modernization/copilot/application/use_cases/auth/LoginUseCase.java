package com.ailegacy.modernization.copilot.application.use_cases.auth;

import com.ailegacy.modernization.copilot.application.services.AuthTokenService;
import com.ailegacy.modernization.copilot.application.use_cases.UseCase;
import com.ailegacy.modernization.copilot.domain.entities.User;
import com.ailegacy.modernization.copilot.domain.exceptions.UnauthorizedException;
import com.ailegacy.modernization.copilot.domain.repositories.UserRepository;
import com.ailegacy.modernization.copilot.interfaces.rest.dto.auth.AuthTokenResponse;
import com.ailegacy.modernization.copilot.interfaces.rest.dto.auth.LoginRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Authenticates a user by email/password and issues a token pair.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LoginUseCase implements UseCase<LoginRequest, AuthTokenResponse> {

    private static final String INVALID_CREDENTIALS_MESSAGE = "Invalid email or password";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthTokenService authTokenService;

    @Override
    public AuthTokenResponse execute(LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Login failed, no account for email | email={}", email);
                    return new UnauthorizedException(INVALID_CREDENTIALS_MESSAGE);
                });

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            log.warn("Login failed, invalid password | userId={}", user.getId());
            throw new UnauthorizedException(INVALID_CREDENTIALS_MESSAGE);
        }

        if (!user.isEnabled()) {
            log.warn("Login rejected, account disabled | userId={}", user.getId());
            throw new UnauthorizedException("Account is disabled");
        }

        log.info("User logged in | userId={}", user.getId());
        return authTokenService.issueTokens(user);
    }

}
