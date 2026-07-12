package com.ailegacy.modernization.copilot.infrastructure.ai;

import com.ailegacy.modernization.copilot.infrastructure.analysis.model.ScannedFile;
import com.ailegacy.modernization.copilot.infrastructure.ai.model.CodeDigest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

/**
 * Builds a bounded text digest of a project's extracted files for the business
 * logic analyzer's LLM prompt.
 *
 * A whole legacy codebase rarely fits in a single prompt, so files are ranked
 * by how likely they are to reveal business logic (entities and controllers
 * first, then legacy job/config files, then everything else) and appended,
 * most important first, until the character budget runs out.
 */
@Component
public class CodeDigestBuilder {

    @Value("${app.ai.max-digest-chars:60000}")
    private int maxDigestChars;

    @Value("${app.ai.max-file-chars:6000}")
    private int maxFileChars;

    public CodeDigest build(List<ScannedFile> files) {
        if (files.isEmpty()) {
            return new CodeDigest("", 0, 0);
        }

        List<ScannedFile> ordered = files.stream()
                .sorted(Comparator.comparingInt(this::priority).reversed())
                .toList();

        StringBuilder digest = new StringBuilder();
        int included = 0;

        for (ScannedFile file : ordered) {
            String snippet = file.content().length() > maxFileChars
                    ? file.content().substring(0, maxFileChars) + "\n... (truncated)"
                    : file.content();
            String block = "\n--- File: " + file.relativePath() + " ---\n" + snippet + "\n";

            if (digest.length() + block.length() > maxDigestChars) {
                break;
            }

            digest.append(block);
            included++;
        }

        return new CodeDigest(digest.toString(), included, files.size());
    }

    private int priority(ScannedFile file) {
        String content = file.content();

        if (content.contains("@Entity") || content.contains("javax.persistence") || content.contains("jakarta.persistence")) {
            return 100;
        }
        if (content.contains("@Controller") || content.contains("@RestController")
                || content.contains("extends HttpServlet") || content.contains("extends Action")) {
            return 90;
        }

        return switch (file.extension()) {
            case "xml" -> 70;
            case "cbl", "jcl" -> 65;
            case "java" -> 60;
            case "jsp" -> 50;
            case "sql" -> 40;
            case "properties", "yaml", "yml" -> 30;
            default -> 10;
        };
    }

}
