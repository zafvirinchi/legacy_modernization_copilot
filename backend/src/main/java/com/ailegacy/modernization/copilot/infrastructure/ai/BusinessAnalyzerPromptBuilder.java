package com.ailegacy.modernization.copilot.infrastructure.ai;

import com.ailegacy.modernization.copilot.infrastructure.ai.model.CodeDigest;
import org.springframework.stereotype.Component;

/**
 * Builds the prompt sent to the LLM for business logic analysis, instructing
 * it to explain the project in plain English and respond with strict JSON so
 * the response can be parsed deterministically.
 */
@Component
public class BusinessAnalyzerPromptBuilder {

    public String build(String projectName, CodeDigest digest) {
        return """
                You are a Business Logic Analyzer inside a legacy application modernization tool.
                You will be given a sample of source files (Java, JSP, COBOL, JCL, XML configuration, SQL,
                properties/YAML) extracted from an uploaded legacy project named "%s". The sample includes
                %d of the project's %d files, prioritized to surface the most business-relevant files first
                (entities, controllers, legacy jobs, configuration).

                Explain what this system does in simple, non-technical English, as if to someone with no
                software background.

                Respond with ONLY a single valid JSON object (no markdown code fences, no commentary before
                or after it) with EXACTLY these keys:
                - "businessPurpose": one sentence describing what the system is for
                - "executiveSummary": 2-4 sentences, high-level, for a non-technical executive
                - "businessSummary": a fuller plain-English explanation (2-4 paragraphs) of what the system
                  does and why, in simple English
                - "mainModules": array of short module/subsystem names identified in the code
                - "criticalWorkflows": array of short descriptions of the most important business workflows
                - "coreEntities": array of the core business entities/data objects the system revolves around
                - "moduleSummary": array of objects {"moduleName": string, "description": string}, one per
                  entry in mainModules, explaining what that module does in plain English

                Project files:
                %s
                """.formatted(projectName, digest.filesIncluded(), digest.totalFiles(), digest.content());
    }

}
