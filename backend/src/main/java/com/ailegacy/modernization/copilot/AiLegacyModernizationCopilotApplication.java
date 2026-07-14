package com.ailegacy.modernization.copilot;

import com.ailegacy.modernization.copilot.infrastructure.config.diagnostics.StartupLifecycleEventLogger;
import com.ailegacy.modernization.copilot.infrastructure.config.diagnostics.StartupWatchdog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
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
 * No autoconfigurations are excluded here - this build is deliberately at full,
 * unmodified functionality for the exhaustive Northflank startup-hang
 * diagnostics living under {@code infrastructure.config.diagnostics}. See
 * that package for what's instrumented and why.
 */
@SpringBootApplication
@EnableScheduling
public class AiLegacyModernizationCopilotApplication {

    private static final Logger log = LoggerFactory.getLogger(AiLegacyModernizationCopilotApplication.class);

    public static void main(String[] args) {
        // Diagnostic instrumentation (Northflank startup-hang investigation), deliberately
        // started before any Spring code runs at all, so a hang anywhere - even before the
        // ApplicationContext exists - is still visible. Safe to remove once diagnosed: see
        // infrastructure.config.diagnostics package Javadoc for the full list of what's added.
        Thread.setDefaultUncaughtExceptionHandler((thread, ex) ->
                log.error("[UNCAUGHT] Thread '{}' (daemon={}) died with an uncaught exception",
                        thread.getName(), thread.isDaemon(), ex));

        StartupWatchdog watchdog = new StartupWatchdog();
        watchdog.start();

        SpringApplication app = new SpringApplication(AiLegacyModernizationCopilotApplication.class);
        // spring.main.application-startup is NOT a bindable YAML property (there is no
        // String->ApplicationStartup converter - confirmed by a real ConverterNotFoundException
        // the first time this was tried as a property). ApplicationStartup can only be wired
        // programmatically like this. Buffered step timings are retrievable via
        // GET /api/actuator/startup, IF the process gets far enough to serve HTTP requests at all.
        app.setApplicationStartup(new BufferingApplicationStartup(2048));
        // addListeners (as opposed to a @Component/@EventListener bean) is required to catch the
        // early SpringApplicationEvents (ApplicationStartingEvent etc.) that fire before any
        // ApplicationContext - and therefore any Spring-managed bean - exists yet.
        app.addListeners(new StartupLifecycleEventLogger(watchdog));
        app.run(args);
    }

}
