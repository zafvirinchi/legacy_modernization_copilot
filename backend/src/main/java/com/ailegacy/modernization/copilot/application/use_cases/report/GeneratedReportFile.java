package com.ailegacy.modernization.copilot.application.use_cases.report;

/**
 * A generated report's bytes paired with a suggested download filename.
 */
public record GeneratedReportFile(byte[] content, String suggestedFilename) {
}
