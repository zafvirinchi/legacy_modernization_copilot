package com.ailegacy.modernization.copilot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
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
 *
 * TEMPORARY DIAGNOSTIC (Northflank startup-hang investigation, see
 * [[northflank-mongo-srv-dns-hang]] finding): MongoAutoConfiguration and
 * MongoDataAutoConfiguration are excluded below to conclusively test whether
 * MongoClient bean construction (specifically its synchronous mongodb+srv://
 * DNS SRV lookup) is what prevents ApplicationContext refresh from completing
 * on Northflank. This does NOT remove any repository/service/controller code -
 * only the autoconfiguration that backs Mongo connectivity is disabled, so
 * MongoRepository beans will fail to construct (no MongoTemplate available)
 * if this build is ever used for real traffic. REVERT this exclusion once the
 * diagnostic is complete - it must not stay in place.
 */
@SpringBootApplication(exclude = {
        MongoAutoConfiguration.class,
        MongoDataAutoConfiguration.class
})
@EnableScheduling
public class AiLegacyModernizationCopilotApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(AiLegacyModernizationCopilotApplication.class);
        // Diagnostic instrumentation for the Northflank startup-hang investigation:
        // spring.main.application-startup is NOT a bindable YAML property (there is
        // no String->ApplicationStartup converter - confirmed by a real
        // ConverterNotFoundException the first time this was tried as a property).
        // ApplicationStartup can only be wired programmatically like this. Buffered
        // step timings are retrievable via GET /api/actuator/startup, IF the process
        // gets far enough to serve HTTP requests at all.
        app.setApplicationStartup(new BufferingApplicationStartup(2048));
        app.run(args);
    }

}
