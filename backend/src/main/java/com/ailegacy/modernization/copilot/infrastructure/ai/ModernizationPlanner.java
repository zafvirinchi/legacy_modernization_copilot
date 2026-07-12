package com.ailegacy.modernization.copilot.infrastructure.ai;

import com.ailegacy.modernization.copilot.domain.entities.ModernizationPlan;
import com.ailegacy.modernization.copilot.domain.entities.PriorityMatrixItem;
import com.ailegacy.modernization.copilot.domain.entities.RequiredTechnology;
import com.ailegacy.modernization.copilot.domain.entities.Risk;
import com.ailegacy.modernization.copilot.domain.enums.Level;
import com.ailegacy.modernization.copilot.domain.enums.ModernTechnology;
import com.ailegacy.modernization.copilot.domain.exceptions.BusinessLogicException;
import com.ailegacy.modernization.copilot.domain.exceptions.ValidationException;
import com.ailegacy.modernization.copilot.infrastructure.analysis.ProjectFileScanner;
import com.ailegacy.modernization.copilot.infrastructure.analysis.model.ScannedFile;
import com.ailegacy.modernization.copilot.infrastructure.ai.model.CodeDigest;
import com.ailegacy.modernization.copilot.infrastructure.ai.model.LlmModernizationPlanPayload;
import com.ailegacy.modernization.copilot.infrastructure.ai.model.LlmPriorityMatrixItem;
import com.ailegacy.modernization.copilot.infrastructure.ai.model.LlmRequiredTechnology;
import com.ailegacy.modernization.copilot.infrastructure.ai.model.LlmRisk;
import com.ailegacy.modernization.copilot.infrastructure.ai.model.ModernizationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.model.chat.ChatLanguageModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Modernization Planner agent: synthesizes a concrete migration roadmap from
 * a project's code sample plus whatever prior analyses are available.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ModernizationPlanner {

    private static final Pattern JSON_BLOCK_PATTERN = Pattern.compile("\\{.*}", Pattern.DOTALL);

    private final ChatLanguageModel chatLanguageModel;
    private final ProjectFileScanner fileScanner;
    private final CodeDigestBuilder digestBuilder;
    private final ModernizationPlannerPromptBuilder promptBuilder;
    private final ObjectMapper objectMapper;

    public ModernizationPlan plan(String projectId, String projectName, String storagePath, ModernizationContext context) {
        if (chatLanguageModel == null) {
            throw new BusinessLogicException(
                    "AI features are disabled because no LLM API key is configured", "AI_DISABLED");
        }

        List<ScannedFile> files = fileScanner.scan(storagePath);
        if (files.isEmpty()) {
            throw new ValidationException("No files were found to analyze for this project");
        }

        CodeDigest digest = digestBuilder.build(files);
        String prompt = promptBuilder.build(projectName, digest, context);

        String rawResponse;
        try {
            rawResponse = chatLanguageModel.generate(prompt);
        } catch (Exception ex) {
            log.error("LLM call failed during modernization planning | projectId={}", projectId, ex);
            throw new BusinessLogicException("Failed to generate modernization plan: " + ex.getMessage(), "AI_ANALYSIS_FAILED", ex);
        }

        ModernizationPlan plan = buildPlan(projectId, digest, rawResponse);

        log.info("Modernization plan generated | projectId={} | complexity={} | risks={}",
                projectId, plan.getMigrationComplexity(), plan.getRisks().size());
        return plan;
    }

    private ModernizationPlan buildPlan(String projectId, CodeDigest digest, String rawResponse) {
        try {
            LlmModernizationPlanPayload payload = objectMapper.readValue(extractJson(rawResponse), LlmModernizationPlanPayload.class);

            return ModernizationPlan.builder()
                    .projectId(projectId)
                    .migrationStrategy(payload.getMigrationStrategy())
                    .estimatedTimeline(payload.getEstimatedTimeline())
                    .migrationComplexity(Level.fromLabel(payload.getMigrationComplexity()))
                    .priorityMatrix(toPriorityMatrix(payload.getPriorityMatrix()))
                    .quickWins(payload.getQuickWins() == null ? List.of() : payload.getQuickWins())
                    .risks(toRisks(payload.getRisks()))
                    .requiredTechnologies(toRequiredTechnologies(payload.getRequiredTechnologies()))
                    .filesAnalyzed(digest.filesIncluded())
                    .totalProjectFiles(digest.totalFiles())
                    .build();
        } catch (Exception ex) {
            log.error("Failed to parse AI response as valid modernization plan | response={}", rawResponse, ex);
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

    private List<PriorityMatrixItem> toPriorityMatrix(List<LlmPriorityMatrixItem> source) {
        if (source == null) {
            return List.of();
        }
        return source.stream()
                .map(i -> PriorityMatrixItem.builder()
                        .item(i.getItem())
                        .impact(Level.fromLabel(i.getImpact()))
                        .effort(Level.fromLabel(i.getEffort()))
                        .build())
                .toList();
    }

    private List<Risk> toRisks(List<LlmRisk> source) {
        if (source == null) {
            return List.of();
        }
        return source.stream()
                .map(r -> Risk.builder()
                        .description(r.getDescription())
                        .severity(Level.fromLabel(r.getSeverity()))
                        .build())
                .toList();
    }

    /**
     * Guarantees the plan always covers all 8 checklist technologies, even if
     * the LLM omits one or mislabels it - missing/unrecognized entries are
     * backfilled as not recommended rather than failing the whole plan.
     */
    private List<RequiredTechnology> toRequiredTechnologies(List<LlmRequiredTechnology> source) {
        Map<ModernTechnology, RequiredTechnology> byTechnology = new EnumMap<>(ModernTechnology.class);
        if (source != null) {
            for (LlmRequiredTechnology item : source) {
                try {
                    ModernTechnology technology = ModernTechnology.fromLabel(item.getTechnology());
                    byTechnology.put(technology, RequiredTechnology.builder()
                            .technology(technology)
                            .recommended(Boolean.TRUE.equals(item.getRecommended()))
                            .reason(item.getReason())
                            .build());
                } catch (IllegalArgumentException ex) {
                    log.warn("Skipping unrecognized required technology label from LLM: {}", item.getTechnology());
                }
            }
        }

        List<RequiredTechnology> result = new ArrayList<>();
        for (ModernTechnology technology : ModernTechnology.values()) {
            result.add(byTechnology.getOrDefault(technology, RequiredTechnology.builder()
                    .technology(technology)
                    .recommended(false)
                    .reason("Not assessed by the analysis")
                    .build()));
        }
        return result;
    }

}
