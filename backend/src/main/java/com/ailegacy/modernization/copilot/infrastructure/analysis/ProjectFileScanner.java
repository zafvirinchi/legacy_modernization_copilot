package com.ailegacy.modernization.copilot.infrastructure.analysis;

import com.ailegacy.modernization.copilot.infrastructure.analysis.model.ScannedFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

/**
 * Reads every extracted project file into memory once, so all technology
 * detectors can scan the same in-memory snapshot instead of re-reading disk.
 */
@Slf4j
@Component
public class ProjectFileScanner {

    private static final long MAX_FILE_READ_BYTES = 2 * 1024 * 1024; // 2MB per file

    public List<ScannedFile> scan(String storagePath) {
        Path root = Path.of(storagePath);
        if (!Files.isDirectory(root)) {
            log.warn("Project storage path does not exist or is not a directory | path={}", storagePath);
            return List.of();
        }

        try (Stream<Path> walk = Files.walk(root)) {
            return walk
                    .filter(Files::isRegularFile)
                    .map(path -> toScannedFile(root, path))
                    .filter(java.util.Objects::nonNull)
                    .toList();
        } catch (IOException ex) {
            log.error("Failed to scan project files | path={}", storagePath, ex);
            return List.of();
        }
    }

    private ScannedFile toScannedFile(Path root, Path file) {
        try {
            long size = Files.size(file);
            String content = size > MAX_FILE_READ_BYTES
                    ? new String(Files.readAllBytes(file), 0, (int) MAX_FILE_READ_BYTES, StandardCharsets.UTF_8)
                    : Files.readString(file, StandardCharsets.UTF_8);

            String relativePath = root.relativize(file).toString().replace('\\', '/');
            String fileName = file.getFileName().toString();
            int dotIndex = fileName.lastIndexOf('.');
            String extension = dotIndex > 0 ? fileName.substring(dotIndex + 1).toLowerCase(Locale.ROOT) : "";

            return new ScannedFile(relativePath, fileName, extension, content);
        } catch (IOException ex) {
            log.warn("Skipping unreadable file during technology detection | file={}", file, ex);
            return null;
        }
    }

}
