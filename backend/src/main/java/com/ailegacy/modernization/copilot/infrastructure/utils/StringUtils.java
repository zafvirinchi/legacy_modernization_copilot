package com.ailegacy.modernization.copilot.infrastructure.utils;

import org.springframework.stereotype.Component;

/**
 * Utility for common string and object transformation operations.
 * 
 * Used across the application for:
 * - ID generation
 * - Slug creation
 * - Case conversions
 * - String validations
 */
@Component
public class StringUtils {

    /**
     * Generate a unique identifier
     */
    public static String generateId() {
        return java.util.UUID.randomUUID().toString();
    }

    /**
     * Create a slug from a string
     */
    public static String toSlug(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "";
        }
        return input.toLowerCase()
                .trim()
                .replaceAll("[^a-z0-9-]", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }

    /**
     * Convert snake_case to camelCase
     */
    public static String snakeToCamel(String input) {
        String[] parts = input.split("_");
        StringBuilder sb = new StringBuilder(parts[0]);
        for (int i = 1; i < parts.length; i++) {
            sb.append(parts[i].substring(0, 1).toUpperCase())
                    .append(parts[i].substring(1));
        }
        return sb.toString();
    }

    /**
     * Truncate string to specified length
     */
    public static String truncate(String input, int maxLength) {
        if (input != null && input.length() > maxLength) {
            return input.substring(0, maxLength) + "...";
        }
        return input;
    }

}
