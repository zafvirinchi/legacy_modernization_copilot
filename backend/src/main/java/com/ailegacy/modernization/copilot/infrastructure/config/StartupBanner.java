package com.ailegacy.modernization.copilot.infrastructure.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Logs a concise summary of the resolved runtime configuration once the
 * application is fully up, to make deployment diagnostics (e.g. on
 * Northflank) straightforward from the log output alone. Never logs secrets
 * (JWT signing key, Mongo credentials, OpenAI API key) - only whether each
 * is configured and where the app is pointed.
 */
@Slf4j
@Component
public class StartupBanner implements ApplicationListener<ApplicationReadyEvent> {

    private final Environment environment;

    @Value("${server.port}")
    private String serverPort;

    @Value("${server.address:0.0.0.0}")
    private String serverAddress;

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @Value("${MONGODB_CLUSTER:not configured}")
    private String mongoCluster;

    @Value("${spring.data.mongodb.database:unknown}")
    private String mongoDatabase;

    @Value("${llm.openai.model}")
    private String openAiModel;

    @Value("${llm.openai.api-key:}")
    private String openAiApiKey;

    public StartupBanner(Environment environment) {
        this.environment = environment;
    }

    /**
     * Logs the address/port Tomcat is about to bind to. {@code customize()} runs
     * while Spring Boot configures the {@code ServletWebServerFactory}, which
     * happens before the web server actually starts listening - so this is the
     * last point at which we can confirm what Tomcat is *about* to do, useful for
     * diagnosing "no healthy upstream"-style failures where the process starts
     * but never becomes reachable on the platform's expected port/interface.
     */
    @Bean
    public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> logResolvedPortBeforeBind() {
        log.info("[STARTUP-DIAG] >> StartupBanner.logResolvedPortBeforeBind() starting at {}", Instant.now());
        WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> customizer =
                factory -> log.info("Binding embedded Tomcat to {}:{} (before web server start)", serverAddress, serverPort);
        log.info("[STARTUP-DIAG] << StartupBanner.logResolvedPortBeforeBind() finished at {}", Instant.now());
        return customizer;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        String[] activeProfiles = environment.getActiveProfiles();
        String profile = activeProfiles.length > 0 ? String.join(",", activeProfiles) : "default";

        log.info("=================================================================");
        log.info(" AI Legacy Modernization Copilot - startup complete");
        log.info(" Java version      : {}", System.getProperty("java.version"));
        log.info(" Spring profile    : {}", profile);
        log.info(" Server port       : {}", serverPort);
        log.info(" Context path      : {}", contextPath.isBlank() ? "/" : contextPath);
        log.info(" MongoDB cluster   : {}", mongoCluster);
        log.info(" MongoDB database  : {}", mongoDatabase);
        log.info(" OpenAI model      : {}", openAiModel);
        log.info(" OpenAI configured : {}", openAiApiKey.isBlank() ? "no (AI features disabled)" : "yes");
        log.info(" Health check      : http://localhost:{}{}/actuator/health", serverPort, contextPath);
        log.info("=================================================================");
    }

}
