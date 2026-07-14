package com.ailegacy.modernization.copilot.infrastructure.ai;

import com.ailegacy.modernization.copilot.domain.entities.PerformanceAnalysisReport;
import com.ailegacy.modernization.copilot.domain.enums.PerformanceIssueType;
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

class PerformanceAnalyzerTest {

    private static final String MULTI_FINDING_JSON = """
            {
              "performanceScore": 55,
              "performanceScoreJustification": "Several N+1 queries and a god object drag down maintainability.",
              "findings": [
                {
                  "issueType": "N_PLUS_ONE_QUERY",
                  "title": "N+1 query loading order line items",
                  "description": "Each order's line items are fetched in a separate query inside a loop.",
                  "location": "OrderService.java",
                  "optimizationSuggestion": "Fetch line items eagerly with a join or batch query.",
                  "modernAlternative": "Use Spring Data JPA's @EntityGraph or a JOIN FETCH query.",
                  "evidence": ["for (Order o : orders) { o.getLineItems(); }"]
                },
                {
                  "issueType": "GOD_OBJECT",
                  "title": "OrderManager does everything",
                  "description": "Handles orders, payments, shipping, and notifications in one class.",
                  "location": "OrderManager.java",
                  "optimizationSuggestion": "Split into OrderService, PaymentService, ShippingService.",
                  "modernAlternative": "Apply single-responsibility Spring services per concern.",
                  "evidence": ["class OrderManager { ... 40 methods ... }"]
                }
              ]
            }
            """;

    @TempDir
    Path projectDir;

    private PerformanceAnalyzer newAnalyzer(dev.langchain4j.model.chat.ChatLanguageModel model) {
        CodeDigestBuilder digestBuilder = new CodeDigestBuilder();
        ReflectionTestUtils.setField(digestBuilder, "maxDigestChars", 60_000);
        ReflectionTestUtils.setField(digestBuilder, "maxFileChars", 6_000);

        return new PerformanceAnalyzer(
                new FixedObjectProvider<>(model),
                new ProjectFileScanner(),
                digestBuilder,
                new PerformanceAnalyzerPromptBuilder(),
                new ObjectMapper()
        );
    }

    @Test
    void throwsWhenNoLlmIsConfigured() throws IOException {
        writeFile("App.java", "public class App {}");
        PerformanceAnalyzer analyzer = newAnalyzer(null);

        assertThatThrownBy(() -> analyzer.analyze("project-1", "Demo", projectDir.toString(), List.of()))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("disabled");
    }

    @Test
    void throwsWhenProjectHasNoFiles() {
        PerformanceAnalyzer analyzer = newAnalyzer(FakeChatLanguageModel.returning(MULTI_FINDING_JSON));

        assertThatThrownBy(() -> analyzer.analyze("project-2", "Demo", projectDir.toString(), List.of()))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void parsesMultipleFindingsAndHolisticScore() throws IOException {
        writeFile("OrderService.java", "class OrderService {}");
        PerformanceAnalyzer analyzer = newAnalyzer(FakeChatLanguageModel.returning(MULTI_FINDING_JSON));

        PerformanceAnalysisReport report = analyzer.analyze("project-3", "Demo", projectDir.toString(), List.of("JDBC"));

        assertThat(report.getPerformanceScore()).isEqualTo(55);
        assertThat(report.getPerformanceScoreJustification()).contains("N+1");
        assertThat(report.getFindings()).hasSize(2);
        assertThat(report.getFindings().get(0).getIssueType()).isEqualTo(PerformanceIssueType.N_PLUS_ONE_QUERY);
        assertThat(report.getFindings().get(1).getIssueType()).isEqualTo(PerformanceIssueType.GOD_OBJECT);
        assertThat(report.getFilesAnalyzed()).isEqualTo(1);
    }

    @Test
    void returnsEmptyReportWhenNoIssuesFound() throws IOException {
        writeFile("App.java", "public class App {}");
        String cleanJson = "{ \"performanceScore\": 100, \"performanceScoreJustification\": \"No issues found.\", \"findings\": [] }";
        PerformanceAnalyzer analyzer = newAnalyzer(FakeChatLanguageModel.returning(cleanJson));

        PerformanceAnalysisReport report = analyzer.analyze("project-4", "Demo", projectDir.toString(), List.of());

        assertThat(report.getFindings()).isEmpty();
        assertThat(report.getPerformanceScore()).isEqualTo(100);
    }

    @Test
    void throwsClearErrorWhenPatternLabelIsUnrecognized() throws IOException {
        writeFile("App.java", "public class App {}");
        String invalidType = MULTI_FINDING_JSON.replace("\"N_PLUS_ONE_QUERY\"", "\"SPAGHETTI_CODE\"");
        PerformanceAnalyzer analyzer = newAnalyzer(FakeChatLanguageModel.returning(invalidType));

        assertThatThrownBy(() -> analyzer.analyze("project-5", "Demo", projectDir.toString(), List.of()))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("parsed");
    }

    @Test
    void throwsClearErrorWhenLlmResponseIsNotValidJson() throws IOException {
        writeFile("App.java", "public class App {}");
        PerformanceAnalyzer analyzer = newAnalyzer(FakeChatLanguageModel.returning("I cannot help with that."));

        assertThatThrownBy(() -> analyzer.analyze("project-6", "Demo", projectDir.toString(), List.of()))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("parsed");
    }

    @Test
    void throwsClearErrorWhenLlmCallFails() throws IOException {
        writeFile("App.java", "public class App {}");
        PerformanceAnalyzer analyzer = newAnalyzer(FakeChatLanguageModel.throwing(new RuntimeException("rate limited")));

        assertThatThrownBy(() -> analyzer.analyze("project-7", "Demo", projectDir.toString(), List.of()))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("rate limited");
    }

    private void writeFile(String relativePath, String content) throws IOException {
        Path target = projectDir.resolve(relativePath);
        Files.createDirectories(target.getParent() == null ? projectDir : target.getParent());
        Files.writeString(target, content);
    }

}
