package com.ailegacy.modernization.copilot.infrastructure.analysis.model;

import java.util.function.Predicate;

/**
 * A single named check for evidence of a technology within one scanned file.
 * {@code description} is surfaced back to the user as evidence when the signal matches.
 */
public record TechnologySignal(String description, Predicate<ScannedFile> matcher) {
}
