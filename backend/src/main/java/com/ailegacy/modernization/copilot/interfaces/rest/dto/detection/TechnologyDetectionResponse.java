package com.ailegacy.modernization.copilot.interfaces.rest.dto.detection;

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
public class TechnologyDetectionResponse {

    private String id;
    private String projectId;
    private List<DetectedTechnologyResponse> detectedTechnologies;
    private String javaVersion;
    private List<String> databases;
    private String buildTool;
    private String applicationServer;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant createdAt;

}
