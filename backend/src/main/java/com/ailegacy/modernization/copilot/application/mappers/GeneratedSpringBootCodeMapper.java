package com.ailegacy.modernization.copilot.application.mappers;

import com.ailegacy.modernization.copilot.domain.entities.GeneratedSpringBootCode;
import com.ailegacy.modernization.copilot.interfaces.rest.dto.generator.GeneratedSpringBootCodeResponse;
import org.springframework.stereotype.Component;

/**
 * Maps {@link GeneratedSpringBootCode} entities to their read-only response DTO.
 */
@Component
public class GeneratedSpringBootCodeMapper {

    public GeneratedSpringBootCodeResponse toResponse(GeneratedSpringBootCode code) {
        return GeneratedSpringBootCodeResponse.builder()
                .id(code.getId())
                .projectId(code.getProjectId())
                .sourceServletReference(code.getSourceServletReference())
                .sourceJdbcReference(code.getSourceJdbcReference())
                .entityCode(code.getEntityCode())
                .repositoryCode(code.getRepositoryCode())
                .dtoCode(code.getDtoCode())
                .serviceCode(code.getServiceCode())
                .controllerCode(code.getControllerCode())
                .explanation(code.getExplanation())
                .filesAnalyzed(code.getFilesAnalyzed())
                .totalProjectFiles(code.getTotalProjectFiles())
                .createdAt(code.getCreatedAt())
                .build();
    }

}
