package com.ailegacy.modernization.copilot.infrastructure.config;

import com.ailegacy.modernization.copilot.infrastructure.security.JwtAuthenticationFilter;
import com.ailegacy.modernization.copilot.infrastructure.security.JwtTokenProvider;
import com.ailegacy.modernization.copilot.infrastructure.security.RestAccessDeniedHandler;
import com.ailegacy.modernization.copilot.infrastructure.security.RestAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Spring Security configuration for JWT-based authentication.
 *
 * - Enables JWT token validation
 * - Configures CORS for frontend communication
 * - Sets up session management
 */
@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final RestAccessDeniedHandler restAccessDeniedHandler;

    @Value("${app.frontend-url:}")
    private String frontendUrl;

    @Bean
    public PasswordEncoder passwordEncoder() {
        log.info("[STARTUP-DIAG] >> SecurityConfig.passwordEncoder() starting at {}", Instant.now());
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        log.info("[STARTUP-DIAG] << SecurityConfig.passwordEncoder() finished at {}", Instant.now());
        return encoder;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("[STARTUP-DIAG] >> SecurityConfig.filterChain() starting at {}", Instant.now());
        SecurityFilterChain chain = buildFilterChain(http);
        log.info("[STARTUP-DIAG] << SecurityConfig.filterChain() finished at {}", Instant.now());
        return chain;
    }

    private SecurityFilterChain buildFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf().disable()
                .cors().and()
                .exceptionHandling()
                .authenticationEntryPoint(restAuthenticationEntryPoint)
                .accessDeniedHandler(restAccessDeniedHandler)
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests((authz) -> authz
                        .requestMatchers("/auth/register", "/auth/login", "/auth/refresh").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/health").permitAll()
                        .requestMatchers("/info").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        log.info("[STARTUP-DIAG] >> SecurityConfig.corsConfigurationSource() starting at {}", Instant.now());
        CorsConfigurationSource source = buildCorsConfigurationSource();
        log.info("[STARTUP-DIAG] << SecurityConfig.corsConfigurationSource() finished at {}", Instant.now());
        return source;
    }

    private CorsConfigurationSource buildCorsConfigurationSource() {
        // Origin patterns (not exact origins) so the Vercel preview-deployment
        // wildcard works; required by Spring when allowCredentials is also true.
        List<String> allowedOriginPatterns = new ArrayList<>(List.of(
                "http://localhost:3000",
                "http://localhost:5173",
                "http://localhost:9090",
                "https://*.vercel.app"
        ));
        if (!frontendUrl.isBlank()) {
            allowedOriginPatterns.add(frontendUrl);
        }

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(allowedOriginPatterns);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
