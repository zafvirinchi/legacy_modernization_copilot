package com.ailegacy.modernization.copilot.infrastructure.analysis;

/**
 * Static analysis rule engine for detecting issues.
 * 
 * Manages:
 * - Rule registry
 * - Rule execution
 * - Issue severity scoring
 * - Issue deduplication
 */
public interface AnalysisRuleEngine {

    /**
     * Register a new analysis rule
     */
    void registerRule(String ruleName, String ruleDefinition);

    /**
     * Execute all registered rules
     */
    void executeRules();

}
