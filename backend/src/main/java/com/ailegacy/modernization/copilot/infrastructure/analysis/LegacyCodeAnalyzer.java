package com.ailegacy.modernization.copilot.infrastructure.analysis;

/**
 * Base interface for legacy code analyzers.
 * 
 * Implementations handle:
 * - Legacy framework detection (Servlet/JSP, Struts, Spring MVC XML, COBOL, JCL)
 * - Architecture pattern identification
 * - Code quality assessment
 * - Security vulnerability detection
 * - Modernization opportunity identification
 * 
 * Examples:
 * - ServletJspAnalyzer
 * - StrutsAnalyzer
 * - JdbcAnalyzer
 * - CobolAnalyzer
 * - JclAnalyzer
 */
public interface LegacyCodeAnalyzer {

    /**
     * Analyze artifact and detect issues
     */
    void analyze(String artifactPath);

}
