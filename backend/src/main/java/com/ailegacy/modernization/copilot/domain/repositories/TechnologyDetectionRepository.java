package com.ailegacy.modernization.copilot.domain.repositories;

import com.ailegacy.modernization.copilot.domain.entities.TechnologyDetectionResult;

import java.util.Optional;

/**
 * Repository contract for {@link TechnologyDetectionResult} persistence.
 */
public interface TechnologyDetectionRepository extends BaseRepository<TechnologyDetectionResult, String> {

    Optional<TechnologyDetectionResult> findByProjectId(String projectId);

    void deleteByProjectId(String projectId);

}
