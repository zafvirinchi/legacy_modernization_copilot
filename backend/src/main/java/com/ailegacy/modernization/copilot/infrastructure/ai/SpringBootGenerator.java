package com.ailegacy.modernization.copilot.infrastructure.ai;

import com.ailegacy.modernization.copilot.domain.entities.GeneratedSpringBootCode;
import com.ailegacy.modernization.copilot.domain.exceptions.BusinessLogicException;
import com.ailegacy.modernization.copilot.domain.exceptions.ValidationException;
import com.ailegacy.modernization.copilot.infrastructure.analysis.ProjectFileScanner;
import com.ailegacy.modernization.copilot.infrastructure.analysis.model.ScannedFile;
import com.ailegacy.modernization.copilot.infrastructure.ai.model.CodeDigest;
import com.ailegacy.modernization.copilot.infrastructure.ai.model.LlmGeneratedCodePayload;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.model.chat.ChatLanguageModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Spring Boot Generator agent: produces one representative modern Spring Boot
 * example (Entity/Repository/DTO/Service/Controller) converting a sample
 * Servlet to a @RestController and a sample JDBC class to Spring Data JPA.
 *
 * Deliberately generates a single representative example, not a full project
 * conversion.
 */
@Slf4j
@Component
public class SpringBootGenerator {

    private static final Pattern JSON_BLOCK_PATTERN = Pattern.compile("\\{.*}", Pattern.DOTALL);

    private final ObjectProvider<ChatLanguageModel> chatLanguageModelProvider;
    private final ProjectFileScanner fileScanner;
    private final CodeDigestBuilder digestBuilder;
    private final SpringBootGeneratorPromptBuilder promptBuilder;
    private final ObjectMapper objectMapper;

    /**
     * Depends on an {@code ObjectProvider} rather than a direct {@code ChatLanguageModel}
     * dependency so the underlying OpenAI client (see {@link LangChain4jConfig}) is never
     * constructed during application startup - only when {@link #generate} is actually
     * called. A direct/eager dependency here would force bean creation during
     * {@code ApplicationContext} refresh, and if that construction fails (e.g. the
     * OpenAI host is unreachable), it would take down the whole application before
     * the web server ever starts.
     */
    public SpringBootGenerator(
            ObjectProvider<ChatLanguageModel> chatLanguageModelProvider,
            ProjectFileScanner fileScanner,
            CodeDigestBuilder digestBuilder,
            SpringBootGeneratorPromptBuilder promptBuilder,
            ObjectMapper objectMapper) {
        this.chatLanguageModelProvider = chatLanguageModelProvider;
        this.fileScanner = fileScanner;
        this.digestBuilder = digestBuilder;
        this.promptBuilder = promptBuilder;
        this.objectMapper = objectMapper;
    }

    public GeneratedSpringBootCode generate(String projectId, String projectName, String storagePath,
                                             List<String> knownTechnologies) {
        ChatLanguageModel chatLanguageModel = chatLanguageModelProvider.getIfAvailable();
        if (chatLanguageModel == null) {
            throw new BusinessLogicException(
                    "AI features are disabled because no LLM API key is configured", "AI_DISABLED");
        }

        List<ScannedFile> files = fileScanner.scan(storagePath);
        if (files.isEmpty()) {
            throw new ValidationException("No files were found to generate a representative example from");
        }

        CodeDigest digest = digestBuilder.build(files);
        String prompt = promptBuilder.build(projectName, digest, knownTechnologies);

        String rawResponse;
        try {
            rawResponse = chatLanguageModel.generate(prompt);
        } catch (Exception ex) {
            log.error("LLM call failed during Spring Boot code generation | projectId={}", projectId, ex);
            throw new BusinessLogicException("Failed to generate Spring Boot code: " + ex.getMessage(), "AI_ANALYSIS_FAILED", ex);
        }

        GeneratedSpringBootCode result = buildResult(projectId, digest, rawResponse);

        log.info("Spring Boot code generation completed | projectId={}", projectId);
        return result;
    }

    private GeneratedSpringBootCode buildResult(String projectId, CodeDigest digest, String rawResponse) {
        try {
            LlmGeneratedCodePayload payload = objectMapper.readValue(extractJson(rawResponse), LlmGeneratedCodePayload.class);

            return GeneratedSpringBootCode.builder()
                    .projectId(projectId)
                    .sourceServletReference(payload.getSourceServletReference())
                    .sourceJdbcReference(payload.getSourceJdbcReference())
                    .entityCode(payload.getEntityCode())
                    .repositoryCode(payload.getRepositoryCode())
                    .dtoCode(payload.getDtoCode())
                    .serviceCode(payload.getServiceCode())
                    .controllerCode(payload.getControllerCode())
                    .explanation(payload.getExplanation())
                    .filesAnalyzed(digest.filesIncluded())
                    .totalProjectFiles(digest.totalFiles())
                    .build();
        } catch (Exception ex) {
            log.error("Failed to parse AI response as valid generated code | response={}", rawResponse, ex);
            throw new BusinessLogicException(
                    "AI response could not be parsed as valid analysis output", "AI_RESPONSE_INVALID", ex);
        }
    }

    /**
     * LLMs are prone to wrapping JSON in markdown code fences despite instructions
     * not to, so pull out the first {...} block rather than trusting the raw text.
     * This still works when the JSON string values themselves contain Java source
     * (with its own braces), since the outer match is greedy and only the
     * outermost { and } of the whole response matter here.
     */
    private String extractJson(String rawResponse) {
        Matcher matcher = JSON_BLOCK_PATTERN.matcher(rawResponse);
        return matcher.find() ? matcher.group() : rawResponse;
    }

}
