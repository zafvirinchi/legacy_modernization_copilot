package com.ailegacy.modernization.copilot.infrastructure.persistence.repositories;

import com.ailegacy.modernization.copilot.domain.entities.PerformanceAnalysisReport;
import com.ailegacy.modernization.copilot.domain.repositories.PerformanceAnalysisReportRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB implementation of {@link PerformanceAnalysisReportRepository}.
 */
public interface PerformanceAnalysisReportMongoRepository extends MongoRepository<PerformanceAnalysisReport, String>, PerformanceAnalysisReportRepository {
}
