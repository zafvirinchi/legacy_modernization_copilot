package com.ailegacy.modernization.copilot.domain.repositories;

import com.ailegacy.modernization.copilot.domain.entities.BusinessAnalysisReport;

import java.util.Optional;

/**
 * Repository contract for {@link BusinessAnalysisReport} persistence.
 */
public interface BusinessAnalysisReportRepository extends BaseRepository<BusinessAnalysisReport, String> {

    Optional<BusinessAnalysisReport> findByProjectId(String projectId);

    void deleteByProjectId(String projectId);

}
