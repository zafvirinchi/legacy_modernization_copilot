package com.ailegacy.modernization.copilot.infrastructure.ai;

import com.ailegacy.modernization.copilot.infrastructure.ai.model.CodeDigest;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Builds the prompt sent to the LLM for architecture analysis, instructing it
 * to classify the project's architecture, score it, and propose a target
 * architecture with Mermaid diagrams, all as strict JSON.
 */
@Component
public class ArchitectureAnalyzerPromptBuilder {

    public String build(String projectName, CodeDigest digest, List<String> knownTechnologies, String businessContext) {
        String technologiesSection = knownTechnologies == null || knownTechnologies.isEmpty()
                ? ""
                : "\nTechnologies already detected in this project: " + String.join(", ", knownTechnologies) + "\n";

        String businessSection = (businessContext == null || businessContext.isBlank())
                ? ""
                : "\nBusiness context already known about this project: " + businessContext + "\n";

        return """
                You are an Architecture Analyzer inside a legacy application modernization tool.
                You will be given a sample of source files (Java, JSP, COBOL, JCL, XML configuration, SQL,
                properties/YAML) extracted from an uploaded legacy project named "%s". The sample includes
                %d of the project's %d files, prioritized to surface the most architecturally-relevant files
                first (entities, controllers, legacy jobs, configuration).
                %s%s
                Classify the project's current architecture as exactly ONE of these five patterns:
                MONOLITH, MVC, LAYERED, CLIENT_SERVER, MICROSERVICE.

                Then produce:
                - A description of the current architecture in plain English
                - A Mermaid diagram (flowchart syntax, e.g. "flowchart TD") showing the current architecture's
                  main components/layers and how they connect
                - An architecture quality score from 0 (poor) to 100 (excellent), considering maintainability,
                  separation of concerns, testability and coupling
                - A short justification for that score
                - A list of concrete recommendations to improve the architecture
                - A recommended target architecture pattern (one of the same five values)
                - A description of that target architecture in plain English
                - A Mermaid diagram (flowchart syntax) showing the migration path from the current architecture
                  to the target architecture

                Respond with ONLY a single valid JSON object (no markdown code fences, no commentary before or
                after it) with EXACTLY these keys:
                - "detectedPattern": one of MONOLITH, MVC, LAYERED, CLIENT_SERVER, MICROSERVICE
                - "currentArchitectureDescription": string
                - "currentArchitectureDiagram": string containing valid Mermaid flowchart syntax
                - "architectureScore": integer 0-100
                - "architectureScoreJustification": string
                - "recommendations": array of strings
                - "targetArchitecturePattern": one of MONOLITH, MVC, LAYERED, CLIENT_SERVER, MICROSERVICE
                - "targetArchitectureDescription": string
                - "migrationDiagram": string containing valid Mermaid flowchart syntax

                Project files:
                %s
                """.formatted(
                projectName, digest.filesIncluded(), digest.totalFiles(),
                technologiesSection, businessSection, digest.content()
        );
    }

}
