package com.ailegacy.modernization.copilot.domain.enums;

import java.util.Locale;
import java.util.Map;

/**
 * Categories of performance/code-quality issues the performance analyzer
 * looks for.
 */
public enum PerformanceIssueType {

    N_PLUS_ONE_QUERY,
    LARGE_CLASS,
    GOD_OBJECT,
    MEMORY_LEAK_RISK,
    DUPLICATE_CODE,
    BLOCKING_IO;

    private static final Map<String, PerformanceIssueType> ALIASES = Map.ofEntries(
            Map.entry("N_PLUS_ONE_QUERY", N_PLUS_ONE_QUERY),
            Map.entry("N_PLUS_ONE_QUERIES", N_PLUS_ONE_QUERY),
            Map.entry("N_PLUS_ONE", N_PLUS_ONE_QUERY),
            Map.entry("N_1_QUERY", N_PLUS_ONE_QUERY),
            Map.entry("N_1_QUERIES", N_PLUS_ONE_QUERY),
            Map.entry("N_1", N_PLUS_ONE_QUERY),
            Map.entry("LARGE_CLASS", LARGE_CLASS),
            Map.entry("LARGE_CLASSES", LARGE_CLASS),
            Map.entry("BLOATED_CLASS", LARGE_CLASS),
            Map.entry("GOD_OBJECT", GOD_OBJECT),
            Map.entry("GOD_OBJECTS", GOD_OBJECT),
            Map.entry("GOD_CLASS", GOD_OBJECT),
            Map.entry("MEMORY_LEAK_RISK", MEMORY_LEAK_RISK),
            Map.entry("MEMORY_LEAK_RISKS", MEMORY_LEAK_RISK),
            Map.entry("MEMORY_LEAK", MEMORY_LEAK_RISK),
            Map.entry("MEMORY_LEAKS", MEMORY_LEAK_RISK),
            Map.entry("DUPLICATE_CODE", DUPLICATE_CODE),
            Map.entry("DUPLICATED_CODE", DUPLICATE_CODE),
            Map.entry("CODE_DUPLICATION", DUPLICATE_CODE),
            Map.entry("DUPLICATE_LOGIC", DUPLICATE_CODE),
            Map.entry("BLOCKING_IO", BLOCKING_IO),
            Map.entry("BLOCKING_I_O", BLOCKING_IO),
            Map.entry("SYNCHRONOUS_IO", BLOCKING_IO),
            Map.entry("BLOCKING_CALLS", BLOCKING_IO)
    );

    /**
     * Normalizes a free-form LLM label (e.g. "N+1 Queries", "God Class") into
     * one of the fixed categories.
     */
    public static PerformanceIssueType fromLabel(String label) {
        if (label == null) {
            throw new IllegalArgumentException("Performance issue type label is missing");
        }
        String normalized = label.trim().toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9]+", "_");
        PerformanceIssueType type = ALIASES.get(normalized);
        if (type == null) {
            throw new IllegalArgumentException("Unrecognized performance issue type: " + label);
        }
        return type;
    }

}
