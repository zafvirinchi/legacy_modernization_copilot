package com.ailegacy.modernization.copilot.infrastructure.queue;

/**
 * Job queue interface for asynchronous processing.
 * 
 * Supported operations:
 * - Queue scan jobs
 * - Queue AI analysis jobs
 * - Queue report generation jobs
 * - Monitor job status
 * - Handle job retries
 * 
 * Implementations:
 * - RabbitMQ-based
 * - Kafka-based
 * - MongoDB-based
 */
public interface JobQueue {

    /**
     * Submit a job to the queue
     */
    String submitJob(String jobType, Object payload);

    /**
     * Get job status
     */
    String getJobStatus(String jobId);

}
