package com.ailegacy.modernization.copilot.infrastructure.analysis.model;

/**
 * A single extracted project file loaded for technology detection.
 */
public record ScannedFile(String relativePath, String fileName, String extension, String content) {
}
