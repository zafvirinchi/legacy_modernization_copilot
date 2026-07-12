package com.ailegacy.modernization.copilot.domain.entities;

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
 * AI-generated performance analysis of an uploaded project, persisted in the
 * {@code performance_analysis_reports} collection.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "performance_analysis_reports")
public class PerformanceAnalysisReport {

    @Id
    private String id;

    @Indexed(unique = true)
    private String projectId;

    private int performanceScore;

    private String performanceScoreJustification;

    private List<PerformanceFinding> findings;

    private int filesAnalyzed;

    private int totalProjectFiles;

    @CreatedDate
    private Instant createdAt;

}
