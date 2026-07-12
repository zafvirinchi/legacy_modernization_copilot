package com.ailegacy.modernization.copilot.interfaces.rest.controllers;

import com.ailegacy.modernization.copilot.application.use_cases.detection.DetectTechnologiesUseCase;
import com.ailegacy.modernization.copilot.application.use_cases.detection.GetTechnologyDetectionUseCase;
import com.ailegacy.modernization.copilot.application.use_cases.project.GetProjectCommand;
import com.ailegacy.modernization.copilot.infrastructure.security.UserPrincipal;
import com.ailegacy.modernization.copilot.interfaces.rest.dto.ApiResponse;
import com.ailegacy.modernization.copilot.interfaces.rest.dto.detection.TechnologyDetectionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Technology detection agent endpoints.
 *
 * Detection is a standalone pipeline stage scoped to a single uploaded project;
 * it does not trigger architecture analysis or any other downstream stage.
 */
@RestController
@RequestMapping("/projects/{projectId}/technology-detection")
@RequiredArgsConstructor
@Tag(name = "Technology Detection", description = "Detect legacy technologies used by an uploaded project")
public class TechnologyDetectionController {

    private final DetectTechnologiesUseCase detectTechnologiesUseCase;
    private final GetTechnologyDetectionUseCase getTechnologyDetectionUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Run technology detection",
            description = "Scans the uploaded project's extracted files and stores a fresh technology detection result"
    )
    public ApiResponse<TechnologyDetectionResponse> run(
            @PathVariable String projectId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        TechnologyDetectionResponse response = detectTechnologiesUseCase.execute(new GetProjectCommand(projectId, principal.getId()));
        return ApiResponse.success(response, "Technology detection completed");
    }

    @GetMapping
    @Operation(summary = "Get technology detection result", description = "Returns the most recently computed detection result for this project")
    public ApiResponse<TechnologyDetectionResponse> get(
            @PathVariable String projectId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        TechnologyDetectionResponse response = getTechnologyDetectionUseCase.execute(new GetProjectCommand(projectId, principal.getId()));
        return ApiResponse.success(response);
    }

}
