package com.ailegacy.modernization.copilot.interfaces.rest.dto.performance;

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
public class PerformanceAnalysisResponse {

    private String id;
    private String projectId;
    private int performanceScore;
    private String performanceScoreJustification;
    private List<PerformanceFindingResponse> findings;
    private int filesAnalyzed;
    private int totalProjectFiles;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant createdAt;

}
