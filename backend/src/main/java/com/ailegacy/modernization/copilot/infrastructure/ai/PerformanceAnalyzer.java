package com.ailegacy.modernization.copilot.infrastructure.ai;

import com.ailegacy.modernization.copilot.domain.entities.PerformanceAnalysisReport;
import com.ailegacy.modernization.copilot.domain.entities.PerformanceFinding;
import com.ailegacy.modernization.copilot.domain.enums.PerformanceIssueType;
import com.ailegacy.modernization.copilot.domain.exceptions.BusinessLogicException;
import com.ailegacy.modernization.copilot.domain.exceptions.ValidationException;
import com.ailegacy.modernization.copilot.infrastructure.analysis.ProjectFileScanner;
import com.ailegacy.modernization.copilot.infrastructure.analysis.model.ScannedFile;
import com.ailegacy.modernization.copilot.infrastructure.ai.model.CodeDigest;
import com.ailegacy.modernization.copilot.infrastructure.ai.model.LlmPerformanceAnalysisPayload;
import com.ailegacy.modernization.copilot.infrastructure.ai.model.LlmPerformanceFinding;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.model.chat.ChatLanguageModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Performance Analyzer agent: finds concrete performance and code-quality
 * issues in an uploaded project and recommends modern alternatives.
 */
@Slf4j
@Component
public class PerformanceAnalyzer {

    private static final Pattern JSON_BLOCK_PATTERN = Pattern.compile("\\{.*}", Pattern.DOTALL);

    private final ObjectProvider<ChatLanguageModel> chatLanguageModelProvider;
    private final ProjectFileScanner fileScanner;
    private final CodeDigestBuilder digestBuilder;
    private final PerformanceAnalyzerPromptBuilder promptBuilder;
    private final ObjectMapper objectMapper;

    /**
     * Depends on an {@code ObjectProvider} rather than a direct {@code ChatLanguageModel}
     * dependency so the underlying OpenAI client (see {@link LangChain4jConfig}) is never
     * constructed during application startup - only when {@link #analyze} is actually
     * called. A direct/eager dependency here would force bean creation during
     * {@code ApplicationContext} refresh, and if that construction fails (e.g. the
     * OpenAI host is unreachable), it would take down the whole application before
     * the web server ever starts.
     */
    public PerformanceAnalyzer(
            ObjectProvider<ChatLanguageModel> chatLanguageModelProvider,
            ProjectFileScanner fileScanner,
            CodeDigestBuilder digestBuilder,
            PerformanceAnalyzerPromptBuilder promptBuilder,
            ObjectMapper objectMapper) {
        this.chatLanguageModelProvider = chatLanguageModelProvider;
        this.fileScanner = fileScanner;
        this.digestBuilder = digestBuilder;
        this.promptBuilder = promptBuilder;
        this.objectMapper = objectMapper;
    }

    public PerformanceAnalysisReport analyze(String projectId, String projectName, String storagePath,
                                              List<String> knownTechnologies) {
        ChatLanguageModel chatLanguageModel = chatLanguageModelProvider.getIfAvailable();
        if (chatLanguageModel == null) {
            throw new BusinessLogicException(
                    "AI features are disabled because no LLM API key is configured", "AI_DISABLED");
        }

        List<ScannedFile> files = fileScanner.scan(storagePath);
        if (files.isEmpty()) {
            throw new ValidationException("No files were found to analyze for this project");
        }

        CodeDigest digest = digestBuilder.build(files);
        String prompt = promptBuilder.build(projectName, digest, knownTechnologies);

        String rawResponse;
        try {
            rawResponse = chatLanguageModel.generate(prompt);
        } catch (Exception ex) {
            log.error("LLM call failed during performance analysis | projectId={}", projectId, ex);
            throw new BusinessLogicException("Failed to analyze performance: " + ex.getMessage(), "AI_ANALYSIS_FAILED", ex);
        }

        PerformanceAnalysisReport report = buildReport(projectId, digest, rawResponse);

        log.info("Performance analysis completed | projectId={} | findings={} | performanceScore={}",
                projectId, report.getFindings().size(), report.getPerformanceScore());
        return report;
    }

    private PerformanceAnalysisReport buildReport(String projectId, CodeDigest digest, String rawResponse) {
        try {
            LlmPerformanceAnalysisPayload payload = objectMapper.readValue(extractJson(rawResponse), LlmPerformanceAnalysisPayload.class);

            return PerformanceAnalysisReport.builder()
                    .projectId(projectId)
                    .performanceScore(clampScore(payload.getPerformanceScore()))
                    .performanceScoreJustification(payload.getPerformanceScoreJustification())
                    .findings(toFindings(payload.getFindings()))
                    .filesAnalyzed(digest.filesIncluded())
                    .totalProjectFiles(digest.totalFiles())
                    .build();
        } catch (Exception ex) {
            log.error("Failed to parse AI response as valid performance analysis | response={}", rawResponse, ex);
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

    private List<PerformanceFinding> toFindings(List<LlmPerformanceFinding> source) {
        if (source == null) {
            return List.of();
        }
        return source.stream()
                .map(f -> PerformanceFinding.builder()
                        .issueType(PerformanceIssueType.fromLabel(f.getIssueType()))
                        .title(f.getTitle())
                        .description(f.getDescription())
                        .location(f.getLocation())
                        .optimizationSuggestion(f.getOptimizationSuggestion())
                        .modernAlternative(f.getModernAlternative())
                        .evidence(f.getEvidence() == null ? List.of() : f.getEvidence())
                        .build())
                .toList();
    }

    private int clampScore(Integer score) {
        if (score == null) {
            return 0;
        }
        return Math.max(0, Math.min(100, score));
    }

}
