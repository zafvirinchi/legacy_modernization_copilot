package com.ailegacy.modernization.copilot.infrastructure.ai.model;

/**
 * A bounded, prioritized text sample of a project's source files, built to fit
 * within the language model's context budget.
 */
public record CodeDigest(String content, int filesIncluded, int totalFiles) {
}
