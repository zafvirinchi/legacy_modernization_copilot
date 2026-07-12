package com.ailegacy.modernization.copilot.infrastructure.analysis;

import com.ailegacy.modernization.copilot.infrastructure.analysis.model.ScannedFile;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Detects the build tool from its descriptor file.
 *
 * Note: Gradle build files (build.gradle / build.gradle.kts) are not among the
 * project upload module's supported extensions, so they never reach disk and
 * cannot be detected here; Gradle projects will report "Unknown".
 */
@Component
public class BuildToolDetector {

    public String detect(List<ScannedFile> files) {
        boolean hasPom = files.stream().anyMatch(f -> "pom.xml".equalsIgnoreCase(f.fileName()));
        if (hasPom) {
            return "Maven";
        }

        boolean hasAntBuild = files.stream().anyMatch(f -> "build.xml".equalsIgnoreCase(f.fileName()));
        if (hasAntBuild) {
            return "Ant";
        }

        return "Unknown";
    }

}
