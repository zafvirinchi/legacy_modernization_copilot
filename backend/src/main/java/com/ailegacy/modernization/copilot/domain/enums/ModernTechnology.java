package com.ailegacy.modernization.copilot.domain.enums;

import java.util.Locale;
import java.util.Map;

/**
 * Fixed checklist of modern-stack technologies the modernization planner
 * evaluates for relevance to a given legacy project.
 */
public enum ModernTechnology {

    SPRING_BOOT,
    SPRING_SECURITY,
    DOCKER,
    KUBERNETES,
    KAFKA,
    REDIS,
    OPENAPI,
    CLOUD_MIGRATION;

    private static final Map<String, ModernTechnology> ALIASES = Map.ofEntries(
            Map.entry("SPRING_BOOT", SPRING_BOOT),
            Map.entry("SPRINGBOOT", SPRING_BOOT),
            Map.entry("SPRING_SECURITY", SPRING_SECURITY),
            Map.entry("SPRINGSECURITY", SPRING_SECURITY),
            Map.entry("DOCKER", DOCKER),
            Map.entry("KUBERNETES", KUBERNETES),
            Map.entry("K8S", KUBERNETES),
            Map.entry("KAFKA", KAFKA),
            Map.entry("APACHE_KAFKA", KAFKA),
            Map.entry("REDIS", REDIS),
            Map.entry("OPENAPI", OPENAPI),
            Map.entry("OPEN_API", OPENAPI),
            Map.entry("SWAGGER", OPENAPI),
            Map.entry("CLOUD_MIGRATION", CLOUD_MIGRATION),
            Map.entry("CLOUD", CLOUD_MIGRATION)
    );

    /**
     * Normalizes a free-form LLM label into one of the fixed checklist entries.
     */
    public static ModernTechnology fromLabel(String label) {
        if (label == null) {
            throw new IllegalArgumentException("Modern technology label is missing");
        }
        String normalized = label.trim().toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9]+", "_");
        ModernTechnology technology = ALIASES.get(normalized);
        if (technology == null) {
            throw new IllegalArgumentException("Unrecognized modern technology: " + label);
        }
        return technology;
    }

}
