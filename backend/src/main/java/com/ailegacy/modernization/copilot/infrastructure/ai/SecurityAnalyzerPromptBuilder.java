package com.ailegacy.modernization.copilot.infrastructure.ai;

import com.ailegacy.modernization.copilot.infrastructure.ai.model.CodeDigest;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Builds the prompt sent to the LLM for security analysis, instructing it to
 * find concrete vulnerabilities and recommend modern Spring Security-based
 * fixes, all as strict JSON.
 */
@Component
public class SecurityAnalyzerPromptBuilder {

    public String build(String projectName, CodeDigest digest, List<String> knownTechnologies) {
        String technologiesSection = knownTechnologies == null || knownTechnologies.isEmpty()
                ? ""
                : "\nTechnologies already detected in this project: " + String.join(", ", knownTechnologies) + "\n";

        return """
                You are a Security Analyzer inside a legacy application modernization tool. You specialize in
                finding security vulnerabilities in legacy Java/JSP/COBOL/JCL/XML/SQL codebases and recommending
                modern Spring Security-based remediations.

                You will be given a sample of source files extracted from an uploaded legacy project named "%s".
                The sample includes %d of the project's %d files, prioritized to surface the most
                security-relevant files first (entities, controllers, legacy jobs, configuration).
                %s
                Look specifically for evidence of:
                - SQL Injection: string-concatenated or otherwise unparameterized SQL queries
                - Hardcoded Passwords: literal credentials, API keys or secrets in source or config files
                - Weak Encryption: outdated algorithms (MD5, SHA-1, DES), ECB mode, or insufficient key lengths
                - Missing Authentication: endpoints, servlets or actions with no access control
                - Session Risks: missing HttpOnly/Secure cookie flags, session fixation, unsafe session handling
                - Other OWASP Top 10 issues: e.g. XSS, insecure deserialization, broken access control, that
                  don't fit the categories above

                Only report issues you find actual evidence for in the provided files - do not invent findings.
                If nothing security-relevant is found, return an empty findings array.

                For each finding, provide:
                - "issueType": one of SQL_INJECTION, HARDCODED_PASSWORD, WEAK_ENCRYPTION, MISSING_AUTHENTICATION,
                  SESSION_RISK, OWASP_ISSUE
                - "title": a short summary of the specific issue
                - "description": what the vulnerability is and why it matters
                - "severity": one of LOW, MEDIUM, HIGH, CRITICAL
                - "riskScore": integer 0-100
                - "location": the file (and line/method if evident) where this was found, or "General" if
                  project-wide
                - "recommendation": concrete remediation advice
                - "modernAlternative": a specific modern Spring Security-based alternative or fix (e.g. which
                  Spring Security feature, annotation, or configuration to use instead)
                - "evidence": array of short strings quoting or referencing the offending code

                Respond with ONLY a single valid JSON object (no markdown code fences, no commentary before or
                after it) with EXACTLY this shape:
                { "findings": [ { ... }, ... ] }

                Project files:
                %s
                """.formatted(
                projectName, digest.filesIncluded(), digest.totalFiles(), technologiesSection, digest.content()
        );
    }

}
