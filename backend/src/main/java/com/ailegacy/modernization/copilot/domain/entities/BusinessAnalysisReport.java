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
 * AI-generated explanation of an uploaded project's business logic, persisted
 * in the {@code business_analysis_reports} collection.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "business_analysis_reports")
public class BusinessAnalysisReport {

    @Id
    private String id;

    @Indexed(unique = true)
    private String projectId;

    private String businessPurpose;

    private List<String> mainModules;

    private List<String> criticalWorkflows;

    private List<String> coreEntities;

    private String executiveSummary;

    private String businessSummary;

    private List<ModuleSummary> moduleSummary;

    private int filesAnalyzed;

    private int totalProjectFiles;

    @CreatedDate
    private Instant createdAt;

}
