package com.ailegacy.modernization.copilot.domain.enums;

import java.util.Locale;
import java.util.Map;

/**
 * Standard security severity levels.
 */
public enum Severity {

    LOW,
    MEDIUM,
    HIGH,
    CRITICAL;

    private static final Map<String, Severity> ALIASES = Map.ofEntries(
            Map.entry("LOW", LOW),
            Map.entry("MEDIUM", MEDIUM),
            Map.entry("MED", MEDIUM),
            Map.entry("MODERATE", MEDIUM),
            Map.entry("HIGH", HIGH),
            Map.entry("CRITICAL", CRITICAL),
            Map.entry("SEVERE", CRITICAL)
    );

    /**
     * Normalizes a free-form LLM label, defaulting to {@link #MEDIUM} for
     * anything unrecognized so a single oddly-worded finding doesn't fail the
     * whole analysis.
     */
    public static Severity fromLabel(String label) {
        if (label == null) {
            return MEDIUM;
        }
        String normalized = label.trim().toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9]+", "_");
        return ALIASES.getOrDefault(normalized, MEDIUM);
    }

}
