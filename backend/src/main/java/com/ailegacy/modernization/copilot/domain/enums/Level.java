package com.ailegacy.modernization.copilot.domain.enums;

import java.util.Locale;
import java.util.Map;

/**
 * A generic three-point rating shared by the modernization planner's
 * impact/effort/complexity/risk-severity ratings.
 */
public enum Level {

    LOW,
    MEDIUM,
    HIGH;

    private static final Map<String, Level> ALIASES = Map.ofEntries(
            Map.entry("LOW", LOW),
            Map.entry("MEDIUM", MEDIUM),
            Map.entry("MED", MEDIUM),
            Map.entry("MODERATE", MEDIUM),
            Map.entry("HIGH", HIGH)
    );

    /**
     * Normalizes a free-form LLM label, defaulting to {@link #MEDIUM} for
     * anything unrecognized so one oddly-worded rating doesn't fail the whole
     * plan.
     */
    public static Level fromLabel(String label) {
        if (label == null) {
            return MEDIUM;
        }
        String normalized = label.trim().toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9]+", "_");
        return ALIASES.getOrDefault(normalized, MEDIUM);
    }

}
