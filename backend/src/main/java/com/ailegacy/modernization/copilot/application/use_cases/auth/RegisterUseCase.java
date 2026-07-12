package com.ailegacy.modernization.copilot.application.use_cases.auth;

import com.ailegacy.modernization.copilot.application.services.AuthTokenService;
import com.ailegacy.modernization.copilot.application.use_cases.UseCase;
import com.ailegacy.modernization.copilot.domain.entities.User;
import com.ailegacy.modernization.copilot.domain.exceptions.BusinessLogicException;
import com.ailegacy.modernization.copilot.domain.repositories.UserRepository;
import com.ailegacy.modernization.copilot.interfaces.rest.dto.auth.AuthTokenResponse;
import com.ailegacy.modernization.copilot.interfaces.rest.dto.auth.RegisterRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Registers a new user account and issues an initial token pair.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RegisterUseCase implements UseCase<RegisterRequest, AuthTokenResponse> {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthTokenService authTokenService;

    @Override
    public AuthTokenResponse execute(RegisterRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            log.warn("Registration rejected, email already in use | email={}", email);
            throw new BusinessLogicException("An account with this email already exists", "EMAIL_ALREADY_EXISTS");
        }

        User user = User.builder()
                .name(request.getName().trim())
                .email(email)
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .enabled(true)
                .build();

        User saved = userRepository.save(user);
        log.info("User registered | userId={} | role={}", saved.getId(), saved.getRole());

        return authTokenService.issueTokens(saved);
    }

}
