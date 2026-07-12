package com.ailegacy.modernization.copilot.domain.repositories;

import com.ailegacy.modernization.copilot.domain.entities.ArchitectureAnalysisReport;

import java.util.Optional;

/**
 * Repository contract for {@link ArchitectureAnalysisReport} persistence.
 */
public interface ArchitectureAnalysisReportRepository extends BaseRepository<ArchitectureAnalysisReport, String> {

    Optional<ArchitectureAnalysisReport> findByProjectId(String projectId);

    void deleteByProjectId(String projectId);

}
