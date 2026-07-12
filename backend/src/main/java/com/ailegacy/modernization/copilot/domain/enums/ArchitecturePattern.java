package com.ailegacy.modernization.copilot.domain.enums;

import java.util.Locale;
import java.util.Map;

/**
 * Architectural patterns the architecture analyzer can classify a project as.
 */
public enum ArchitecturePattern {

    MONOLITH,
    MVC,
    LAYERED,
    CLIENT_SERVER,
    MICROSERVICE;

    private static final Map<String, ArchitecturePattern> ALIASES = Map.ofEntries(
            Map.entry("MONOLITH", MONOLITH),
            Map.entry("MONOLITHIC", MONOLITH),
            Map.entry("MVC", MVC),
            Map.entry("MODEL_VIEW_CONTROLLER", MVC),
            Map.entry("LAYERED", LAYERED),
            Map.entry("LAYERED_ARCHITECTURE", LAYERED),
            Map.entry("N_TIER", LAYERED),
            Map.entry("MULTI_TIER", LAYERED),
            Map.entry("CLIENT_SERVER", CLIENT_SERVER),
            Map.entry("CLIENT_SERVER_ARCHITECTURE", CLIENT_SERVER),
            Map.entry("MICROSERVICE", MICROSERVICE),
            Map.entry("MICROSERVICES", MICROSERVICE),
            Map.entry("MICROSERVICE_ARCHITECTURE", MICROSERVICE)
    );

    /**
     * Normalizes a free-form LLM label (e.g. "Client-Server", "microservices")
     * into one of the fixed patterns. LLM output is never guaranteed to match
     * an exact enum token, so common variants are tolerated here rather than
     * failing the whole analysis over formatting differences.
     */
    public static ArchitecturePattern fromLabel(String label) {
        if (label == null) {
            throw new IllegalArgumentException("Architecture pattern label is missing");
        }
        String normalized = label.trim().toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9]+", "_");
        ArchitecturePattern pattern = ALIASES.get(normalized);
        if (pattern == null) {
            throw new IllegalArgumentException("Unrecognized architecture pattern: " + label);
        }
        return pattern;
    }

}
