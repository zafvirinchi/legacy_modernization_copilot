package com.ailegacy.modernization.copilot.infrastructure.ai.model;

/**
 * Pre-formatted summaries of any prior analyses available for a project. Each
 * field is null when that analysis hasn't been run yet - the modernization
 * planner works with whatever subset is available, or none at all.
 */
public record ModernizationContext(
        String technologySummary,
        String businessSummary,
        String architectureSummary,
        String securitySummary,
        String performanceSummary
) {

    public static ModernizationContext empty() {
        return new ModernizationContext(null, null, null, null, null);
    }

}
