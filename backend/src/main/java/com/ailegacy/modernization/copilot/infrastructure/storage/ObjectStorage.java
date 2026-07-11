package com.ailegacy.modernization.copilot.infrastructure.storage;

/**
 * Object storage interface for artifacts and reports.
 * 
 * Supports:
 * - Local file system storage
 * - AWS S3 storage
 * - Azure Blob Storage
 * - Google Cloud Storage
 * 
 * Operations:
 * - Upload artifacts
 * - Download reports
 * - Manage storage lifecycle
 */
public interface ObjectStorage {

    /**
     * Store object
     */
    String storeObject(String bucket, String key, byte[] content);

    /**
     * Retrieve object
     */
    byte[] retrieveObject(String bucket, String key);

    /**
     * Delete object
     */
    void deleteObject(String bucket, String key);

}
