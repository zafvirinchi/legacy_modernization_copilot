package com.ailegacy.modernization.copilot.application.use_cases.project;

/**
 * Input for {@link GetProjectUseCase}.
 */
public record GetProjectCommand(String projectId, String ownerId) {
}
