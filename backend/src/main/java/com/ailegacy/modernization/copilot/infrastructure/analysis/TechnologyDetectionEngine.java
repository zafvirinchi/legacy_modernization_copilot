package com.ailegacy.modernization.copilot.infrastructure.analysis;

import com.ailegacy.modernization.copilot.domain.entities.DetectedTechnology;
import com.ailegacy.modernization.copilot.domain.entities.TechnologyDetectionResult;
import com.ailegacy.modernization.copilot.infrastructure.analysis.model.ScannedFile;
import com.ailegacy.modernization.copilot.infrastructure.analysis.model.TechnologyRule;
import com.ailegacy.modernization.copilot.infrastructure.analysis.model.TechnologySignal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Technology detection agent: scans an extracted project's files and reports
 * which legacy technologies it uses, with a confidence score and supporting
 * evidence for each.
 *
 * This is a single, self-contained pipeline stage - it does not trigger or feed
 * into architecture analysis.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TechnologyDetectionEngine {

    private static final int MAX_EVIDENCE_SAMPLES = 5;

    private final ProjectFileScanner fileScanner;
    private final TechnologyRuleCatalog ruleCatalog;
    private final JavaVersionDetector javaVersionDetector;
    private final DatabaseDetector databaseDetector;
    private final BuildToolDetector buildToolDetector;
    private final ApplicationServerDetector applicationServerDetector;

    public TechnologyDetectionResult detect(String projectId, String storagePath) {
        List<ScannedFile> files = fileScanner.scan(storagePath);

        List<DetectedTechnology> detectedTechnologies = ruleCatalog.rules().stream()
                .map(rule -> evaluate(rule, files))
                .filter(java.util.Objects::nonNull)
                .sorted(Comparator.comparingInt(DetectedTechnology::getConfidenceScore).reversed())
                .toList();

        TechnologyDetectionResult result = TechnologyDetectionResult.builder()
                .projectId(projectId)
                .detectedTechnologies(detectedTechnologies)
                .javaVersion(javaVersionDetector.detect(files).orElse("Unknown"))
                .databases(databaseDetector.detect(files))
                .buildTool(buildToolDetector.detect(files))
                .applicationServer(applicationServerDetector.detect(files))
                .build();

        log.info("Technology detection completed | projectId={} | technologiesFound={} | filesScanned={}",
                projectId, detectedTechnologies.size(), files.size());
        return result;
    }

    private DetectedTechnology evaluate(TechnologyRule rule, List<ScannedFile> files) {
        int distinctSignalsMatched = 0;
        int totalOccurrences = 0;
        List<String> evidence = new ArrayList<>();

        for (TechnologySignal signal : rule.signals()) {
            int occurrences = 0;
            for (ScannedFile file : files) {
                if (signal.matcher().test(file)) {
                    occurrences++;
                    if (evidence.size() < MAX_EVIDENCE_SAMPLES) {
                        evidence.add(signal.description() + " (" + file.relativePath() + ")");
                    }
                }
            }
            if (occurrences > 0) {
                distinctSignalsMatched++;
                totalOccurrences += occurrences;
            }
        }

        if (distinctSignalsMatched == 0) {
            return null;
        }

        return DetectedTechnology.builder()
                .technology(rule.technology())
                .confidenceScore(ConfidenceScorer.score(distinctSignalsMatched, totalOccurrences))
                .evidence(evidence)
                .build();
    }

}
