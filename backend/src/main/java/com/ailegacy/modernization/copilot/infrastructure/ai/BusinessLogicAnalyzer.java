package com.ailegacy.modernization.copilot.infrastructure.ai;

import com.ailegacy.modernization.copilot.domain.entities.BusinessAnalysisReport;
import com.ailegacy.modernization.copilot.domain.entities.ModuleSummary;
import com.ailegacy.modernization.copilot.domain.exceptions.BusinessLogicException;
import com.ailegacy.modernization.copilot.domain.exceptions.ValidationException;
import com.ailegacy.modernization.copilot.infrastructure.analysis.ProjectFileScanner;
import com.ailegacy.modernization.copilot.infrastructure.analysis.model.ScannedFile;
import com.ailegacy.modernization.copilot.infrastructure.ai.model.CodeDigest;
import com.ailegacy.modernization.copilot.infrastructure.ai.model.LlmBusinessAnalysisPayload;
import com.ailegacy.modernization.copilot.infrastructure.ai.model.LlmModuleSummary;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.model.chat.ChatLanguageModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Business Logic Analyzer agent: explains an uploaded project's business
 * purpose, modules, workflows and entities in plain English using the
 * configured LLM, and returns a report ready to be persisted.
 */
@Slf4j
@Component
public class BusinessLogicAnalyzer {

    private static final Pattern JSON_BLOCK_PATTERN = Pattern.compile("\\{.*}", Pattern.DOTALL);

    private final ChatLanguageModel chatLanguageModel;
    private final ProjectFileScanner fileScanner;
    private final CodeDigestBuilder digestBuilder;
    private final BusinessAnalyzerPromptBuilder promptBuilder;
    private final ObjectMapper objectMapper;

    /**
     * {@code chatLanguageModel} is {@code @Nullable} because {@link LangChain4jConfig}
     * returns a null bean when no OpenAI API key is configured - Spring must treat
     * this constructor parameter as optional, or the whole context fails to start
     * whenever the key is absent (see {@code AI_DISABLED} handling in {@link #analyze}).
     */
    public BusinessLogicAnalyzer(
            @Nullable ChatLanguageModel chatLanguageModel,
            ProjectFileScanner fileScanner,
            CodeDigestBuilder digestBuilder,
            BusinessAnalyzerPromptBuilder promptBuilder,
            ObjectMapper objectMapper) {
        this.chatLanguageModel = chatLanguageModel;
        this.fileScanner = fileScanner;
        this.digestBuilder = digestBuilder;
        this.promptBuilder = promptBuilder;
        this.objectMapper = objectMapper;
    }

    public BusinessAnalysisReport analyze(String projectId, String projectName, String storagePath) {
        if (chatLanguageModel == null) {
            throw new BusinessLogicException(
                    "AI features are disabled because no LLM API key is configured", "AI_DISABLED");
        }

        List<ScannedFile> files = fileScanner.scan(storagePath);
        if (files.isEmpty()) {
            throw new ValidationException("No files were found to analyze for this project");
        }

        CodeDigest digest = digestBuilder.build(files);
        String prompt = promptBuilder.build(projectName, digest);

        String rawResponse;
        try {
            rawResponse = chatLanguageModel.generate(prompt);
        } catch (Exception ex) {
            log.error("LLM call failed during business analysis | projectId={}", projectId, ex);
            throw new BusinessLogicException("Failed to analyze project: " + ex.getMessage(), "AI_ANALYSIS_FAILED", ex);
        }

        LlmBusinessAnalysisPayload payload = parse(rawResponse);

        BusinessAnalysisReport report = BusinessAnalysisReport.builder()
                .projectId(projectId)
                .businessPurpose(payload.getBusinessPurpose())
                .mainModules(nullToEmpty(payload.getMainModules()))
                .criticalWorkflows(nullToEmpty(payload.getCriticalWorkflows()))
                .coreEntities(nullToEmpty(payload.getCoreEntities()))
                .executiveSummary(payload.getExecutiveSummary())
                .businessSummary(payload.getBusinessSummary())
                .moduleSummary(toModuleSummaries(payload.getModuleSummary()))
                .filesAnalyzed(digest.filesIncluded())
                .totalProjectFiles(digest.totalFiles())
                .build();

        log.info("Business analysis completed | projectId={} | filesAnalyzed={}/{}",
                projectId, digest.filesIncluded(), digest.totalFiles());
        return report;
    }

    private LlmBusinessAnalysisPayload parse(String rawResponse) {
        String jsonText = extractJson(rawResponse);
        try {
            return objectMapper.readValue(jsonText, LlmBusinessAnalysisPayload.class);
        } catch (JsonProcessingException ex) {
            log.error("Failed to parse AI response as JSON | response={}", rawResponse, ex);
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

    private List<String> nullToEmpty(List<String> list) {
        return list == null ? List.of() : list;
    }

    private List<ModuleSummary> toModuleSummaries(List<LlmModuleSummary> source) {
        if (source == null) {
            return List.of();
        }
        return source.stream()
                .map(m -> ModuleSummary.builder()
                        .moduleName(m.getModuleName())
                        .description(m.getDescription())
                        .build())
                .toList();
    }

}
