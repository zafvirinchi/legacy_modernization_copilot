package com.ailegacy.modernization.copilot.interfaces.rest.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Health check endpoint used for readiness/liveness probing and monitoring.
 */
@Slf4j
@RestController
@Tag(name = "Health", description = "Service health and readiness checks")
public class HealthController {

    private final Environment environment;

    @Value("${app.name:AI Legacy Modernization Copilot}")
    private String appName;

    @Value("${app.version:1.0.0}")
    private String appVersion;

    public HealthController(Environment environment) {
        this.environment = environment;
    }

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Returns the current status of the service")
    public Map<String, Object> health() {
        log.debug("Health check requested");

        String[] activeProfiles = environment.getActiveProfiles();
        String environmentName = activeProfiles.length > 0 ? activeProfiles[0] : "default";

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "UP");
        response.put("service", appName);
        response.put("version", appVersion);
        response.put("environment", environmentName);
        response.put("timestamp", LocalDateTime.now().toString());
        return response;
    }

}
