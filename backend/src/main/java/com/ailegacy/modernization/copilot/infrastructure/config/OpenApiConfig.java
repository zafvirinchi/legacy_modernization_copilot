package com.ailegacy.modernization.copilot.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;

/**
 * OpenAPI / Swagger configuration for API documentation.
 *
 * Provides:
 * - API metadata (title, version, description)
 * - Contact information
 * - Security scheme definitions (JWT Bearer)
 */
@Slf4j
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        log.info("[STARTUP-DIAG] >> OpenApiConfig.customOpenAPI() starting at {}", Instant.now());
        OpenAPI openAPI = buildOpenAPI();
        log.info("[STARTUP-DIAG] << OpenApiConfig.customOpenAPI() finished at {}", Instant.now());
        return openAPI;
    }

    private OpenAPI buildOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AI Legacy Modernization Copilot API")
                        .version("1.0.0")
                        .description("REST API for analyzing and modernizing legacy enterprise applications")
                        .contact(new Contact()
                                .name("AI Legacy Team")
                                .email("support@ailegacy.com")
                                .url("https://github.com/ailegacy/modernization-copilot")
                        )
                        .license(new License()
                                .name("Apache License 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList("bearer_jwt"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("bearer_jwt", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Enter JWT token")
                        )
                );
    }

}
