package com.ailegacy.modernization.copilot.application.use_cases.auth;

import com.ailegacy.modernization.copilot.application.services.AuthTokenService;
import com.ailegacy.modernization.copilot.domain.entities.User;
import com.ailegacy.modernization.copilot.domain.enums.Role;
import com.ailegacy.modernization.copilot.domain.exceptions.UnauthorizedException;
import com.ailegacy.modernization.copilot.domain.repositories.UserRepository;
import com.ailegacy.modernization.copilot.interfaces.rest.dto.auth.AuthTokenResponse;
import com.ailegacy.modernization.copilot.interfaces.rest.dto.auth.LoginRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthTokenService authTokenService;

    private LoginUseCase loginUseCase;

    private User existingUser;

    @BeforeEach
    void setUp() {
        loginUseCase = new LoginUseCase(userRepository, passwordEncoder, authTokenService);
        existingUser = User.builder()
                .id("user-1")
                .name("Ada Lovelace")
                .email("ada@example.com")
                .passwordHash("hashed-password")
                .role(Role.ADMIN)
                .enabled(true)
                .build();
    }

    @Test
    void issuesTokensForValidCredentials() {
        LoginRequest request = LoginRequest.builder().email("ada@example.com").password("correct-password").build();

        when(userRepository.findByEmail("ada@example.com")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("correct-password", "hashed-password")).thenReturn(true);
        AuthTokenResponse expectedResponse = AuthTokenResponse.builder().accessToken("access").build();
        when(authTokenService.issueTokens(existingUser)).thenReturn(expectedResponse);

        AuthTokenResponse response = loginUseCase.execute(request);

        assertThat(response).isEqualTo(expectedResponse);
    }

    @Test
    void rejectsUnknownEmail() {
        LoginRequest request = LoginRequest.builder().email("unknown@example.com").password("whatever").build();
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loginUseCase.execute(request))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Invalid email or password");
    }

    @Test
    void rejectsIncorrectPassword() {
        LoginRequest request = LoginRequest.builder().email("ada@example.com").password("wrong-password").build();

        when(userRepository.findByEmail("ada@example.com")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("wrong-password", "hashed-password")).thenReturn(false);

        assertThatThrownBy(() -> loginUseCase.execute(request))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Invalid email or password");
    }

    @Test
    void rejectsDisabledAccount() {
        existingUser.setEnabled(false);
        LoginRequest request = LoginRequest.builder().email("ada@example.com").password("correct-password").build();

        when(userRepository.findByEmail("ada@example.com")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("correct-password", "hashed-password")).thenReturn(true);

        assertThatThrownBy(() -> loginUseCase.execute(request))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("disabled");
    }

}
