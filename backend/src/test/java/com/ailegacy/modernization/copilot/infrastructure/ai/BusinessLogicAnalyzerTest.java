package com.ailegacy.modernization.copilot.infrastructure.ai;

import com.ailegacy.modernization.copilot.domain.entities.BusinessAnalysisReport;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BusinessLogicAnalyzerTest {

    private static final String CLEAN_JSON = """
            {
              "businessPurpose": "Manage customer billing and invoicing.",
              "executiveSummary": "This system automates billing for customers.",
              "businessSummary": "It tracks customers and generates invoices for their orders.",
              "mainModules": ["Billing", "Customer Management"],
              "criticalWorkflows": ["Customer places an order and is billed automatically"],
              "coreEntities": ["Customer", "Invoice"],
              "moduleSummary": [
                {"moduleName": "Billing", "description": "Generates and tracks invoices."},
                {"moduleName": "Customer Management", "description": "Stores customer records."}
              ]
            }
            """;

    @TempDir
    Path projectDir;

    private BusinessLogicAnalyzer newAnalyzer(dev.langchain4j.model.chat.ChatLanguageModel model) {
        CodeDigestBuilder digestBuilder = new CodeDigestBuilder();
        ReflectionTestUtils.setField(digestBuilder, "maxDigestChars", 60_000);
        ReflectionTestUtils.setField(digestBuilder, "maxFileChars", 6_000);

        return new BusinessLogicAnalyzer(
                new FixedObjectProvider<>(model),
                new ProjectFileScanner(),
                digestBuilder,
                new BusinessAnalyzerPromptBuilder(),
                new ObjectMapper()
        );
    }

    @Test
    void throwsWhenNoLlmIsConfigured() throws IOException {
        writeFile("App.java", "public class App {}");
        BusinessLogicAnalyzer analyzer = newAnalyzer(null);

        assertThatThrownBy(() -> analyzer.analyze("project-1", "Demo", projectDir.toString()))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("disabled");
    }

    @Test
    void throwsWhenProjectHasNoFiles() {
        BusinessLogicAnalyzer analyzer = newAnalyzer(FakeChatLanguageModel.returning(CLEAN_JSON));

        assertThatThrownBy(() -> analyzer.analyze("project-2", "Demo", projectDir.toString()))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void parsesCleanJsonIntoReport() throws IOException {
        writeFile("Customer.java", "@Entity\npublic class Customer {}");
        BusinessLogicAnalyzer analyzer = newAnalyzer(FakeChatLanguageModel.returning(CLEAN_JSON));

        BusinessAnalysisReport report = analyzer.analyze("project-3", "Demo", projectDir.toString());

        assertThat(report.getProjectId()).isEqualTo("project-3");
        assertThat(report.getBusinessPurpose()).isEqualTo("Manage customer billing and invoicing.");
        assertThat(report.getMainModules()).containsExactly("Billing", "Customer Management");
        assertThat(report.getCriticalWorkflows()).containsExactly("Customer places an order and is billed automatically");
        assertThat(report.getCoreEntities()).containsExactly("Customer", "Invoice");
        assertThat(report.getModuleSummary()).hasSize(2);
        assertThat(report.getModuleSummary().get(0).getModuleName()).isEqualTo("Billing");
        assertThat(report.getFilesAnalyzed()).isEqualTo(1);
        assertThat(report.getTotalProjectFiles()).isEqualTo(1);
    }

    @Test
    void parsesJsonWrappedInMarkdownCodeFences() throws IOException {
        writeFile("Customer.java", "@Entity\npublic class Customer {}");
        String fenced = "```json\n" + CLEAN_JSON + "\n```";
        BusinessLogicAnalyzer analyzer = newAnalyzer(FakeChatLanguageModel.returning(fenced));

        BusinessAnalysisReport report = analyzer.analyze("project-4", "Demo", projectDir.toString());

        assertThat(report.getBusinessPurpose()).isEqualTo("Manage customer billing and invoicing.");
    }

    @Test
    void throwsClearErrorWhenLlmResponseIsNotValidJson() throws IOException {
        writeFile("Customer.java", "@Entity\npublic class Customer {}");
        BusinessLogicAnalyzer analyzer = newAnalyzer(FakeChatLanguageModel.returning("Sorry, I cannot help with that."));

        assertThatThrownBy(() -> analyzer.analyze("project-5", "Demo", projectDir.toString()))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("parsed");
    }

    @Test
    void throwsClearErrorWhenLlmCallFails() throws IOException {
        writeFile("Customer.java", "@Entity\npublic class Customer {}");
        BusinessLogicAnalyzer analyzer = newAnalyzer(FakeChatLanguageModel.throwing(new RuntimeException("rate limited")));

        assertThatThrownBy(() -> analyzer.analyze("project-6", "Demo", projectDir.toString()))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("rate limited");
    }

    private void writeFile(String relativePath, String content) throws IOException {
        Path target = projectDir.resolve(relativePath);
        Files.createDirectories(target.getParent() == null ? projectDir : target.getParent());
        Files.writeString(target, content);
    }

}
