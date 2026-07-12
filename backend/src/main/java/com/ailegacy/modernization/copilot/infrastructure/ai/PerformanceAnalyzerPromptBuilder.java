package com.ailegacy.modernization.copilot.infrastructure.ai;

import com.ailegacy.modernization.copilot.infrastructure.ai.model.CodeDigest;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Builds the prompt sent to the LLM for performance analysis, instructing it
 * to find concrete performance/code-quality issues and recommend modern
 * alternatives, all as strict JSON.
 */
@Component
public class PerformanceAnalyzerPromptBuilder {

    public String build(String projectName, CodeDigest digest, List<String> knownTechnologies) {
        String technologiesSection = knownTechnologies == null || knownTechnologies.isEmpty()
                ? ""
                : "\nTechnologies already detected in this project: " + String.join(", ", knownTechnologies) + "\n";

        return """
                You are a Performance Analyzer inside a legacy application modernization tool. You specialize in
                finding performance and code-quality issues in legacy Java/JSP/COBOL/JCL/XML/SQL codebases and
                recommending modern alternatives.

                You will be given a sample of source files extracted from an uploaded legacy project named "%s".
                The sample includes %d of the project's %d files, prioritized to surface the most relevant
                files first (entities, controllers, legacy jobs, configuration).
                %s
                Look specifically for evidence of:
                - N+1 Queries: loading a collection then querying the database again for each element
                - Large Classes: classes with an excessive number of fields/methods/responsibilities
                - God Objects: a single class that knows about or controls too much of the system
                - Memory Leak Risks: unclosed resources, unbounded caches/collections, static collections
                  that keep growing, listeners never deregistered
                - Duplicate Code: near-identical logic repeated across multiple files/methods
                - Blocking IO: synchronous network/file/database calls on paths that should be non-blocking
                  or asynchronous

                Only report issues you find actual evidence for in the provided files - do not invent findings.
                If nothing relevant is found, return an empty findings array.

                For each finding, provide:
                - "issueType": one of N_PLUS_ONE_QUERY, LARGE_CLASS, GOD_OBJECT, MEMORY_LEAK_RISK,
                  DUPLICATE_CODE, BLOCKING_IO
                - "title": a short summary of the specific issue
                - "description": what the problem is and why it hurts performance or maintainability
                - "location": the file (and method if evident) where this was found
                - "optimizationSuggestion": concrete steps to fix or mitigate it
                - "modernAlternative": a specific modern library, pattern or Spring feature to use instead
                - "evidence": array of short strings quoting or referencing the offending code

                Then provide one holistic assessment of the project's overall performance and code quality:
                - "performanceScore": integer 0-100, where 100 means excellent, well-optimized code with no
                  significant issues, and 0 means severe, pervasive performance problems
                - "performanceScoreJustification": a short explanation for that score

                Respond with ONLY a single valid JSON object (no markdown code fences, no commentary before or
                after it) with EXACTLY this shape:
                { "performanceScore": <int>, "performanceScoreJustification": "...", "findings": [ { ... }, ... ] }

                Project files:
                %s
                """.formatted(
                projectName, digest.filesIncluded(), digest.totalFiles(), technologiesSection, digest.content()
        );
    }

}
