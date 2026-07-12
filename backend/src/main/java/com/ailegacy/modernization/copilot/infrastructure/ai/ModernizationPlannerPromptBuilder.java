package com.ailegacy.modernization.copilot.infrastructure.ai;

import com.ailegacy.modernization.copilot.infrastructure.ai.model.CodeDigest;
import com.ailegacy.modernization.copilot.infrastructure.ai.model.ModernizationContext;
import org.springframework.stereotype.Component;

/**
 * Builds the prompt sent to the LLM for modernization planning, instructing
 * it to synthesize a migration roadmap from a code sample plus whatever
 * prior analyses are available, all as strict JSON.
 */
@Component
public class ModernizationPlannerPromptBuilder {

    public String build(String projectName, CodeDigest digest, ModernizationContext context) {
        StringBuilder priorAnalysis = new StringBuilder();
        appendIfPresent(priorAnalysis, "Technology detection findings", context.technologySummary());
        appendIfPresent(priorAnalysis, "Business analysis", context.businessSummary());
        appendIfPresent(priorAnalysis, "Architecture analysis", context.architectureSummary());
        appendIfPresent(priorAnalysis, "Security analysis", context.securitySummary());
        appendIfPresent(priorAnalysis, "Performance analysis", context.performanceSummary());

        String priorAnalysisSection = priorAnalysis.isEmpty()
                ? "\nNo prior analysis reports are available for this project yet - base the plan on the code sample alone.\n"
                : "\nPrior analysis already completed for this project:\n" + priorAnalysis;

        return """
                You are a Modernization Planner inside a legacy application modernization tool. You synthesize
                everything known about a legacy project into a concrete, actionable migration roadmap targeting
                a modern Spring Boot / cloud-native stack.

                You will be given a sample of source files extracted from an uploaded legacy project named "%s".
                The sample includes %d of the project's %d files.
                %s
                Produce a modernization roadmap with:
                - "migrationStrategy": a plain-English description of the recommended overall approach (e.g.
                  strangler fig, phased rewrite, big bang, lift-and-shift then refactor) and why it fits this project
                - "estimatedTimeline": a realistic timeline estimate as free text (e.g. "6-9 months across 4 phases")
                - "migrationComplexity": one of LOW, MEDIUM, HIGH for the overall migration
                - "priorityMatrix": an array of { "item": string, "impact": LOW|MEDIUM|HIGH, "effort": LOW|MEDIUM|HIGH },
                  ordered from highest to lowest priority
                - "quickWins": array of short, low-effort improvements that could be done immediately
                - "risks": array of { "description": string, "severity": LOW|MEDIUM|HIGH }
                - "requiredTechnologies": an array with EXACTLY these 8 entries, each as
                  { "technology": <token>, "recommended": true|false, "reason": string } - assess every one
                  of them for this specific project, do not omit any:
                  SPRING_BOOT, SPRING_SECURITY, DOCKER, KUBERNETES, KAFKA, REDIS, OPENAPI, CLOUD_MIGRATION

                Respond with ONLY a single valid JSON object (no markdown code fences, no commentary before or
                after it) with EXACTLY these top-level keys: migrationStrategy, estimatedTimeline,
                migrationComplexity, priorityMatrix, quickWins, risks, requiredTechnologies.

                Project files:
                %s
                """.formatted(
                projectName, digest.filesIncluded(), digest.totalFiles(), priorAnalysisSection, digest.content()
        );
    }

    private void appendIfPresent(StringBuilder builder, String label, String value) {
        if (value != null && !value.isBlank()) {
            builder.append("- ").append(label).append(": ").append(value).append("\n");
        }
    }

}
