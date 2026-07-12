package com.ailegacy.modernization.copilot.application.use_cases.auth;

import com.ailegacy.modernization.copilot.application.services.AuthTokenService;
import com.ailegacy.modernization.copilot.domain.entities.User;
import com.ailegacy.modernization.copilot.domain.enums.Role;
import com.ailegacy.modernization.copilot.domain.exceptions.BusinessLogicException;
import com.ailegacy.modernization.copilot.domain.repositories.UserRepository;
import com.ailegacy.modernization.copilot.interfaces.rest.dto.auth.AuthTokenResponse;
import com.ailegacy.modernization.copilot.interfaces.rest.dto.auth.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthTokenService authTokenService;

    private RegisterUseCase registerUseCase;

    @BeforeEach
    void setUp() {
        registerUseCase = new RegisterUseCase(userRepository, passwordEncoder, authTokenService);
    }

    @Test
    void registersNewUserAndIssuesTokens() {
        RegisterRequest request = RegisterRequest.builder()
                .name("Grace Hopper")
                .email("Grace@Example.com")
                .password("supersecret")
                .role(Role.DEVELOPER)
                .build();

        when(userRepository.existsByEmail("grace@example.com")).thenReturn(false);
        when(passwordEncoder.encode("supersecret")).thenReturn("hashed-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId("generated-id");
            return user;
        });
        AuthTokenResponse expectedResponse = AuthTokenResponse.builder().accessToken("access").build();
        when(authTokenService.issueTokens(any(User.class))).thenReturn(expectedResponse);

        AuthTokenResponse response = registerUseCase.execute(request);

        assertThat(response).isEqualTo(expectedResponse);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getEmail()).isEqualTo("grace@example.com");
        assertThat(savedUser.getPasswordHash()).isEqualTo("hashed-password");
        assertThat(savedUser.getRole()).isEqualTo(Role.DEVELOPER);
        assertThat(savedUser.isEnabled()).isTrue();
    }

    @Test
    void rejectsRegistrationWhenEmailAlreadyExists() {
        RegisterRequest request = RegisterRequest.builder()
                .name("Grace Hopper")
                .email("grace@example.com")
                .password("supersecret")
                .role(Role.DEVELOPER)
                .build();

        when(userRepository.existsByEmail("grace@example.com")).thenReturn(true);

        assertThatThrownBy(() -> registerUseCase.execute(request))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("already exists");
    }

}
