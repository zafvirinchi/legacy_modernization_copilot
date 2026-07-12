package com.ailegacy.modernization.copilot.interfaces.rest.dto.analysis;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessAnalysisResponse {

    private String id;
    private String projectId;
    private String businessPurpose;
    private List<String> mainModules;
    private List<String> criticalWorkflows;
    private List<String> coreEntities;
    private String executiveSummary;
    private String businessSummary;
    private List<ModuleSummaryResponse> moduleSummary;
    private int filesAnalyzed;
    private int totalProjectFiles;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant createdAt;

}
