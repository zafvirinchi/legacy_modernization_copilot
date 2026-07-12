package com.ailegacy.modernization.copilot.infrastructure.storage;

import com.ailegacy.modernization.copilot.domain.exceptions.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ZipProjectExtractorTest {

    @TempDir
    Path tempDir;

    private ZipProjectExtractor newExtractor() {
        return newExtractor(2000);
    }

    private ZipProjectExtractor newExtractor(long maxExtractedSizeMb) {
        return new ZipProjectExtractor(
                tempDir.toString(),
                "java,jsp,xml,properties,sql,yaml,yml,cbl,jcl",
                maxExtractedSizeMb
        );
    }

    @Test
    void extractsOnlySupportedFilesAndComputesTotals() throws IOException {
        MockMultipartFile zip = zipOf("project.zip", Map.of(
                "src/App.java", "class App {}",
                "config/settings.xml", "<settings/>",
                "README.txt", "not supported",
                "assets/logo.png", "binary"
        ));

        ExtractionResult result = newExtractor().extract(zip, "project-1");

        assertThat(result.totalFiles()).isEqualTo(2);
        assertThat(result.fileExtensionBreakdown()).containsEntry("java", 1L).containsEntry("xml", 1L);
        assertThat(result.totalSizeBytes()).isEqualTo("class App {}".length() + "<settings/>".length());
        assertThat(Files.exists(Path.of(result.storagePath(), "src", "App.java"))).isTrue();
        assertThat(Files.exists(Path.of(result.storagePath(), "README.txt"))).isFalse();
    }

    @Test
    void rejectsNonZipFiles() {
        MockMultipartFile file = new MockMultipartFile("file", "project.rar", "application/octet-stream", new byte[]{1, 2, 3});

        assertThatThrownBy(() -> newExtractor().extract(file, "project-2"))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void throwsAndCleansUpWhenNoSupportedFilesFound() throws IOException {
        MockMultipartFile zip = zipOf("project.zip", Map.of("README.txt", "hello", "logo.png", "binary"));

        assertThatThrownBy(() -> newExtractor().extract(zip, "project-3"))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("No supported files");

        assertThat(Files.exists(tempDir.resolve("projects").resolve("project-3"))).isFalse();
    }

    @Test
    void skipsZipSlipEntriesWithoutWritingOutsideTargetDirectory() throws IOException {
        MockMultipartFile zip = zipOf("project.zip", Map.of(
                "../../evil.java", "class Evil {}",
                "src/Good.java", "class Good {}"
        ));

        ExtractionResult result = newExtractor().extract(zip, "project-4");

        assertThat(result.totalFiles()).isEqualTo(1);
        assertThat(Files.exists(tempDir.resolve("evil.java"))).isFalse();
        assertThat(Files.exists(Path.of(result.storagePath(), "src", "Good.java"))).isTrue();
    }

    @Test
    void rejectsArchiveExceedingMaxExtractedSize() throws IOException {
        String largeContent = "x".repeat(2 * 1024 * 1024);
        MockMultipartFile zip = zipOf("project.zip", Map.of("Big.java", largeContent));

        assertThatThrownBy(() -> newExtractor(1).extract(zip, "project-5"))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("maximum allowed size");

        assertThat(Files.exists(tempDir.resolve("projects").resolve("project-5"))).isFalse();
    }

    private MockMultipartFile zipOf(String filename, Map<String, String> entries) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            for (Map.Entry<String, String> entry : new LinkedHashMap<>(entries).entrySet()) {
                zos.putNextEntry(new ZipEntry(entry.getKey()));
                zos.write(entry.getValue().getBytes(StandardCharsets.UTF_8));
                zos.closeEntry();
            }
        }
        return new MockMultipartFile("file", filename, "application/zip", baos.toByteArray());
    }

}
