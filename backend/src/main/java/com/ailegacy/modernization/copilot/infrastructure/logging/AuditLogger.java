package com.ailegacy.modernization.copilot.infrastructure.logging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Centralized logging utility for structured logging across the application.
 * 
 * Provides methods for:
 * - Business logic events
 * - Analysis progress tracking
 * - Error tracking with context
 */
@Slf4j
@Component
public class AuditLogger {

    public void logScanStarted(String scanId, String projectId, String userId) {
        log.info("Scan started | scanId={} | projectId={} | userId={}", scanId, projectId, userId);
    }

    public void logScanCompleted(String scanId, String projectId, int issueCount) {
        log.info("Scan completed | scanId={} | projectId={} | issueCount={}", scanId, projectId, issueCount);
    }

    public void logArtifactProcessed(String artifactId, String artifactType) {
        log.info("Artifact processed | artifactId={} | type={}", artifactId, artifactType);
    }

    public void logAiRecommendationGenerated(String recommendationId, String scanId) {
        log.info("AI recommendation generated | recommendationId={} | scanId={}", recommendationId, scanId);
    }

    public void logUserAction(String userId, String action, String resourceId) {
        log.info("User action | userId={} | action={} | resourceId={}", userId, action, resourceId);
    }

    public void logErrorOccurred(String context, String errorMessage, Exception ex) {
        log.error("Error occurred | context={} | message={}", context, errorMessage, ex);
    }

}
