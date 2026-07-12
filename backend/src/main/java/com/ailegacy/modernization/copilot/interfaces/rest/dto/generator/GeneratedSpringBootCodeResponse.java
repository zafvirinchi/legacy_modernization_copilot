package com.ailegacy.modernization.copilot.interfaces.rest.dto.generator;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeneratedSpringBootCodeResponse {

    private String id;
    private String projectId;
    private String sourceServletReference;
    private String sourceJdbcReference;
    private String entityCode;
    private String repositoryCode;
    private String dtoCode;
    private String serviceCode;
    private String controllerCode;
    private String explanation;
    private int filesAnalyzed;
    private int totalProjectFiles;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant createdAt;

}
