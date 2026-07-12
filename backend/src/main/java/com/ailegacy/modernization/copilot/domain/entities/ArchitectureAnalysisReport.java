package com.ailegacy.modernization.copilot.domain.entities;

import com.ailegacy.modernization.copilot.domain.enums.ArchitecturePattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

/**
 * AI-generated architecture analysis of an uploaded project, persisted in the
 * {@code architecture_analysis_reports} collection.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "architecture_analysis_reports")
public class ArchitectureAnalysisReport {

    @Id
    private String id;

    @Indexed(unique = true)
    private String projectId;

    private ArchitecturePattern detectedPattern;

    private String currentArchitectureDescription;

    private String currentArchitectureDiagram;

    private int architectureScore;

    private String architectureScoreJustification;

    private List<String> recommendations;

    private ArchitecturePattern targetArchitecturePattern;

    private String targetArchitectureDescription;

    private String migrationDiagram;

    private int filesAnalyzed;

    private int totalProjectFiles;

    @CreatedDate
    private Instant createdAt;

}
