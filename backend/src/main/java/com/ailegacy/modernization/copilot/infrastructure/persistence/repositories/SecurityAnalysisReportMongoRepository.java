package com.ailegacy.modernization.copilot.infrastructure.persistence.repositories;

import com.ailegacy.modernization.copilot.domain.entities.SecurityAnalysisReport;
import com.ailegacy.modernization.copilot.domain.repositories.SecurityAnalysisReportRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB implementation of {@link SecurityAnalysisReportRepository}.
 */
public interface SecurityAnalysisReportMongoRepository extends MongoRepository<SecurityAnalysisReport, String>, SecurityAnalysisReportRepository {
}
