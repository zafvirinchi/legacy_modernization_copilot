package com.ailegacy.modernization.copilot.infrastructure.analysis.model;

import com.ailegacy.modernization.copilot.domain.enums.TechnologyType;

import java.util.List;

/**
 * The full set of signals that count as evidence for a given technology.
 */
public record TechnologyRule(TechnologyType technology, List<TechnologySignal> signals) {
}
