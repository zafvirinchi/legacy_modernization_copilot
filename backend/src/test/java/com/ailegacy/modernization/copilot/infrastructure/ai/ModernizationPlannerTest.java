package com.ailegacy.modernization.copilot.infrastructure.ai;

import com.ailegacy.modernization.copilot.domain.entities.ModernizationPlan;
import com.ailegacy.modernization.copilot.domain.enums.Level;
import com.ailegacy.modernization.copilot.domain.enums.ModernTechnology;
import com.ailegacy.modernization.copilot.domain.exceptions.BusinessLogicException;
import com.ailegacy.modernization.copilot.domain.exceptions.ValidationException;
import com.ailegacy.modernization.copilot.infrastructure.analysis.ProjectFileScanner;
import com.ailegacy.modernization.copilot.infrastructure.ai.model.ModernizationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ModernizationPlannerTest {

    private static final String FULL_JSON = """
            {
              "migrationStrategy": "Strangler fig: incrementally extract modules behind a facade.",
              "estimatedTimeline": "6-9 months across 4 phases",
              "migrationComplexity": "HIGH",
              "priorityMatrix": [
                { "item": "Externalize hardcoded credentials", "impact": "HIGH", "effort": "LOW" },
                { "item": "Migrate to Spring Boot", "impact": "HIGH", "effort": "HIGH" }
              ],
              "quickWins": ["Add health check endpoint", "Externalize config to environment variables"],
              "risks": [
                { "description": "Data migration downtime", "severity": "HIGH" },
                { "description": "Team unfamiliar with Kubernetes", "severity": "MEDIUM" }
              ],
              "requiredTechnologies": [
                { "technology": "SPRING_BOOT", "recommended": true, "reason": "Replaces the legacy servlet container setup." },
                { "technology": "SPRING_SECURITY", "recommended": true, "reason": "Replaces custom auth filters." },
                { "technology": "DOCKER", "recommended": true, "reason": "Enables consistent deployment." },
                { "technology": "KUBERNETES", "recommended": true, "reason": "Needed for horizontal scaling." },
                { "technology": "KAFKA", "recommended": false, "reason": "No async messaging need identified." },
                { "technology": "REDIS", "recommended": true, "reason": "Session caching." },
                { "technology": "OPENAPI", "recommended": true, "reason": "Document the REST endpoints." },
                { "technology": "CLOUD_MIGRATION", "recommended": true, "reason": "Reduce infrastructure overhead." }
              ]
            }
            """;

    @TempDir
    Path projectDir;

    private ModernizationPlanner newPlanner(dev.langchain4j.model.chat.ChatLanguageModel model) {
        CodeDigestBuilder digestBuilder = new CodeDigestBuilder();
        ReflectionTestUtils.setField(digestBuilder, "maxDigestChars", 60_000);
        ReflectionTestUtils.setField(digestBuilder, "maxFileChars", 6_000);

        return new ModernizationPlanner(
                model,
                new ProjectFileScanner(),
                digestBuilder,
                new ModernizationPlannerPromptBuilder(),
                new ObjectMapper()
        );
    }

    @Test
    void throwsWhenNoLlmIsConfigured() throws IOException {
        writeFile("App.java", "public class App {}");
        ModernizationPlanner planner = newPlanner(null);

        assertThatThrownBy(() -> planner.plan("project-1", "Demo", projectDir.toString(), ModernizationContext.empty()))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("disabled");
    }

    @Test
    void throwsWhenProjectHasNoFiles() {
        ModernizationPlanner planner = newPlanner(FakeChatLanguageModel.returning(FULL_JSON));

        assertThatThrownBy(() -> planner.plan("project-2", "Demo", projectDir.toString(), ModernizationContext.empty()))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void parsesFullPlanWithAllEightRequiredTechnologies() throws IOException {
        writeFile("App.java", "public class App {}");
        ModernizationPlanner planner = newPlanner(FakeChatLanguageModel.returning(FULL_JSON));
        ModernizationContext context = new ModernizationContext("Spring MVC, JDBC", null, null, null, null);

        ModernizationPlan plan = planner.plan("project-3", "Demo", projectDir.toString(), context);

        assertThat(plan.getMigrationComplexity()).isEqualTo(Level.HIGH);
        assertThat(plan.getPriorityMatrix()).hasSize(2);
        assertThat(plan.getQuickWins()).hasSize(2);
        assertThat(plan.getRisks()).hasSize(2);
        assertThat(plan.getRisks().get(0).getSeverity()).isEqualTo(Level.HIGH);
        assertThat(plan.getRequiredTechnologies()).hasSize(8);

        Set<ModernTechnology> covered = plan.getRequiredTechnologies().stream()
                .map(rt -> rt.getTechnology())
                .collect(Collectors.toSet());
        assertThat(covered).isEqualTo(EnumSet.allOf(ModernTechnology.class));
    }

    @Test
    void backfillsMissingRequiredTechnologiesAsNotRecommended() throws IOException {
        writeFile("App.java", "public class App {}");
        // Drop the KAFKA entry entirely to simulate the LLM omitting one.
        String missingKafka = FULL_JSON.replaceAll(
                ",\\s*\\{ \"technology\": \"KAFKA\".*?\\}", "");
        ModernizationPlanner planner = newPlanner(FakeChatLanguageModel.returning(missingKafka));

        ModernizationPlan plan = planner.plan("project-4", "Demo", projectDir.toString(), ModernizationContext.empty());

        assertThat(plan.getRequiredTechnologies()).hasSize(8);
        var kafkaEntry = plan.getRequiredTechnologies().stream()
                .filter(rt -> rt.getTechnology() == ModernTechnology.KAFKA)
                .findFirst()
                .orElseThrow();
        assertThat(kafkaEntry.isRecommended()).isFalse();
        assertThat(kafkaEntry.getReason()).isEqualTo("Not assessed by the analysis");
    }

    @Test
    void throwsClearErrorWhenLlmResponseIsNotValidJson() throws IOException {
        writeFile("App.java", "public class App {}");
        ModernizationPlanner planner = newPlanner(FakeChatLanguageModel.returning("Not a plan."));

        assertThatThrownBy(() -> planner.plan("project-5", "Demo", projectDir.toString(), ModernizationContext.empty()))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("parsed");
    }

    @Test
    void throwsClearErrorWhenLlmCallFails() throws IOException {
        writeFile("App.java", "public class App {}");
        ModernizationPlanner planner = newPlanner(FakeChatLanguageModel.throwing(new RuntimeException("rate limited")));

        assertThatThrownBy(() -> planner.plan("project-6", "Demo", projectDir.toString(), ModernizationContext.empty()))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("rate limited");
    }

    private void writeFile(String relativePath, String content) throws IOException {
        Path target = projectDir.resolve(relativePath);
        Files.createDirectories(target.getParent() == null ? projectDir : target.getParent());
        Files.writeString(target, content);
    }

}
