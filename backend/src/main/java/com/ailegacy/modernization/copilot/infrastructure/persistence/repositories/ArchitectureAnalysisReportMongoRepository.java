package com.ailegacy.modernization.copilot.infrastructure.persistence.repositories;

import com.ailegacy.modernization.copilot.domain.entities.ArchitectureAnalysisReport;
import com.ailegacy.modernization.copilot.domain.repositories.ArchitectureAnalysisReportRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB implementation of {@link ArchitectureAnalysisReportRepository}.
 */
public interface ArchitectureAnalysisReportMongoRepository extends MongoRepository<ArchitectureAnalysisReport, String>, ArchitectureAnalysisReportRepository {
}
