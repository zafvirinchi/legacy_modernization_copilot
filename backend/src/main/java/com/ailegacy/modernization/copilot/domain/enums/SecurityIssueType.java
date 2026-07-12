package com.ailegacy.modernization.copilot.domain.enums;

import java.util.Locale;
import java.util.Map;

/**
 * Categories of security issues the security analyzer looks for.
 * {@link #OWASP_ISSUE} is a deliberate catch-all for OWASP Top 10 concerns
 * that don't fit the more specific categories.
 */
public enum SecurityIssueType {

    SQL_INJECTION,
    HARDCODED_PASSWORD,
    WEAK_ENCRYPTION,
    MISSING_AUTHENTICATION,
    SESSION_RISK,
    OWASP_ISSUE;

    private static final Map<String, SecurityIssueType> ALIASES = Map.ofEntries(
            Map.entry("SQL_INJECTION", SQL_INJECTION),
            Map.entry("SQLI", SQL_INJECTION),
            Map.entry("HARDCODED_PASSWORD", HARDCODED_PASSWORD),
            Map.entry("HARDCODED_PASSWORDS", HARDCODED_PASSWORD),
            Map.entry("HARDCODED_CREDENTIALS", HARDCODED_PASSWORD),
            Map.entry("HARDCODED_SECRET", HARDCODED_PASSWORD),
            Map.entry("WEAK_ENCRYPTION", WEAK_ENCRYPTION),
            Map.entry("WEAK_CRYPTOGRAPHY", WEAK_ENCRYPTION),
            Map.entry("INSECURE_ENCRYPTION", WEAK_ENCRYPTION),
            Map.entry("MISSING_AUTHENTICATION", MISSING_AUTHENTICATION),
            Map.entry("MISSING_AUTH", MISSING_AUTHENTICATION),
            Map.entry("NO_AUTHENTICATION", MISSING_AUTHENTICATION),
            Map.entry("BROKEN_AUTHENTICATION", MISSING_AUTHENTICATION),
            Map.entry("SESSION_RISK", SESSION_RISK),
            Map.entry("SESSION_RISKS", SESSION_RISK),
            Map.entry("SESSION_MANAGEMENT", SESSION_RISK),
            Map.entry("INSECURE_SESSION", SESSION_RISK),
            Map.entry("OWASP_ISSUE", OWASP_ISSUE),
            Map.entry("OWASP", OWASP_ISSUE),
            Map.entry("OTHER", OWASP_ISSUE)
    );

    /**
     * Normalizes a free-form LLM label into one of the fixed categories,
     * falling back to {@link #OWASP_ISSUE} for anything unrecognized - that
     * category exists precisely to catch issues that don't fit elsewhere, so
     * an unrecognized label is not treated as an error here (unlike
     * {@link ArchitecturePattern#fromLabel}, which has no such catch-all).
     */
    public static SecurityIssueType fromLabel(String label) {
        if (label == null) {
            return OWASP_ISSUE;
        }
        String normalized = label.trim().toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9]+", "_");
        return ALIASES.getOrDefault(normalized, OWASP_ISSUE);
    }

}
