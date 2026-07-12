package com.ailegacy.modernization.copilot.domain.repositories;

import com.ailegacy.modernization.copilot.domain.entities.SecurityAnalysisReport;

import java.util.Optional;

/**
 * Repository contract for {@link SecurityAnalysisReport} persistence.
 */
public interface SecurityAnalysisReportRepository extends BaseRepository<SecurityAnalysisReport, String> {

    Optional<SecurityAnalysisReport> findByProjectId(String projectId);

    void deleteByProjectId(String projectId);

}
