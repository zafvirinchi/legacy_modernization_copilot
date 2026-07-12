package com.ailegacy.modernization.copilot.infrastructure.ai;

import com.ailegacy.modernization.copilot.domain.entities.ArchitectureAnalysisReport;
import com.ailegacy.modernization.copilot.domain.enums.ArchitecturePattern;
import com.ailegacy.modernization.copilot.domain.exceptions.BusinessLogicException;
import com.ailegacy.modernization.copilot.domain.exceptions.ValidationException;
import com.ailegacy.modernization.copilot.infrastructure.analysis.ProjectFileScanner;
import com.ailegacy.modernization.copilot.infrastructure.analysis.model.ScannedFile;
import com.ailegacy.modernization.copilot.infrastructure.ai.model.CodeDigest;
import com.ailegacy.modernization.copilot.infrastructure.ai.model.LlmArchitectureAnalysisPayload;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.model.chat.ChatLanguageModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Architecture Analyzer agent: classifies an uploaded project's architecture,
 * scores it, generates a Mermaid diagram of the current and target
 * architectures, and returns a report ready to be persisted.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ArchitectureAnalyzer {

    private static final Pattern JSON_BLOCK_PATTERN = Pattern.compile("\\{.*}", Pattern.DOTALL);
    private static final Pattern MERMAID_FENCE_PATTERN = Pattern.compile(
            "^```(?:mermaid)?\\s*|```\\s*$", Pattern.MULTILINE);

    private final ChatLanguageModel chatLanguageModel;
    private final ProjectFileScanner fileScanner;
    private final CodeDigestBuilder digestBuilder;
    private final ArchitectureAnalyzerPromptBuilder promptBuilder;
    private final ObjectMapper objectMapper;

    public ArchitectureAnalysisReport analyze(String projectId, String projectName, String storagePath,
                                               List<String> knownTechnologies, String businessContext) {
        if (chatLanguageModel == null) {
            throw new BusinessLogicException(
                    "AI features are disabled because no LLM API key is configured", "AI_DISABLED");
        }

        List<ScannedFile> files = fileScanner.scan(storagePath);
        if (files.isEmpty()) {
            throw new ValidationException("No files were found to analyze for this project");
        }

        CodeDigest digest = digestBuilder.build(files);
        String prompt = promptBuilder.build(projectName, digest, knownTechnologies, businessContext);

        String rawResponse;
        try {
            rawResponse = chatLanguageModel.generate(prompt);
        } catch (Exception ex) {
            log.error("LLM call failed during architecture analysis | projectId={}", projectId, ex);
            throw new BusinessLogicException("Failed to analyze architecture: " + ex.getMessage(), "AI_ANALYSIS_FAILED", ex);
        }

        ArchitectureAnalysisReport report = buildReport(projectId, digest, rawResponse);

        log.info("Architecture analysis completed | projectId={} | pattern={} | score={}",
                projectId, report.getDetectedPattern(), report.getArchitectureScore());
        return report;
    }

    private ArchitectureAnalysisReport buildReport(String projectId, CodeDigest digest, String rawResponse) {
        try {
            LlmArchitectureAnalysisPayload payload = objectMapper.readValue(extractJson(rawResponse), LlmArchitectureAnalysisPayload.class);

            return ArchitectureAnalysisReport.builder()
                    .projectId(projectId)
                    .detectedPattern(ArchitecturePattern.fromLabel(payload.getDetectedPattern()))
                    .currentArchitectureDescription(payload.getCurrentArchitectureDescription())
                    .currentArchitectureDiagram(cleanMermaid(payload.getCurrentArchitectureDiagram()))
                    .architectureScore(clampScore(payload.getArchitectureScore()))
                    .architectureScoreJustification(payload.getArchitectureScoreJustification())
                    .recommendations(nullToEmpty(payload.getRecommendations()))
                    .targetArchitecturePattern(ArchitecturePattern.fromLabel(payload.getTargetArchitecturePattern()))
                    .targetArchitectureDescription(payload.getTargetArchitectureDescription())
                    .migrationDiagram(cleanMermaid(payload.getMigrationDiagram()))
                    .filesAnalyzed(digest.filesIncluded())
                    .totalProjectFiles(digest.totalFiles())
                    .build();
        } catch (Exception ex) {
            log.error("Failed to parse AI response as valid architecture analysis | response={}", rawResponse, ex);
            throw new BusinessLogicException(
                    "AI response could not be parsed as valid analysis output", "AI_RESPONSE_INVALID", ex);
        }
    }

    /**
     * LLMs are prone to wrapping JSON in markdown code fences despite instructions
     * not to, so pull out the first {...} block rather than trusting the raw text.
     */
    private String extractJson(String rawResponse) {
        Matcher matcher = JSON_BLOCK_PATTERN.matcher(rawResponse);
        return matcher.find() ? matcher.group() : rawResponse;
    }

    private String cleanMermaid(String diagram) {
        if (diagram == null) {
            return "";
        }
        return MERMAID_FENCE_PATTERN.matcher(diagram.trim()).replaceAll("").trim();
    }

    private int clampScore(Integer score) {
        if (score == null) {
            return 0;
        }
        return Math.max(0, Math.min(100, score));
    }

    private List<String> nullToEmpty(List<String> list) {
        return list == null ? List.of() : list;
    }

}
