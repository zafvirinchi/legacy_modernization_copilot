package com.ailegacy.modernization.copilot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main application entry point for AI Legacy Modernization Copilot.
 *
 * Features:
 * - Spring Boot 3 with Java 21
 * - MongoDB persistence
 * - JWT authentication
 * - LangChain4j AI integration
 * - REST API with Swagger documentation
 * - Clean Architecture with domain-driven design
 *
 * Mongo auditing is enabled in {@link com.ailegacy.modernization.copilot.infrastructure.config.MongoConfig}.
 */
@SpringBootApplication
@EnableScheduling
public class AiLegacyModernizationCopilotApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiLegacyModernizationCopilotApplication.class, args);
    }

}
