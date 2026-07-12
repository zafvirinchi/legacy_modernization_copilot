package com.ailegacy.modernization.copilot.interfaces.rest.controllers;

import com.ailegacy.modernization.copilot.application.use_cases.auth.GetProfileUseCase;
import com.ailegacy.modernization.copilot.application.use_cases.auth.LoginUseCase;
import com.ailegacy.modernization.copilot.application.use_cases.auth.LogoutUseCase;
import com.ailegacy.modernization.copilot.application.use_cases.auth.RefreshTokenUseCase;
import com.ailegacy.modernization.copilot.application.use_cases.auth.RegisterUseCase;
import com.ailegacy.modernization.copilot.infrastructure.security.UserPrincipal;
import com.ailegacy.modernization.copilot.interfaces.rest.dto.ApiResponse;
import com.ailegacy.modernization.copilot.interfaces.rest.dto.auth.AuthTokenResponse;
import com.ailegacy.modernization.copilot.interfaces.rest.dto.auth.LoginRequest;
import com.ailegacy.modernization.copilot.interfaces.rest.dto.auth.RefreshTokenRequest;
import com.ailegacy.modernization.copilot.interfaces.rest.dto.auth.RegisterRequest;
import com.ailegacy.modernization.copilot.interfaces.rest.dto.auth.UserProfileResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Authentication endpoints: registration, login, token refresh, logout and profile retrieval.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Registration, login, token refresh and profile management")
public class AuthController {

    private final RegisterUseCase registerUseCase;
    private final LoginUseCase loginUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final LogoutUseCase logoutUseCase;
    private final GetProfileUseCase getProfileUseCase;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register a new account", description = "Creates a new user with the requested role and returns an initial token pair")
    public ApiResponse<AuthTokenResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthTokenResponse response = registerUseCase.execute(request);
        return ApiResponse.success(response, "Account created successfully");
    }

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticates a user by email and password and returns a token pair")
    public ApiResponse<AuthTokenResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthTokenResponse response = loginUseCase.execute(request);
        return ApiResponse.success(response, "Login successful");
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Exchanges a valid refresh token for a new access/refresh token pair")
    public ApiResponse<AuthTokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        AuthTokenResponse response = refreshTokenUseCase.execute(request);
        return ApiResponse.success(response, "Token refreshed successfully");
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Revokes the presented refresh token")
    public ApiResponse<Void> logout(@Valid @RequestBody RefreshTokenRequest request) {
        logoutUseCase.execute(request);
        return ApiResponse.success(null, "Logged out successfully");
    }

    @GetMapping("/me")
    @Operation(summary = "Get current profile", description = "Returns the profile of the currently authenticated user")
    public ApiResponse<UserProfileResponse> me(@AuthenticationPrincipal UserPrincipal principal) {
        UserProfileResponse response = getProfileUseCase.execute(principal.getId());
        return ApiResponse.success(response);
    }

}
