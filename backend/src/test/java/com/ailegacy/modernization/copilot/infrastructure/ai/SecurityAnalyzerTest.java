package com.ailegacy.modernization.copilot.infrastructure.ai;

import com.ailegacy.modernization.copilot.domain.entities.SecurityAnalysisReport;
import com.ailegacy.modernization.copilot.domain.enums.SecurityIssueType;
import com.ailegacy.modernization.copilot.domain.enums.Severity;
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

class SecurityAnalyzerTest {

    private static final String MULTI_FINDING_JSON = """
            {
              "findings": [
                {
                  "issueType": "SQL_INJECTION",
                  "title": "Unparameterized query in CustomerDao",
                  "description": "Query built via string concatenation of user input.",
                  "severity": "CRITICAL",
                  "riskScore": 90,
                  "location": "CustomerDao.java",
                  "recommendation": "Use PreparedStatement with bound parameters.",
                  "modernAlternative": "Use Spring Data JPA repositories or JdbcTemplate with named parameters.",
                  "evidence": ["\\"SELECT * FROM customers WHERE id = \\" + id"]
                },
                {
                  "issueType": "HARDCODED_PASSWORD",
                  "title": "Hardcoded database password",
                  "description": "DB password is a string literal in source.",
                  "severity": "HIGH",
                  "riskScore": 70,
                  "location": "DataSourceConfig.java",
                  "recommendation": "Externalize credentials to environment variables or a secrets manager.",
                  "modernAlternative": "Use Spring Boot's externalized configuration with encrypted properties.",
                  "evidence": ["String password = \\"admin123\\";"]
                }
              ]
            }
            """;

    @TempDir
    Path projectDir;

    private SecurityAnalyzer newAnalyzer(dev.langchain4j.model.chat.ChatLanguageModel model) {
        CodeDigestBuilder digestBuilder = new CodeDigestBuilder();
        ReflectionTestUtils.setField(digestBuilder, "maxDigestChars", 60_000);
        ReflectionTestUtils.setField(digestBuilder, "maxFileChars", 6_000);

        return new SecurityAnalyzer(
                model,
                new ProjectFileScanner(),
                digestBuilder,
                new SecurityAnalyzerPromptBuilder(),
                new ObjectMapper()
        );
    }

    @Test
    void throwsWhenNoLlmIsConfigured() throws IOException {
        writeFile("App.java", "public class App {}");
        SecurityAnalyzer analyzer = newAnalyzer(null);

        assertThatThrownBy(() -> analyzer.analyze("project-1", "Demo", projectDir.toString(), List.of()))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("disabled");
    }

    @Test
    void throwsWhenProjectHasNoFiles() {
        SecurityAnalyzer analyzer = newAnalyzer(FakeChatLanguageModel.returning(MULTI_FINDING_JSON));

        assertThatThrownBy(() -> analyzer.analyze("project-2", "Demo", projectDir.toString(), List.of()))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void parsesMultipleFindingsAndComputesOverallRiskAsTheMax() throws IOException {
        writeFile("CustomerDao.java", "class CustomerDao {}");
        SecurityAnalyzer analyzer = newAnalyzer(FakeChatLanguageModel.returning(MULTI_FINDING_JSON));

        SecurityAnalysisReport report = analyzer.analyze("project-3", "Demo", projectDir.toString(), List.of("JDBC"));

        assertThat(report.getFindings()).hasSize(2);
        assertThat(report.getFindings().get(0).getIssueType()).isEqualTo(SecurityIssueType.SQL_INJECTION);
        assertThat(report.getFindings().get(0).getSeverity()).isEqualTo(Severity.CRITICAL);
        assertThat(report.getFindings().get(1).getIssueType()).isEqualTo(SecurityIssueType.HARDCODED_PASSWORD);
        assertThat(report.getOverallRiskScore()).isEqualTo(90);
        assertThat(report.getFilesAnalyzed()).isEqualTo(1);
    }

    @Test
    void returnsEmptyReportWhenNoIssuesFound() throws IOException {
        writeFile("App.java", "public class App {}");
        SecurityAnalyzer analyzer = newAnalyzer(FakeChatLanguageModel.returning("{ \"findings\": [] }"));

        SecurityAnalysisReport report = analyzer.analyze("project-4", "Demo", projectDir.toString(), List.of());

        assertThat(report.getFindings()).isEmpty();
        assertThat(report.getOverallRiskScore()).isZero();
    }

    @Test
    void normalizesUnrecognizedIssueTypeToOwaspIssueRatherThanFailing() throws IOException {
        writeFile("App.java", "public class App {}");
        String withUnknownType = MULTI_FINDING_JSON.replace("\"SQL_INJECTION\"", "\"Insecure Deserialization\"");
        SecurityAnalyzer analyzer = newAnalyzer(FakeChatLanguageModel.returning(withUnknownType));

        SecurityAnalysisReport report = analyzer.analyze("project-5", "Demo", projectDir.toString(), List.of());

        assertThat(report.getFindings().get(0).getIssueType()).isEqualTo(SecurityIssueType.OWASP_ISSUE);
    }

    @Test
    void throwsClearErrorWhenLlmResponseIsNotValidJson() throws IOException {
        writeFile("App.java", "public class App {}");
        SecurityAnalyzer analyzer = newAnalyzer(FakeChatLanguageModel.returning("I refuse to answer."));

        assertThatThrownBy(() -> analyzer.analyze("project-6", "Demo", projectDir.toString(), List.of()))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("parsed");
    }

    @Test
    void throwsClearErrorWhenLlmCallFails() throws IOException {
        writeFile("App.java", "public class App {}");
        SecurityAnalyzer analyzer = newAnalyzer(FakeChatLanguageModel.throwing(new RuntimeException("rate limited")));

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
