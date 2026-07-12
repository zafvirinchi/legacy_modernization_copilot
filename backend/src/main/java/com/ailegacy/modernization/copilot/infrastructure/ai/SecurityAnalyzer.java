package com.ailegacy.modernization.copilot.infrastructure.ai;

import com.ailegacy.modernization.copilot.domain.entities.SecurityAnalysisReport;
import com.ailegacy.modernization.copilot.domain.entities.SecurityFinding;
import com.ailegacy.modernization.copilot.domain.enums.SecurityIssueType;
import com.ailegacy.modernization.copilot.domain.enums.Severity;
import com.ailegacy.modernization.copilot.domain.exceptions.BusinessLogicException;
import com.ailegacy.modernization.copilot.domain.exceptions.ValidationException;
import com.ailegacy.modernization.copilot.infrastructure.analysis.ProjectFileScanner;
import com.ailegacy.modernization.copilot.infrastructure.analysis.model.ScannedFile;
import com.ailegacy.modernization.copilot.infrastructure.ai.model.CodeDigest;
import com.ailegacy.modernization.copilot.infrastructure.ai.model.LlmSecurityAnalysisPayload;
import com.ailegacy.modernization.copilot.infrastructure.ai.model.LlmSecurityFinding;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.model.chat.ChatLanguageModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Security Analyzer agent: finds concrete security vulnerabilities in an
 * uploaded project and recommends modern Spring Security-based fixes.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityAnalyzer {

    private static final Pattern JSON_BLOCK_PATTERN = Pattern.compile("\\{.*}", Pattern.DOTALL);

    private final ChatLanguageModel chatLanguageModel;
    private final ProjectFileScanner fileScanner;
    private final CodeDigestBuilder digestBuilder;
    private final SecurityAnalyzerPromptBuilder promptBuilder;
    private final ObjectMapper objectMapper;

    public SecurityAnalysisReport analyze(String projectId, String projectName, String storagePath,
                                           List<String> knownTechnologies) {
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
            log.error("LLM call failed during security analysis | projectId={}", projectId, ex);
            throw new BusinessLogicException("Failed to analyze security: " + ex.getMessage(), "AI_ANALYSIS_FAILED", ex);
        }

        SecurityAnalysisReport report = buildReport(projectId, digest, rawResponse);

        log.info("Security analysis completed | projectId={} | findings={} | overallRiskScore={}",
                projectId, report.getFindings().size(), report.getOverallRiskScore());
        return report;
    }

    private SecurityAnalysisReport buildReport(String projectId, CodeDigest digest, String rawResponse) {
        try {
            LlmSecurityAnalysisPayload payload = objectMapper.readValue(extractJson(rawResponse), LlmSecurityAnalysisPayload.class);
            List<SecurityFinding> findings = toFindings(payload.getFindings());

            return SecurityAnalysisReport.builder()
                    .projectId(projectId)
                    .findings(findings)
                    .overallRiskScore(computeOverallRiskScore(findings))
                    .filesAnalyzed(digest.filesIncluded())
                    .totalProjectFiles(digest.totalFiles())
                    .build();
        } catch (Exception ex) {
            log.error("Failed to parse AI response as valid security analysis | response={}", rawResponse, ex);
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

    private List<SecurityFinding> toFindings(List<LlmSecurityFinding> source) {
        if (source == null) {
            return List.of();
        }
        return source.stream()
                .map(f -> SecurityFinding.builder()
                        .issueType(SecurityIssueType.fromLabel(f.getIssueType()))
                        .title(f.getTitle())
                        .description(f.getDescription())
                        .severity(Severity.fromLabel(f.getSeverity()))
                        .riskScore(clampScore(f.getRiskScore()))
                        .location(f.getLocation())
                        .recommendation(f.getRecommendation())
                        .modernAlternative(f.getModernAlternative())
                        .evidence(f.getEvidence() == null ? List.of() : f.getEvidence())
                        .build())
                .toList();
    }

    /**
     * The overall project risk is driven by its worst finding, consistent with
     * standard security posture practice - a single critical hole isn't offset
     * by ten low-severity ones.
     */
    private int computeOverallRiskScore(List<SecurityFinding> findings) {
        return findings.stream().mapToInt(SecurityFinding::getRiskScore).max().orElse(0);
    }

    private int clampScore(Integer score) {
        if (score == null) {
            return 0;
        }
        return Math.max(0, Math.min(100, score));
    }

}
