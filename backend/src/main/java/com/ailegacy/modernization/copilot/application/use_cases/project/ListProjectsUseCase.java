package com.ailegacy.modernization.copilot.application.use_cases.project;

import com.ailegacy.modernization.copilot.application.mappers.ProjectSummaryMapper;
import com.ailegacy.modernization.copilot.application.use_cases.UseCase;
import com.ailegacy.modernization.copilot.domain.repositories.ProjectRepository;
import com.ailegacy.modernization.copilot.interfaces.rest.dto.project.ProjectSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Lists the current user's uploaded projects, most recent first (project history).
 */
@Component
@RequiredArgsConstructor
public class ListProjectsUseCase implements UseCase<String, List<ProjectSummaryResponse>> {

    private final ProjectRepository projectRepository;
    private final ProjectSummaryMapper projectSummaryMapper;

    @Override
    public List<ProjectSummaryResponse> execute(String ownerId) {
        return projectRepository.findByOwnerIdOrderByCreatedAtDesc(ownerId).stream()
                .map(projectSummaryMapper::toSummaryResponse)
                .toList();
    }

}
