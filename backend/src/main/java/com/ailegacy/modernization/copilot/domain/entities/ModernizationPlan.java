package com.ailegacy.modernization.copilot.domain.entities;

import com.ailegacy.modernization.copilot.domain.enums.Level;
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
 * AI-generated modernization roadmap for an uploaded project, synthesized
 * from prior technology/business/architecture/security/performance analyses
 * where available, persisted in the {@code modernization_plans} collection.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "modernization_plans")
public class ModernizationPlan {

    @Id
    private String id;

    @Indexed(unique = true)
    private String projectId;

    private String migrationStrategy;

    private String estimatedTimeline;

    private Level migrationComplexity;

    private List<PriorityMatrixItem> priorityMatrix;

    private List<String> quickWins;

    private List<Risk> risks;

    private List<RequiredTechnology> requiredTechnologies;

    private int filesAnalyzed;

    private int totalProjectFiles;

    @CreatedDate
    private Instant createdAt;

}
