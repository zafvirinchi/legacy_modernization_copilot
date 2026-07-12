package com.ailegacy.modernization.copilot.infrastructure.persistence.repositories;

import com.ailegacy.modernization.copilot.domain.entities.BusinessAnalysisReport;
import com.ailegacy.modernization.copilot.domain.repositories.BusinessAnalysisReportRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB implementation of {@link BusinessAnalysisReportRepository}.
 */
public interface BusinessAnalysisReportMongoRepository extends MongoRepository<BusinessAnalysisReport, String>, BusinessAnalysisReportRepository {
}
