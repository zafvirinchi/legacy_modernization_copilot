package com.ailegacy.modernization.copilot.infrastructure.ai;

import com.ailegacy.modernization.copilot.domain.entities.GeneratedSpringBootCode;
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

class SpringBootGeneratorTest {

    /**
     * A realistic LLM response: the *Code fields hold full Java source (with
     * their own braces, quotes and newlines) properly escaped as JSON string
     * values. This is the main risk this generator faces that the other
     * analyzers don't, since their JSON values are plain prose.
     */
    private static final String JSON_WITH_EMBEDDED_JAVA_CODE = """
            {
              "sourceServletReference": "com.legacy.UserServlet",
              "sourceJdbcReference": "com.legacy.UserDao",
              "entityCode": "package com.example;\\n\\nimport jakarta.persistence.*;\\n\\n@Entity\\npublic class User {\\n    @Id\\n    private Long id;\\n\\n    private String name = \\"default\\";\\n}",
              "repositoryCode": "package com.example;\\n\\nimport org.springframework.data.jpa.repository.JpaRepository;\\n\\npublic interface UserRepository extends JpaRepository<User, Long> {\\n}",
              "dtoCode": "package com.example;\\n\\npublic record UserDto(Long id, String name) {\\n}",
              "serviceCode": "package com.example;\\n\\nimport org.springframework.stereotype.Service;\\n\\n@Service\\npublic class UserService {\\n    private final UserRepository repository;\\n\\n    public UserService(UserRepository repository) {\\n        this.repository = repository;\\n    }\\n}",
              "controllerCode": "package com.example;\\n\\nimport org.springframework.web.bind.annotation.*;\\n\\n@RestController\\n@RequestMapping(\\"/users\\")\\npublic class UserController {\\n}",
              "explanation": "UserServlet's doGet method, which read a \\"user\\" query parameter and wrote raw HTML, was converted into a @GetMapping endpoint returning a UserDto as JSON."
            }
            """;

    @TempDir
    Path projectDir;

    private SpringBootGenerator newGenerator(dev.langchain4j.model.chat.ChatLanguageModel model) {
        CodeDigestBuilder digestBuilder = new CodeDigestBuilder();
        ReflectionTestUtils.setField(digestBuilder, "maxDigestChars", 60_000);
        ReflectionTestUtils.setField(digestBuilder, "maxFileChars", 6_000);

        return new SpringBootGenerator(
                model,
                new ProjectFileScanner(),
                digestBuilder,
                new SpringBootGeneratorPromptBuilder(),
                new ObjectMapper()
        );
    }

    @Test
    void throwsWhenNoLlmIsConfigured() throws IOException {
        writeFile("UserServlet.java", "public class UserServlet extends HttpServlet {}");
        SpringBootGenerator generator = newGenerator(null);

        assertThatThrownBy(() -> generator.generate("project-1", "Demo", projectDir.toString(), List.of()))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("disabled");
    }

    @Test
    void throwsWhenProjectHasNoFiles() {
        SpringBootGenerator generator = newGenerator(FakeChatLanguageModel.returning(JSON_WITH_EMBEDDED_JAVA_CODE));

        assertThatThrownBy(() -> generator.generate("project-2", "Demo", projectDir.toString(), List.of()))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void parsesJsonWhoseValuesContainFullJavaSourceWithBracesAndQuotes() throws IOException {
        writeFile("UserServlet.java", "public class UserServlet extends HttpServlet {}");
        SpringBootGenerator generator = newGenerator(FakeChatLanguageModel.returning(JSON_WITH_EMBEDDED_JAVA_CODE));

        GeneratedSpringBootCode result = generator.generate("project-3", "Demo", projectDir.toString(), List.of("SERVLET", "JDBC"));

        assertThat(result.getSourceServletReference()).isEqualTo("com.legacy.UserServlet");
        assertThat(result.getSourceJdbcReference()).isEqualTo("com.legacy.UserDao");
        assertThat(result.getEntityCode()).contains("@Entity").contains("public class User {").contains("private String name = \"default\";");
        assertThat(result.getRepositoryCode()).contains("extends JpaRepository<User, Long>");
        assertThat(result.getDtoCode()).contains("public record UserDto(Long id, String name)");
        assertThat(result.getServiceCode()).contains("@Service");
        assertThat(result.getControllerCode()).contains("@RestController").contains("@RequestMapping(\"/users\")");
        assertThat(result.getExplanation()).contains("UserServlet's doGet method");
        assertThat(result.getFilesAnalyzed()).isEqualTo(1);
    }

    @Test
    void throwsClearErrorWhenLlmResponseIsNotValidJson() throws IOException {
        writeFile("UserServlet.java", "public class UserServlet extends HttpServlet {}");
        SpringBootGenerator generator = newGenerator(FakeChatLanguageModel.returning("Here is your code: <no json>"));

        assertThatThrownBy(() -> generator.generate("project-4", "Demo", projectDir.toString(), List.of()))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("parsed");
    }

    @Test
    void throwsClearErrorWhenLlmCallFails() throws IOException {
        writeFile("UserServlet.java", "public class UserServlet extends HttpServlet {}");
        SpringBootGenerator generator = newGenerator(FakeChatLanguageModel.throwing(new RuntimeException("rate limited")));

        assertThatThrownBy(() -> generator.generate("project-5", "Demo", projectDir.toString(), List.of()))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("rate limited");
    }

    private void writeFile(String relativePath, String content) throws IOException {
        Path target = projectDir.resolve(relativePath);
        Files.createDirectories(target.getParent() == null ? projectDir : target.getParent());
        Files.writeString(target, content);
    }

}
