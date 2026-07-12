package com.ailegacy.modernization.copilot.infrastructure.ai;

import com.ailegacy.modernization.copilot.infrastructure.analysis.model.ScannedFile;
import com.ailegacy.modernization.copilot.infrastructure.ai.model.CodeDigest;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CodeDigestBuilderTest {

    private CodeDigestBuilder newBuilder(int maxDigestChars, int maxFileChars) {
        CodeDigestBuilder builder = new CodeDigestBuilder();
        ReflectionTestUtils.setField(builder, "maxDigestChars", maxDigestChars);
        ReflectionTestUtils.setField(builder, "maxFileChars", maxFileChars);
        return builder;
    }

    @Test
    void emptyFileListProducesEmptyDigest() {
        CodeDigest digest = newBuilder(60_000, 6_000).build(List.of());

        assertThat(digest.content()).isEmpty();
        assertThat(digest.filesIncluded()).isZero();
        assertThat(digest.totalFiles()).isZero();
    }

    @Test
    void prioritizesEntitiesAndControllersOverPlainFiles() {
        List<ScannedFile> files = List.of(
                new ScannedFile("util/Helper.java", "Helper.java", "java", "public class Helper {}"),
                new ScannedFile("model/Customer.java", "Customer.java", "java", "@Entity\npublic class Customer {}"),
                new ScannedFile("web/HomeController.java", "HomeController.java", "java", "@RestController\npublic class HomeController {}")
        );

        CodeDigest digest = newBuilder(60_000, 6_000).build(files);

        assertThat(digest.filesIncluded()).isEqualTo(3);
        int entityIndex = digest.content().indexOf("Customer.java");
        int controllerIndex = digest.content().indexOf("HomeController.java");
        int helperIndex = digest.content().indexOf("Helper.java");

        assertThat(entityIndex).isLessThan(controllerIndex);
        assertThat(controllerIndex).isLessThan(helperIndex);
    }

    @Test
    void stopsAddingFilesOnceBudgetIsExhausted() {
        List<ScannedFile> files = List.of(
                new ScannedFile("a/A.java", "A.java", "java", "x".repeat(50)),
                new ScannedFile("b/B.java", "B.java", "java", "y".repeat(50)),
                new ScannedFile("c/C.java", "C.java", "java", "z".repeat(50))
        );

        // Budget only large enough for roughly one file's block.
        CodeDigest digest = newBuilder(90, 6_000).build(files);

        assertThat(digest.totalFiles()).isEqualTo(3);
        assertThat(digest.filesIncluded()).isLessThan(3);
    }

    @Test
    void truncatesIndividualFilesLargerThanThePerFileCap() {
        ScannedFile bigFile = new ScannedFile("Big.java", "Big.java", "java", "x".repeat(10_000));

        CodeDigest digest = newBuilder(60_000, 100).build(List.of(bigFile));

        assertThat(digest.filesIncluded()).isEqualTo(1);
        assertThat(digest.content()).contains("... (truncated)");
        assertThat(digest.content().length()).isLessThan(10_000);
    }

}
