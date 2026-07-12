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
 * AI-generated security analysis of an uploaded project, persisted in the
 * {@code security_analysis_reports} collection.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "security_analysis_reports")
public class SecurityAnalysisReport {

    @Id
    private String id;

    @Indexed(unique = true)
    private String projectId;

    private List<SecurityFinding> findings;

    private int overallRiskScore;

    private int filesAnalyzed;

    private int totalProjectFiles;

    @CreatedDate
    private Instant createdAt;

}
