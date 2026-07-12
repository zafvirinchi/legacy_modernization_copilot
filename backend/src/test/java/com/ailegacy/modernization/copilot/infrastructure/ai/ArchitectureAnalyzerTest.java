package com.ailegacy.modernization.copilot.infrastructure.ai;

import com.ailegacy.modernization.copilot.domain.entities.ArchitectureAnalysisReport;
import com.ailegacy.modernization.copilot.domain.enums.ArchitecturePattern;
import com.ailegacy.modernization.copilot.domain.exceptions.BusinessLogicException;
import com.ailegacy.modernization.copilot.domain.exceptions.ValidationException;
import com.ailegacy.modernization.copilot.infrastructure.analysis.ProjectFileScanner;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ArchitectureAnalyzerTest {

    private static final String CLEAN_JSON = """
            {
              "detectedPattern": "MONOLITH",
              "currentArchitectureDescription": "A single deployable application handling all concerns.",
              "currentArchitectureDiagram": "flowchart TD\\nUI-->Controller-->Service-->DB",
              "architectureScore": 45,
              "architectureScoreJustification": "Tight coupling between layers limits testability.",
              "recommendations": ["Introduce a service layer", "Extract the billing module"],
              "targetArchitecturePattern": "LAYERED",
              "targetArchitectureDescription": "Separate presentation, business and data layers.",
              "migrationDiagram": "flowchart TD\\nMonolith-->Layered"
            }
            """;

    @TempDir
    Path projectDir;

    private ArchitectureAnalyzer newAnalyzer(dev.langchain4j.model.chat.ChatLanguageModel model) {
        CodeDigestBuilder digestBuilder = new CodeDigestBuilder();
        ReflectionTestUtils.setField(digestBuilder, "maxDigestChars", 60_000);
        ReflectionTestUtils.setField(digestBuilder, "maxFileChars", 6_000);

        return new ArchitectureAnalyzer(
                model,
                new ProjectFileScanner(),
                digestBuilder,
                new ArchitectureAnalyzerPromptBuilder(),
                new ObjectMapper()
        );
    }

    @Test
    void throwsWhenNoLlmIsConfigured() throws IOException {
        writeFile("App.java", "public class App {}");
        ArchitectureAnalyzer analyzer = newAnalyzer(null);

        assertThatThrownBy(() -> analyzer.analyze("project-1", "Demo", projectDir.toString(), List.of(), null))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("disabled");
    }

    @Test
    void throwsWhenProjectHasNoFiles() {
        ArchitectureAnalyzer analyzer = newAnalyzer(FakeChatLanguageModel.returning(CLEAN_JSON));

        assertThatThrownBy(() -> analyzer.analyze("project-2", "Demo", projectDir.toString(), List.of(), null))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void parsesCleanJsonIntoReport() throws IOException {
        writeFile("App.java", "public class App {}");
        ArchitectureAnalyzer analyzer = newAnalyzer(FakeChatLanguageModel.returning(CLEAN_JSON));

        ArchitectureAnalysisReport report = analyzer.analyze(
                "project-3", "Demo", projectDir.toString(), List.of("SERVLET", "JDBC"), "Manages billing.");

        assertThat(report.getProjectId()).isEqualTo("project-3");
        assertThat(report.getDetectedPattern()).isEqualTo(ArchitecturePattern.MONOLITH);
        assertThat(report.getTargetArchitecturePattern()).isEqualTo(ArchitecturePattern.LAYERED);
        assertThat(report.getArchitectureScore()).isEqualTo(45);
        assertThat(report.getRecommendations()).containsExactly("Introduce a service layer", "Extract the billing module");
        assertThat(report.getCurrentArchitectureDiagram()).contains("flowchart TD");
        assertThat(report.getMigrationDiagram()).contains("Monolith-->Layered");
        assertThat(report.getFilesAnalyzed()).isEqualTo(1);
    }

    @Test
    void stripsMarkdownFencesEmbeddedInMermaidDiagramStrings() throws IOException {
        writeFile("App.java", "public class App {}");
        String jsonWithFencedDiagram = CLEAN_JSON.replace(
                "\"currentArchitectureDiagram\": \"flowchart TD\\nUI-->Controller-->Service-->DB\"",
                "\"currentArchitectureDiagram\": \"```mermaid\\nflowchart TD\\nUI-->Controller\\n```\""
        );
        ArchitectureAnalyzer analyzer = newAnalyzer(FakeChatLanguageModel.returning(jsonWithFencedDiagram));

        ArchitectureAnalysisReport report = analyzer.analyze("project-4", "Demo", projectDir.toString(), List.of(), null);

        assertThat(report.getCurrentArchitectureDiagram()).doesNotContain("```");
        assertThat(report.getCurrentArchitectureDiagram()).contains("flowchart TD");
    }

    @Test
    void throwsClearErrorWhenPatternLabelIsUnrecognized() throws IOException {
        writeFile("App.java", "public class App {}");
        String invalidPattern = CLEAN_JSON.replace("\"MONOLITH\"", "\"SPAGHETTI\"");
        ArchitectureAnalyzer analyzer = newAnalyzer(FakeChatLanguageModel.returning(invalidPattern));

        assertThatThrownBy(() -> analyzer.analyze("project-5", "Demo", projectDir.toString(), List.of(), null))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("parsed");
    }

    @Test
    void throwsClearErrorWhenLlmCallFails() throws IOException {
        writeFile("App.java", "public class App {}");
        ArchitectureAnalyzer analyzer = newAnalyzer(FakeChatLanguageModel.throwing(new RuntimeException("rate limited")));

        assertThatThrownBy(() -> analyzer.analyze("project-6", "Demo", projectDir.toString(), List.of(), null))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("rate limited");
    }

    private void writeFile(String relativePath, String content) throws IOException {
        Path target = projectDir.resolve(relativePath);
        Files.createDirectories(target.getParent() == null ? projectDir : target.getParent());
        Files.writeString(target, content);
    }

}
