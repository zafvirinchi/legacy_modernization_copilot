package com.ailegacy.modernization.copilot.interfaces.rest.dto.security;

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
public class SecurityAnalysisResponse {

    private String id;
    private String projectId;
    private List<SecurityFindingResponse> findings;
    private int overallRiskScore;
    private int filesAnalyzed;
    private int totalProjectFiles;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant createdAt;

}
