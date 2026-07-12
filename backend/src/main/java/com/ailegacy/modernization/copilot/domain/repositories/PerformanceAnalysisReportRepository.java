package com.ailegacy.modernization.copilot.domain.repositories;

import com.ailegacy.modernization.copilot.domain.entities.PerformanceAnalysisReport;

import java.util.Optional;

/**
 * Repository contract for {@link PerformanceAnalysisReport} persistence.
 */
public interface PerformanceAnalysisReportRepository extends BaseRepository<PerformanceAnalysisReport, String> {

    Optional<PerformanceAnalysisReport> findByProjectId(String projectId);

    void deleteByProjectId(String projectId);

}
