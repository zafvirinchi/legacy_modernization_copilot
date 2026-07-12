package com.ailegacy.modernization.copilot.infrastructure.analysis;

import com.ailegacy.modernization.copilot.infrastructure.analysis.model.ScannedFile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Determines the project's Java version, preferring the authoritative build
 * descriptor (pom.xml) and falling back to language-syntax heuristics when
 * no build descriptor is available.
 */
@Component
public class JavaVersionDetector {

    private static final Pattern POM_VERSION_PATTERN = Pattern.compile(
            "<(?:maven\\.compiler\\.release|maven\\.compiler\\.source|maven\\.compiler\\.target|java\\.version)>\\s*(1\\.\\d|\\d{1,2})\\s*</"
    );

    public Optional<String> detect(List<ScannedFile> files) {
        for (ScannedFile file : files) {
            if (!"pom.xml".equalsIgnoreCase(file.fileName())) {
                continue;
            }
            Matcher matcher = POM_VERSION_PATTERN.matcher(file.content());
            if (matcher.find()) {
                return Optional.of(normalize(matcher.group(1)));
            }
        }

        return detectFromSyntaxHeuristics(files);
    }

    private Optional<String> detectFromSyntaxHeuristics(List<ScannedFile> files) {
        boolean hasSealed = anyJavaFileContains(files, "sealed ") && anyJavaFileContains(files, "permits ");
        if (hasSealed) {
            return Optional.of("17+");
        }
        if (anyJavaFileContains(files, "\"\"\"")) {
            return Optional.of("15+");
        }
        if (anyJavaFileMatches(files, "\\brecord\\s+\\w+\\s*\\(")) {
            return Optional.of("16+");
        }
        if (anyJavaFileMatches(files, "\\bvar\\s+\\w+\\s*=")) {
            return Optional.of("10+");
        }
        return Optional.empty();
    }

    private boolean anyJavaFileContains(List<ScannedFile> files, String needle) {
        return files.stream()
                .filter(f -> "java".equalsIgnoreCase(f.extension()))
                .anyMatch(f -> f.content().contains(needle));
    }

    private boolean anyJavaFileMatches(List<ScannedFile> files, String regex) {
        Pattern pattern = Pattern.compile(regex);
        return files.stream()
                .filter(f -> "java".equalsIgnoreCase(f.extension()))
                .anyMatch(f -> pattern.matcher(f.content()).find());
    }

    private String normalize(String rawVersion) {
        return rawVersion.startsWith("1.") ? rawVersion.substring(2) : rawVersion;
    }

}
